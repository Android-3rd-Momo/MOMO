package kr.nbc.momo.domain.model

data class SignInEntity (
    val uId : String,
    val userPassword: String,
    val userName: String,
    val userEmail:String
)