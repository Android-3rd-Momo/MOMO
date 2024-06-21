package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class BlockUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun invoke(blockUser: String) {
        return userRepository.blockUser(blockUser)
    }
}
