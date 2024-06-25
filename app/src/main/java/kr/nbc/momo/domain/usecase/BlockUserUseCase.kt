package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class BlockUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(blockUser: String) {
        return userRepository.blockUser(blockUser)
    }
}
