package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject

class ChangeLeaderUseCase@Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(groupId: String, leaderId: String) {
        return groupRepository.changeLeader(groupId, leaderId)
    }
}