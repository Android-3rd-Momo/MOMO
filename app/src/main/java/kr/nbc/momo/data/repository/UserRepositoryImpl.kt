package kr.nbc.momo.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.model.UserResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.data.model.toUserResponse
import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : UserRepository {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    init {
        setUserListener()
    }

    private fun setUserListener() {
        auth.currentUser?.let { it ->
            fireStore.collection("userInfo").document(it.uid)
                .addSnapshotListener { snapshot, e -> //실시간 업데이트
                    if (e != null) {
                        _currentUser.value = null
                        return@addSnapshotListener
                    }
                    snapshot?.toObject(UserResponse::class.java)?.toEntity()?.let {
                        _currentUser.value = it
                    }
                }
        }
    }

    private fun getCurrentUserUid(): String {
        return auth.currentUser?.uid ?: throw Exception("User not login")
    }

    private suspend fun saveUserInfo(user: UserEntity) {
        val currentUserUid = getCurrentUserUid()
        val userResponse = user.toUserResponse()
        fireStore.collection("userInfo").document(currentUserUid).set(userResponse).await()
//        userPreferences.saveUserInfo(user)
        _currentUser.value = user
    }

    override suspend fun signUpUser(email: String, password: String, user: UserEntity): UserEntity {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            saveUserInfo(user)
            user  //useEntity 반환
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signInUser(email: String, password: String): UserEntity {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val currentUserUid = getCurrentUserUid()
            val snapshot = fireStore.collection("userInfo").document(currentUserUid).get().await()
            val userResponse = snapshot.toObject(UserResponse::class.java) ?: throw Exception("Do not log in")
            _currentUser.value = userResponse.toEntity()
            userResponse.toEntity()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveUserProfile(user: UserEntity) { //저장
        try {
            val updateUser = user.copy(
                userProfileThumbnailUrl = uploadImageToStorage(user.userProfileThumbnailUrl, "profile"),
                userBackgroundThumbnailUrl = uploadImageToStorage(user.userBackgroundThumbnailUrl, "background"),
                userPortfolioImageUrl = uploadImageToStorage(user.userPortfolioImageUrl, "portfolio")
            )
            saveUserInfo(updateUser)
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun uploadImageToStorage(imageUrl: String, path: String): String {
        return if (imageUrl.startsWith("content://")) {
            val uri = Uri.parse(imageUrl)
            try {
                val ref = storage.reference.child("userProfile/$path/${getCurrentUserUid()}.jpeg")
                ref.putFile(uri).await()
                ref.downloadUrl.await().toString()
            } catch (e: Exception) {
                throw e
            }
        } else {
            imageUrl
        }
    }

    override suspend fun isUserIdDuplicate(userId: String): Boolean {
        return try {
            val querySnapshotId = fireStore.collection("userInfo")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            !querySnapshotId.isEmpty
        } catch (e: Exception) {
            throw e
        }
    }


    override suspend fun joinGroup(groupId: String) {
        try {
            val currentUserUid = getCurrentUserUid()
            val userSnapshot = fireStore.collection("userInfo").document(currentUserUid)
            fireStore.runTransaction { transaction ->
                val snapshot = transaction.get(userSnapshot)
                val currentGroupId = snapshot.get("userGroup") as? List<String> ?: emptyList()
                val updateUserGroup = mutableListOf<String>().apply {
                    addAll(currentGroupId)
                    add(groupId)
                }
                transaction.update(userSnapshot, "userGroup", updateUserGroup)
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    override suspend fun signOutUser() {
        try {
            signOut()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signWithdrawalUser() {
        try {
            val currentUserUid = getCurrentUserUid()
            //userGroup을 가져와서 속한 그룹을 돌면서 아이디 삭제하기
            val userSnapshot = fireStore.collection("userInfo").document(currentUserUid).get().await()
            val userGroupIdList = userSnapshot.get("userGroup") as? List<String> ?: emptyList()

            //groups에서 userId 삭제
            for(groupId in userGroupIdList){
                val groupRef = fireStore.collection("groups").document(groupId)
                fireStore.runTransaction { transaction ->
                    val groupSnapshot = transaction.get(groupRef)
                    val userList = groupSnapshot.get("userList") as? MutableList<String> ?: mutableListOf()
                    val userId = userSnapshot.getString("userId") ?: ""

                    if(userList.contains(userId)){
                        userList.remove(userId)
                        transaction.update(groupRef, "userList", userList)
                    }
                }.await()
            }

            //firestore 삭제
            fireStore.collection("userInfo").document(currentUserUid).delete().await()
            //auth 삭제
            auth.currentUser?.delete()?.await()
            signOut()

        } catch (e: Exception) {
            throw e
        }

    }

    override suspend fun reportUser(reportedUser: String): Flow<Boolean> = callbackFlow {
        val ref = fireStore.collection("blackList")
        val user = hashMapOf(
            "count" to 1
        )

        val listener = ref.document(reportedUser).update("count", FieldValue.increment(1))
            .addOnSuccessListener { trySend(true) }
            .addOnFailureListener {
                ref.document(reportedUser).set(user)
                    .addOnSuccessListener { trySend(true) }
                    .addOnFailureListener { e -> close(e) }
            }

        awaitClose()
    }

    override suspend fun blockUser(blockUser: String): Flow<Boolean> = callbackFlow {
        val currentUserUid = getCurrentUserUid()
        val ref = fireStore.collection("userInfo").document(currentUserUid)
        val listener = fireStore.runTransaction { transaction ->
            transaction.update(ref, "blackList", FieldValue.arrayUnion(blockUser))
            null
        }
            .addOnSuccessListener { trySend(true) }
            .addOnFailureListener { e -> close(e) }
        awaitClose()
    }

    override suspend fun userInfo(userId: String): Flow<UserEntity> = flow {
        val snapshot = fireStore.collection("userInfo").whereEqualTo("userId", userId).get().await()
        val response = snapshot.toObjects<UserResponse>()
        emit(response[0].toEntity())
    }

    override suspend fun isUserNumberDuplicate(userNumber: String): Boolean {
        return try {
            val querySnapshotNumber = fireStore.collection("userInfo")
                .whereEqualTo("userNumber", userNumber)
                .get()
                .await()
            !querySnapshotNumber.isEmpty
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getCurrentUser(): Flow<UserEntity?> {
        return currentUser
    }
}
