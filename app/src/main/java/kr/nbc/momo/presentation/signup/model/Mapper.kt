package kr.nbc.momo.presentation.signup.model

import kr.nbc.momo.domain.model.UserEntity

fun UserEntity.toModel(): UserModel {
    return UserModel(
        userEmail = this.userEmail,
        userName = this.userName,
        userNumber = this.userNumber,
        userId = this.userId
    )
}
fun UserModel.toEntity(): UserEntity {
    return UserEntity(
        userEmail = this.userEmail,
        userName = this.userName,
        userNumber = this.userNumber,
        userId = this.userId
    )
}