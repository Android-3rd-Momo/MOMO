package kr.nbc.momo.data.model

import kr.nbc.momo.domain.model.ChatEntity
import kr.nbc.momo.domain.model.GroupChatEntity
import kr.nbc.momo.domain.model.GroupUserEntity
import kr.nbc.momo.domain.model.UserEntity

fun ChatResponse.toEntity(): ChatEntity{
    return ChatEntity(
        userName, userId, text, dateTime
    )
}

fun GroupUserResponse.toEntity(): GroupUserEntity{
    return GroupUserEntity(
        userId, userName, userProfileUrl
    )
}

fun GroupChatResponse.toEntity(): GroupChatEntity{
    return GroupChatEntity(
        this.groupId,
        this.userList.map { it.toEntity() },
        this.chatList.map { it.toEntity() }
    )
}

fun UserResponse.toEntity(): UserEntity{
    return UserEntity(
        email = this.email,
        name = this.name,
        number = this.number
    )
}
