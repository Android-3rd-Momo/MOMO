package kr.nbc.momo.domain.repository

import kr.nbc.momo.domain.model.ChattingListEntity

interface ChatListRepository {
    suspend fun getChattingListByGroupIdList(list: List<String>): List<ChattingListEntity>
    suspend fun getChattingListByGroupId(string: String): ChattingListEntity

}