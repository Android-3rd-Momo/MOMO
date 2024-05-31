package kr.nbc.momo.presentation.signup.model

import kr.nbc.momo.domain.model.UserEntity

fun UserEntity.toModel(): UserModel {
    return UserModel(
        email = this.email,
        name = this.name,
        number = this.number
    )
}
fun UserModel.toEntity(): UserEntity {
    return UserEntity(
        email = this.email,
        name = this.name,
        number = this.number
    )
}