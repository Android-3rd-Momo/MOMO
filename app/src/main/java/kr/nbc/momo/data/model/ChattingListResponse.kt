package kr.nbc.momo.data.model

data class ChattingListResponse(
    val groupName: String = "",
    val groupId: String = "",
    val groupThumbnailUrl: String = "",
    val latestChatMessage: String = "",
    val latestChatTimeGap: String = "",
)