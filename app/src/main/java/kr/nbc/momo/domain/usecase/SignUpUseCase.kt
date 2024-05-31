package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String, user: UserEntity):UserEntity {
        if (!userRepository.isUserIdDuplicate(user.userId)) {
            throw Exception("User ID is already in use.")
        }
        return userRepository.signUpUser(email, password, user)
    }
}