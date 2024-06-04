package kr.nbc.momo.presentation.chatting.chattinglist.model

data class ChattingListModel(
    val groupName: String = "",
    val groupId: String = "",
    val groupThumbnailUrl: String = "",
    val latestChatMessage: String = "",
    val latestChatTimeGap: String = "",
)