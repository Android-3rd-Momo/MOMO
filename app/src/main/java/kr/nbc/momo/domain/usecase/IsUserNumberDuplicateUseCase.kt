package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class IsUserNumberDuplicateUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userNumber: String): Boolean {
        return userRepository.isUserNumberDuplicate(userNumber)
    }
}