package kr.nbc.momo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.datastore.UserPreferences
import kr.nbc.momo.data.model.UserResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.data.model.toUserResponse
import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val userPreferences: UserPreferences
) : UserRepository {
    override suspend fun signUpUser(email: String, password: String, user: UserEntity): UserEntity {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val currentUser = auth.currentUser ?: throw Exception("SignUp Failed")
            val userResponse = user.toUserResponse()
            fireStore.collection("userInfo").document(currentUser.uid).set(userResponse).await()
            userPreferences.saveUserInfo(user) //dataStore
            userResponse.toEntity()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signInUser(email: String, password: String): UserEntity {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val currentUser = auth.currentUser ?: throw Exception("SignIn Failed")
            val snapshot = fireStore.collection("userInfo").document(currentUser.uid).get().await()
            val userResponse = snapshot.toObject(UserResponse::class.java) ?: throw Exception("Do not log in")
            userPreferences.saveUserInfo(userResponse.toEntity()) //dataStore
            userResponse.toEntity()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveUserProfile(user: UserEntity) { //저장
        try {
            val currentUser = auth.currentUser ?: throw Exception("saveProfile Failed")
            val userResponse = user.toUserResponse()
            fireStore.collection("userInfo").document(currentUser.uid).set(userResponse).await()
            userPreferences.saveUserInfo(user) //dataStore
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getUserProfile(): UserEntity? { //불러오기
        return try {
            val currentUser = auth.currentUser ?: throw Exception("getProfile Failed")
            val snapshot = fireStore.collection("userInfo").document(currentUser.uid).get().await()
            snapshot.toObject(UserResponse::class.java)?.toEntity()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun isUserIdDuplicate(userId: String): Boolean {
        return try {
            val querySnapshot = fireStore.collection("userInfo")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun joinGroup(userId: String, groupId: String) {
        try {
            val currentUser = auth.currentUser ?: throw Exception("User not logged in")
            val userSnapshot = fireStore.collection("userInfo").document(currentUser.uid)
            fireStore.runTransaction { transaction ->
                val snapshot = transaction.get(userSnapshot)
                val currentGroupIds = snapshot.get("userGroup") as? List<String> ?: emptyList()
                val updatedGroupIds = currentGroupIds + groupId
                transaction.update(userSnapshot, "userGroup", updatedGroupIds)
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getCurrentUser(): Flow<UserEntity?> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val listenerRegistration = fireStore.collection("userInfo").document(currentUser.uid)
                .addSnapshotListener { snapshot, e -> //실시간 업데이트
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }
                    val userEntity = snapshot?.toObject(UserResponse::class.java)?.toEntity()
                    trySend(userEntity).isSuccess
                }
            awaitClose { listenerRegistration.remove() }
        } else {
            trySend(null).isSuccess
            close()
        }
    }
}