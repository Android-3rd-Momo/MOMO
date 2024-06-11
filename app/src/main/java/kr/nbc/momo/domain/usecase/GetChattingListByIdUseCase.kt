package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.model.ChattingListEntity
import kr.nbc.momo.domain.repository.ChatListRepository
import javax.inject.Inject

class GetChattingListByIdUseCase @Inject constructor(
    private val chattingListRepository: ChatListRepository
) {
    suspend operator fun invoke(string: String): ChattingListEntity{
        return chattingListRepository.getChattingListByGroupId(string)
    }
}