package kr.nbc.momo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
): UserRepository{
    override fun signUpUser(email: String, password: String, user: UserEntity): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun signInUser(email: String, password: String): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): Flow<UserEntity?> {
        TODO("Not yet implemented")
    }
}