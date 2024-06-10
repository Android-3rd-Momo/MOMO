package kr.nbc.momo.domain.model
data class GroupEntity(
    val groupId: String = "",
    val groupName: String = "",
    val groupOneLineDescription : String = "",
    val groupThumbnail: String? = "",
    val groupDescription : String = "",
    val firstDate: String = "",
    val lastDate: String = "",
    val leaderId: String = "",
    val categoryList: List<String> = listOf(),
    val userList: List<String> = listOf(),
//    val categoryList: List<String> = emptyList(),
//    val userList: List<String> = emptyList(),
)
