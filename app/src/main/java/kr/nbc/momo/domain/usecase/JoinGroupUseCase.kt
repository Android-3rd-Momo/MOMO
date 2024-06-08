package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class JoinGroupUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun invoke(groupId: String) {
        userRepository.joinGroup(groupId)
    }
}