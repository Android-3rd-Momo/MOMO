package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject
class UpdateGroupUserListUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    fun invoke(userList : List<String>, groupId: String) {
        groupRepository.addUser(userList, groupId)
    }
}
