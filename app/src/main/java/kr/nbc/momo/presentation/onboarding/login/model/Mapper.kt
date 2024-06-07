package kr.nbc.momo.presentation.onboarding.login.model

import kr.nbc.momo.domain.model.SignInEntity


fun SignInEntity.toModel() : SignInModel {
    return SignInModel(
        userId = this.uId,
        userName = this.userName,
        userPassword = this.userPassword,
        userEmail = this.userEmail,
    )
}

fun SignInModel.toEntity() : SignInEntity {
    return SignInEntity(
        uId = this.userId,
        userName = this.userName,
        userPassword = this.userPassword,
        userEmail = this.userEmail,
    )
}

