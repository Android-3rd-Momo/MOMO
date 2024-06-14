package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject
class SearchLeaderUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend fun invoke(userId: String): Flow<List<String>> {
        return groupRepository.searchLeader(userId)
    }
}
