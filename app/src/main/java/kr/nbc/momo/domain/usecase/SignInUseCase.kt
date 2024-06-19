package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): UserEntity  {
        return userRepository.signInUser(email, password)
    }

}