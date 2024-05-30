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
    override fun signUpUser(email: String, password: String, user: UserEntity): Flow<Result<Boolean>> = flow {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException()
            val userMap = mapOf(
                "email" to user.email,
                "name" to user.name,
                "number" to user.number
            )
            fireStore.collection("userInfo").document(uid).set(userMap).await()
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun signInUser(email: String, password: String): Flow<Result<Boolean>> = flow {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getCurrentUser(): Flow<UserEntity?> = flow {
        val currentUser = auth.currentUser //로그인 된 사용자
        if (currentUser != null) {
            val userRef = fireStore.collection("userInfo").document(currentUser.uid).get().await()
            val userResponse = userRef.toObject(UserResponse::class.java)
            val user = userResponse?.toEntity()
            emit(user)
        } else {
            emit(null)
        }
    }
}