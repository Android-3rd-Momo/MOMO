package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject

class DeleteGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    fun invoke(groupId: String) {
        groupRepository.deleteGroup(groupId)
    }
}
