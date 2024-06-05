package kr.nbc.momo.presentation.signup.model

import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.domain.model.UserEntity

fun UserEntity.toModel(): UserModel {
    return UserModel(
        userEmail = this.userEmail,
        userName = this.userName,
        userNumber = this.userNumber,
        userId = this.userId,
        userProfileThumbnailUrl = this.userProfileThumbnailUrl,
        userSelfIntroduction = this.userSelfIntroduction,
        typeOfDevelopment = this.typeOfDevelopment,
        programOfDevelopment = this.programOfDevelopment,
        stackOfDevelopment = this.stackOfDevelopment,
        portfolio = this.portfolio
    )
}
fun UserModel.toEntity(): UserEntity {
    return UserEntity(
        userEmail = this.userEmail,
        userName = this.userName,
        userNumber = this.userNumber,
        userId = this.userId,
        userProfileThumbnailUrl = this.userProfileThumbnailUrl,
        userSelfIntroduction = this.userSelfIntroduction,
        typeOfDevelopment = this.typeOfDevelopment ,
        programOfDevelopment = this.programOfDevelopment,
        stackOfDevelopment = this.stackOfDevelopment,
        portfolio = this.portfolio
    )
}