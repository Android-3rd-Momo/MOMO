package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject

class GetNotificationCountUseCase@Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend fun invoke(userId: String): Flow<Int> {
        return groupRepository.getNotificationCount(userId)
    }
}