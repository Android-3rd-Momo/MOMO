package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class SignWithdrawalUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(){
        userRepository.signWithdrawalUser()
    }
}