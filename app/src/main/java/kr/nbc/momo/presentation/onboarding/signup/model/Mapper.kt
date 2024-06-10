package kr.nbc.momo.presentation.onboarding.signup.model

import kr.nbc.momo.domain.model.UserEntity

fun UserEntity.toModel(): UserModel {
    return UserModel(
        userEmail = this.userEmail,
        userName = this.userName,
        userNumber = this.userNumber,
        userId = this.userId,
        userGithub = this.userGithub,
        userProfileThumbnailUrl = this.userProfileThumbnailUrl,
        userBackgroundThumbnailUrl = this.userBackgroundThumbnailUrl,
        userPortfolioImageUrl = this.userPortfolioImageUrl,
        userSelfIntroduction = this.userSelfIntroduction,
        typeOfDevelopment = this.typeOfDevelopment,
        programOfDevelopment = this.programOfDevelopment,
        stackOfDevelopment = this.stackOfDevelopment,
        userGroup = this.userGroup,
        userPortfolioText = this.userPortfolioText
    )
}
fun UserModel.toEntity(): UserEntity {
    return UserEntity(
        userEmail = this.userEmail,
        userName = this.userName,
        userNumber = this.userNumber,
        userId = this.userId,
        userGithub = this.userGithub,
        userProfileThumbnailUrl = this.userProfileThumbnailUrl,
        userBackgroundThumbnailUrl = this.userBackgroundThumbnailUrl,
        userPortfolioImageUrl = this.userPortfolioImageUrl,
        userSelfIntroduction = this.userSelfIntroduction,
        typeOfDevelopment = this.typeOfDevelopment ,
        programOfDevelopment = this.programOfDevelopment,
        stackOfDevelopment = this.stackOfDevelopment,
        userGroup = this.userGroup,
        userPortfolioText = this.userPortfolioText
    )
}