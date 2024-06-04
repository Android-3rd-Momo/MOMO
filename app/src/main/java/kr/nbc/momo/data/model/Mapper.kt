package kr.nbc.momo.data.model

import android.net.Uri
import kr.nbc.momo.domain.model.ChatEntity
import kr.nbc.momo.domain.model.GroupChatEntity
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.model.GroupUserEntity
import kr.nbc.momo.domain.model.UserEntity
import java.net.URI

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

fun GroupEntity.toGroupResponse(downloadUri: String): GroupResponse{
    return GroupResponse(
        groupName,
        groupOneLineDescription,
        groupThumbnail,
        downloadUri,
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
        downloadUri,
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
        userId = this.userId
    )
}