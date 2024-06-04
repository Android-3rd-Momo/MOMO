package kr.nbc.momo.presentation.chatting.chattingroom.model

data class GroupChatModel(
    val groupId: String = "",
    val groupName: String = "",
    val userList: List<GroupUserModel> = listOf(),
    val chatList: List<ChatModel> = listOf()
)

data class GroupUserModel(
    val userId: String = "",
    val userName: String = "",
    val userProfileUrl: String = ""
)

data class ChatModel(
    val userName: String = "",
    val userId: String = "",
    val text: String = "",
    val dateTime: String = ""
)