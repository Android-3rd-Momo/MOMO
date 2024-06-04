package kr.nbc.momo.domain.model

data class UserEntity(
    val userEmail: String = "",
    val userName: String = "",
    val userNumber: String = "",
    val userId: String = "",

    val userSelfIntroduction: String = "",
    val typeOfDevelopment: List<String> = emptyList(),
    val programOfDevelopment: List<String> = emptyList(),
    val stackOfDevelopment: String = "",
    val portfolio: String = "" //todo 포트폴리오
)

