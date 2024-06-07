package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.model.SignInEntity
import kr.nbc.momo.domain.repository.SignInRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val signInRepository: SignInRepository) {
    suspend operator fun invoke(email: String, password: String): SignInEntity {
        return signInRepository.login(email, password)
    }
}