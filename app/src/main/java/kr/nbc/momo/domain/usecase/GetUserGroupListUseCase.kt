package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject
class GetUserGroupListUseCase@Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(groupList: List<String>, userId: String): Flow<List<GroupEntity>> {
        return groupRepository.getUserGroupList(groupList, userId)
    }
}