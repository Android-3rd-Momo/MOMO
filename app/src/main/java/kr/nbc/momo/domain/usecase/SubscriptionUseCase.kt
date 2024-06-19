package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject

class SubscriptionUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend fun invoke(userId: String, groupId: String): Flow<Boolean> {
        return groupRepository.subscription(userId, groupId)
    }
}