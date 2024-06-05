package kr.nbc.momo.domain.usecase

import android.net.Uri
import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserEntity ){
//    suspend operator fun invoke(user: UserEntity, imageUriMap: Map<String, Uri> ){
        userRepository.saveUserProfile(user)
    }
}