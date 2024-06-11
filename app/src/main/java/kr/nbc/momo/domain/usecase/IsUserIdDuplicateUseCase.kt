package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class IsUserIdDuplicateUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Boolean {
        return userRepository.isUserIdDuplicate(userId)
    }
}