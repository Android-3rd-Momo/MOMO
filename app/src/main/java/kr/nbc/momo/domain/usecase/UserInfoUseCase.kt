package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject
class UserInfoUseCase@Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun invoke(userId: String): Flow<UserEntity> {
        return userRepository.userInfo(userId)
    }
}