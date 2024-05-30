package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(email: String, password: String): Flow<Result<Boolean>> {
        return userRepository.signInUser(email, password)
    }
}