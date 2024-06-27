package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject

class AddUserUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(userId: String, groupId: String) {
        return groupRepository.addUser(userId, groupId)
    }
}
