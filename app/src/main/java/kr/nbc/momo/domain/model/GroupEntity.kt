package kr.nbc.momo.domain.model

data class GroupEntity(
    val groupName: String = "",
    val groupOneLineDescription : String = "",
    val groupThumbnail: String = "",
    val groupDescription : String = "",
    val firstDate: String = "",
    val lastDate: String = "",
    val leaderId: String = "",
    val categoryList: List<String> = listOf(),
    val userList: List<String> = listOf(),
)
