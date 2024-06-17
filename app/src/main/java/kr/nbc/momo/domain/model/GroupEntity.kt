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
    val category: CategoryEntity= CategoryEntity("", listOf(), listOf()),
    val userList: List<String> = listOf(),
    val limitPerson: String
//    val categoryList: List<String> = emptyList(),
//    val userList: List<String> = emptyList(),
)


data class CategoryEntity(
    val classification: String = "",
    val developmentOccupations: List<String> = listOf(),
    val programingLanguage: List<String> = listOf()
)