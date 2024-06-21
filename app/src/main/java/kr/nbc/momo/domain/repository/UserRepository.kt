package kr.nbc.momo.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.UserEntity

interface UserRepository {
    suspend fun signUpUser(email: String, password: String, user: UserEntity): UserEntity
    suspend fun signInUser(email: String, password: String): UserEntity
    fun getCurrentUser(): Flow<UserEntity?>
    suspend fun saveUserProfile(user: UserEntity)
    suspend fun isUserIdDuplicate(userId: String): Boolean
    suspend fun isUserNumberDuplicate(userNumber:String):Boolean
    suspend fun joinGroup(groupId: String)
    suspend fun signOutUser()
    suspend fun signWithdrawalUser()
    suspend fun reportUser(reportedUser: String)
    suspend fun blockUser(blockUser: String)
    suspend fun userInfo(userId: String): Flow<UserEntity>
}