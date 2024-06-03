package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun invoke(user: UserEntity){
        userRepository.saveUserProfile(user)
    }
}