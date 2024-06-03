package kr.nbc.momo.data.model

import kr.nbc.momo.domain.model.ChatEntity
import kr.nbc.momo.domain.model.GroupChatEntity
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.model.GroupUserEntity
import kr.nbc.momo.domain.model.UserEntity

fun ChatResponse.toEntity(): ChatEntity {
    return ChatEntity(
        userName, userId, text, dateTime
    )
}

fun GroupUserResponse.toEntity(): GroupUserEntity {
    return GroupUserEntity(
        userId, userName, userProfileUrl
    )
}

fun GroupChatResponse.toEntity(): GroupChatEntity {
    return GroupChatEntity(
        this.groupId,
        this.userList.map { it.toEntity() },
        this.chatList.map { it.toEntity() }
    )
}

fun GroupEntity.toGroupResponse(): GroupResponse {
    return GroupResponse(
        groupName,
        groupOneLineDescription,
        groupThumbnail,
        groupDescription,
        firstDate,
        lastDate,
        leaderId,
        categoryList,
        userList
    )
}

fun GroupResponse.toEntity(): GroupEntity {
    return GroupEntity(
        groupName,
        groupOneLineDescription,
        groupThumbnail,
        groupDescription,
        firstDate,
        lastDate,
        leaderId,
        categoryList,
        userList
    )
}

fun UserResponse.toEntity(): UserEntity {
    return UserEntity(
        userEmail = this.userEmail,
        userName = this.userName,
        userNumber = this.userNumber,
        userId = this.userId,
        userSelfIntroduction = this.userSelfIntroduction,
        typeOfDevelopment = this.typeOfDevelopment ?: emptyList(),
        programOfDevelopment = this.programOfDevelopment ?: emptyList(),
        stackOfDevelopment = this.stackOfDevelopment,
        portfolio = this.portfolio
    )
}

fun UserEntity.toUserResponse(): UserResponse {
    return UserResponse(
        userEmail = this.userEmail,
        userName = this.userName,
        userNumber = this.userNumber,
        userId = this.userId,
        userSelfIntroduction = this.userSelfIntroduction,
        typeOfDevelopment = this.typeOfDevelopment,
        programOfDevelopment = this.programOfDevelopment,
        stackOfDevelopment = this.stackOfDevelopment,
        portfolio = this.portfolio
    )
}