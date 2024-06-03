package kr.nbc.momo.data.model

data class UserResponse(
    val userEmail: String = "",
    val userId: String = "",
    val userName: String = "",
    val userNumber: String = "",

    val userSelfIntroduction: String = "",
    val typeOfDevelopment: List<String> = emptyList(),
    val programOfDevelopment: List<String> = emptyList(),
    val stackOfDevelopment: String = "",
    val portfolio: String = "" //todo (이미지,글)
)
//todo
//포트폴리오 ...
//깃헙주소 = String

