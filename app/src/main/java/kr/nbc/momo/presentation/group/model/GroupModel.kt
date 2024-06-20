package kr.nbc.momo.presentation.group.model

data class GroupModel(
    val groupId: String,
    val groupName: String,
    val groupOneLineDescription: String,
    val groupThumbnail: String?,
    val groupDescription: String,
    val firstDate: String,
    val lastDate: String,
    val leaderId: String,
    val category: CategoryModel,
    val userList: List<String>,
    val limitPerson: String,
    val subscriptionList: List<String>,
    val createdDate: String
)

data class CategoryModel(
    val classification: String,
    val developmentOccupations: List<String>,
    val programingLanguage: List<String>
)