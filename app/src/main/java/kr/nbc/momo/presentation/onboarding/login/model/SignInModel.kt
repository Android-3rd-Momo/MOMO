package kr.nbc.momo.presentation.onboarding.login.model

data class SignInModel (
    val userId : String,
    val userPassword: String,
    val userName: String,
    val userEmail:String,

    val isFirstLaunch: Boolean = true,
    val isLoggedIn: Boolean = false
)