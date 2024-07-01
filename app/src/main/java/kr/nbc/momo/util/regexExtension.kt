package kr.nbc.momo.util


fun String.isValidName(): Boolean {
    val usernamePattern = "^[A-Za-z가-힣]{3,20}$"
    return this.matches(usernamePattern.toRegex())
}
fun String.isValidEmail(): Boolean {
    val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    return this.matches(emailPattern.toRegex())
}

fun String.isValidId(): Boolean {
    val usernamePattern = "^[A-Za-z가-힣0-9]{3,20}$"
    return this.matches(usernamePattern.toRegex())
}

fun String.isValidPassword(): Boolean { //8~20, 영문 + 숫자
    val passwordPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{8,20}$"
    return this.matches(passwordPattern.toRegex())
}

fun String.isValidPhoneNumber(): Boolean {
    val phoneNumberPattern = "^\\d{3}-\\d{4}-\\d{4}$"
    return this.matches(phoneNumberPattern.toRegex())
}