package kr.nbc.momo.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.GroupChatEntity

interface ChatRepository {
    suspend fun getGroupChatByGroupId(groupId: String): Flow<GroupChatEntity>

    // 두 함수 다 id 대신에 고유 키값 넣을 듯
    suspend fun sendChat(
        groupId: String,
        userId: String,
        text: String,
        userName: String,
        groupName: String,
        url: String
    )

    suspend fun setLastViewedChat(groupId: String, userId: String, userName: String)
}