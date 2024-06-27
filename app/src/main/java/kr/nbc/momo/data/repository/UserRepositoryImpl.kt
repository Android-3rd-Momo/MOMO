package kr.nbc.momo.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        collectUserData()
    }

    private fun collectUserData() {
        auth.currentUser?.let { it ->
            fireStore.collection("userInfo").document(it.uid)
                .addSnapshotListener { snapshot, e -> //실시간 업데이트
                    e?.let {
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
        _currentUser.value = user
    }

    override suspend fun signUpUser(email: String, password: String, user: UserEntity): UserEntity {
        auth.createUserWithEmailAndPassword(email, password).await()
        saveUserInfo(user)
        return user  //userEntity 반환
    }

    override suspend fun signInUser(email: String, password: String): UserEntity {
        auth.signInWithEmailAndPassword(email, password).await()
        val currentUserUid = getCurrentUserUid()
        val snapshot = fireStore.collection("userInfo").document(currentUserUid).get().await()
        val userResponse = snapshot.toObject(UserResponse::class.java) ?: throw Exception("Do not log in")
        _currentUser.value = userResponse.toEntity()
        return userResponse.toEntity()
    }

    override suspend fun saveUserProfile(user: UserEntity) { //저장
        val updateUser = user.copy(
            userProfileThumbnailUrl = uploadImageToStorage(
                user.userProfileThumbnailUrl,
                "profile"
            ),
            userBackgroundThumbnailUrl = uploadImageToStorage(
                user.userBackgroundThumbnailUrl,
                "background"
            ),
            userPortfolioImageUrl = uploadImageToStorage(
                user.userPortfolioImageUrl,
                "portfolio"
            )
        )
        saveUserInfo(updateUser)

    }

    private suspend fun uploadImageToStorage(imageUrl: String, path: String): String {
        return if (imageUrl.startsWith("content://")) {
            val uri = Uri.parse(imageUrl)
            val ref = storage.reference.child("userProfile/$path/${getCurrentUserUid()}.jpeg")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } else {
            imageUrl
        }
    }

    override suspend fun isUserIdDuplicate(userId: String): Boolean {
        val querySnapshotId = fireStore.collection("userInfo")
            .whereEqualTo("userId", userId)
            .get()
            .await()
        return !querySnapshotId.isEmpty
    }


    override suspend fun joinGroup(groupId: String) {
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
    }

    private fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    override suspend fun signOutUser() {
        signOut()
    }

    override suspend fun signWithdrawalUser() {
        val currentUserUid = getCurrentUserUid()
        //userGroup을 가져와서 속한 그룹을 돌면서 아이디 삭제하기
        val userSnapshot =
            fireStore.collection("userInfo").document(currentUserUid).get().await()
        val userGroupIdList = userSnapshot.get("userGroup") as? List<String> ?: emptyList()

        //groups에서 userId 삭제
        for (groupId in userGroupIdList) {
            val groupRef = fireStore.collection("groups").document(groupId)
            fireStore.runTransaction { transaction ->
                val groupSnapshot = transaction.get(groupRef)
                val userList =
                    groupSnapshot.get("userList") as? MutableList<String> ?: mutableListOf()
                val userId = userSnapshot.getString("userId") ?: ""

                if (userList.contains(userId)) {
                    userList.remove(userId)
                    transaction.update(groupRef, "userList", userList)
                }
            }.await()
        }

        fireStore.collection("userInfo").document(currentUserUid).delete().await() //firestore 삭제
        auth.currentUser?.delete()?.await() //auth 삭제
        signOut()
    }

    override suspend fun reportUser(reportedUser: String) {
        val ref = fireStore.collection("blackList")
        val user = hashMapOf("count" to 1)
        ref.document(reportedUser).update("count", FieldValue.increment(1))
        ref.document(reportedUser).set(user)
    }

    override suspend fun blockUser(blockUser: String) {
        val currentUserUid = getCurrentUserUid()
        val ref = fireStore.collection("userInfo").document(currentUserUid)
        fireStore.runTransaction { transaction ->
            transaction.update(ref, "blackList", FieldValue.arrayUnion(blockUser))
        }
    }

    override suspend fun userInfo(userId: String): Flow<UserEntity> = flow {
        val snapshot = fireStore.collection("userInfo").whereEqualTo("userId", userId).get().await()
        val response = snapshot.toObjects<UserResponse>()
        emit(response[0].toEntity())
    }

    override suspend fun isUserNumberDuplicate(userNumber: String): Boolean {
        val querySnapshotNumber = fireStore.collection("userInfo")
            .whereEqualTo("userNumber", userNumber)
            .get()
            .await()
        return !querySnapshotNumber.isEmpty
    }

    override fun getCurrentUser(): Flow<UserEntity?> {
        return currentUser
    }
}
