package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.repository.ChatRepository
import javax.inject.Inject

class SetLastViewedChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(groupId: String, userId: String, userName: String) {
        return chatRepository.setLastViewedChat(groupId, userId, userName)
    }
}