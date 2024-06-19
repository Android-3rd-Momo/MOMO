package kr.nbc.momo.presentation.chatting.chattinglist.model

import kr.nbc.momo.domain.model.ChattingListEntity

fun ChattingListEntity.toModel(): ChattingListModel{
    return ChattingListModel(
        groupId = groupId,
        groupName = groupName,
        groupThumbnailUrl = groupThumbnailUrl,
        latestChatMessage = latestChatMessage,
        latestChatTimeGap = latestChatTimeGap,
        latestChatIndexGap = latestChatIndexGap
    )
}
