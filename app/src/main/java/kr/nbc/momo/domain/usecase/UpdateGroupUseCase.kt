package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject
class UpdateGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    fun invoke(groupEntity: GroupEntity) {
        groupRepository.updateGroup(groupEntity)
    }
}
