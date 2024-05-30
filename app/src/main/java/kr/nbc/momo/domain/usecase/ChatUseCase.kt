package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.GroupChatEntity
import kr.nbc.momo.domain.repository.ChatRepository
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(groupId: String): Flow<GroupChatEntity> {
        return chatRepository.getGroupChatByGroupId(groupId)
    }

    suspend operator fun invoke(groupId: String, userId: String, text: String, userName: String) {
        chatRepository.sendChat(groupId, userId, text, userName)
    }
}