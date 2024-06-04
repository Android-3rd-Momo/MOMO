package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.repository.ChatRepository
import javax.inject.Inject

class SendChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(groupId: String, userId: String, text: String, userName: String, groupName: String) {
        chatRepository.sendChat(groupId, userId, text, userName, groupName)
    }
}