package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject

class DeleteGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(groupId: String, userList: List<String>) {
        return groupRepository.deleteGroup(groupId, userList)
    }
}
