package kr.nbc.momo.domain.model

data class GroupChatEntity(
    val groupId: String = "",
    val userList: List<GroupUserEntity> = listOf(),
    val chatList: List<ChatEntity> = listOf()
)

data class GroupUserEntity(
    val userId: String = "",
    val userName: String = "",
    val userProfileUrl: String = ""
)

data class ChatEntity(
    val userName: String = "",
    val userId: String = "",
    val text: String = "",
    val dateTime: String = ""
)