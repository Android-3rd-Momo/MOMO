package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject

class UpdateGroupUserListUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend fun invoke(userList : List<String>, groupId: String): Flow<List<String>> {
        return groupRepository.addUser(userList, groupId)
    }
}
