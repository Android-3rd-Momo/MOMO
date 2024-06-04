package kr.nbc.momo.data.model
data class GroupResponse(
    val groupName: String = "",
    val groupOneLineDescription : String = "",
    var groupThumbnail: String = "",
    val groupDescription : String = "",
    val firstDate: String = "",
    val lastDate: String = "",
    val leaderId: String = "",
    val categoryList: List<String> = listOf(),
    val userList: List<String> = listOf(),
)