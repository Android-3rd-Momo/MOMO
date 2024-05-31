package kr.nbc.momo.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.UserEntity

interface UserRepository {
    fun signUpUser(email: String, password: String, user: UserEntity): Flow<UserEntity>
    fun signInUser(email: String, password: String): Flow<UserEntity>
    fun getCurrentUser(): Flow<UserEntity?>
}