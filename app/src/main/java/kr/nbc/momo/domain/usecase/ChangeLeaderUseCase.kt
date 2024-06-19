package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject
class ChangeLeaderUseCase@Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend fun invoke(groupId: String, leaderId: String): Flow<Boolean> {
        return groupRepository.changeLeader(groupId, leaderId)
    }
}