package kr.nbc.momo.data.model

data class UserResponse(
    val userEmail: String = "",
    val userId: String = "",
    val userName: String = "",
    val userNumber: String = "",
    val userGithub: String = "",
    val userProfileThumbnailUrl: String = "",
    val userBackgroundThumbnailUrl: String = "",
    val userPortfolioImageUrl: String = "",
    val userSelfIntroduction: String = "",
    val typeOfDevelopment: List<String> = emptyList(),
    val programOfDevelopment: List<String> = emptyList(),
    val stackOfDevelopment: String = "",
    val userGroup: List<String> = emptyList(),
    val userPortfolioText: String = "",
    val blackList: List<String> = emptyList()
)

