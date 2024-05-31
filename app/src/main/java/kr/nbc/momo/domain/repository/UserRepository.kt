package kr.nbc.momo.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.UserEntity

interface UserRepository {
    suspend fun signUpUser(email: String, password: String, user: UserEntity): UserEntity
    suspend fun signInUser(email: String, password: String): UserEntity
    suspend fun isUserIdDuplicate(userId: String): Boolean //아이디 중복 확인
    fun getCurrentUser(): Flow<UserEntity?>
}