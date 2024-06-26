package kr.nbc.momo.data.model

data class GroupChatResponse(
    val groupId: String = "",
    val groupName: String = "",
    val userList: List<GroupUserResponse> = listOf(),
    val chatList: List<ChatResponse> = listOf()
)

data class GroupUserResponse(
    val userId: String = "",
    val userName: String = "",
    val userProfileUrl: String = "",
    val lastViewedChat: ChatResponse = ChatResponse()
)

data class ChatResponse(
    val userName: String = "",
    val userId: String = "",
    val text: String = "",
    val dateTime: String = ""
)