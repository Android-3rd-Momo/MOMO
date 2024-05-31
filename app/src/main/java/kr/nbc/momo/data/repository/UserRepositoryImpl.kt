package kr.nbc.momo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.di.UserModule
import kr.nbc.momo.data.model.UserResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : UserRepository {
    override suspend fun signUpUser(email: String, password: String, user: UserEntity): UserEntity {
        if (!isUserIdDuplicate(user.userId)) {
            throw Exception("User ID is already taken.")
        }
        auth.createUserWithEmailAndPassword(email, password).await()
        val currentUser = auth.currentUser ?: throw Exception("SignUp Failed")
        val userResponse = UserResponse(
            userEmail = user.userEmail,
            userId = user.userId, // 사용자가 입력한 userId
            userName = user.userName,
            userNumber = user.userNumber
        )
        fireStore.collection("userInfo").document(currentUser.uid).set(userResponse).await()
        return userResponse.toEntity()
    }

    override suspend fun signInUser(email: String, password: String): UserEntity {
        auth.signInWithEmailAndPassword(email, password).await()
        val currentUser = auth.currentUser ?: throw Exception("SignIn Failed")
        val snapshot = fireStore.collection("userInfo").document(currentUser.uid).get().await()
        val userResponse = snapshot.toObject(UserResponse::class.java) ?: throw Exception("User not found")
        return userResponse.toEntity()
    }

    override suspend fun isUserIdDuplicate(userId: String): Boolean {
        val querySnapshot = fireStore.collection("userInfo")
            .whereEqualTo("userId", userId) //query문
            .get()
            .await()
        return querySnapshot.isEmpty
    }

    override fun getCurrentUser(): Flow<UserEntity?> = flow {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val snapshot = fireStore.collection("userInfo").document(currentUser.uid).get().await()
            val userResponse = snapshot.toObject(UserResponse::class.java)
            emit(userResponse?.toEntity())
        } else {
            emit(null)
        }
    }
}