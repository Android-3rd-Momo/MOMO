package kr.nbc.momo.data.model

import kr.nbc.momo.domain.model.CategoryEntity

data class GroupResponse(
    val groupId: String = "",
    val groupName: String = "",
    val groupOneLineDescription : String = "",
    val groupThumbnail: String? = "",
    val groupDescription : String = "",
    val firstDate: String = "",
    val lastDate: String = "",
    val leaderId: String = "",
    val category: CategoryResponse = CategoryResponse("", listOf(), listOf()),
    val userList: List<String> = listOf(),
)

data class CategoryResponse(
    val classification: String = "",
    val developmentOccupations: List<String> = listOf(),
    val programingLanguage: List<String> = listOf()
)