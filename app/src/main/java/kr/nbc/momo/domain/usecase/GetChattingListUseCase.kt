package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.model.ChattingListEntity
import kr.nbc.momo.domain.repository.ChatListRepository
import javax.inject.Inject

class GetChattingListUseCase @Inject constructor(
    private val chattingListRepository: ChatListRepository
) {
    suspend operator fun invoke(list: List<String>): List<ChattingListEntity> {
        return chattingListRepository.getChattingListByGroupIdList(list)
    }
}