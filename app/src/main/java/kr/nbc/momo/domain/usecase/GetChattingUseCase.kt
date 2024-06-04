package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.GroupChatEntity
import kr.nbc.momo.domain.repository.ChatRepository
import javax.inject.Inject

class GetChattingUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(groupId: String): Flow<GroupChatEntity> {
        return chatRepository.getGroupChatByGroupId(groupId)
    }
}