package kr.nbc.momo.presentation.group.model

data class GroupModel(
    val groupName: String,
    val groupOneLineDescription: String,
    val groupThumbnail: String,
    val groupDescription: String,
    val firstDate: String,
    val lastDate: String,
    val leaderId: String,
    val categoryList: List<String>,
    val userList: List<String>
)