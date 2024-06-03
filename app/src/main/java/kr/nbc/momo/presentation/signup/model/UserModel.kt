package kr.nbc.momo.presentation.signup.model

data class UserModel(
    val userEmail: String = "",
    val userName: String = "",
    val userNumber: String = "",
    val userId: String = "",

    val userSelfIntroduction: String = "",
    val typeOfDevelopment: List<String> = emptyList(),
    val programOfDevelopment: List<String> = emptyList(),
    val stackOfDevelopment: String = "",
    val portfolio: String = "" //todo
)
//온보딩 정보 추가
