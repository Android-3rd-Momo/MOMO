package kr.nbc.momo.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.UserEntity

interface UserRepository {
    fun signUpUser(email: String, password: String, user: UserEntity): Flow<Result<Boolean>>
    fun signInUser(email: String, password: String): Flow<Result<Boolean>>
    fun getCurrentUser(): Flow<UserEntity?>
}