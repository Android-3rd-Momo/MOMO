package kr.nbc.momo.domain.model

data class ChattingListEntity(
    val groupName: String = "",
    val groupId: String = "",
    val groupThumbnailUrl: String? = "",
    val latestChatMessage: String = "",
    val latestChatTimeGap: String = "",
)