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
    override fun signUpUser(email: String, password: String, user: UserEntity): Flow<UserEntity> = flow {
        auth.createUserWithEmailAndPassword(email, password).await()
        val currentUser = auth.currentUser ?: throw Exception("User creation failed")
        val userRef = fireStore.collection("userInfo").document(currentUser.uid)
        userRef.set(user).await()
        emit(user)
    }

    override fun signInUser(email: String, password: String): Flow<UserEntity> = flow {
        auth.signInWithEmailAndPassword(email, password).await()
        val currentUser = auth.currentUser ?: throw Exception("Authentication failed")
        val userRef = fireStore.collection("userInfo").document(currentUser.uid).get().await()
        val user = userRef.toObject(UserEntity::class.java) ?: throw Exception("User not found")
        emit(user)
    }

    override fun getCurrentUser(): Flow<UserEntity?> = flow {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = fireStore.collection("userInfo").document(currentUser.uid).get().await()
            emit(userRef.toObject(UserEntity::class.java))
        } else {
            emit(null)
        }
    }
}