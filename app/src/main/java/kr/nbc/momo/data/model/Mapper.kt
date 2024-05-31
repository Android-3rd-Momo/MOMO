package kr.nbc.momo.data.model

import kr.nbc.momo.domain.model.ChatEntity
import kr.nbc.momo.domain.model.GroupChatEntity
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.model.GroupUserEntity

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

fun GroupEntity.toGroupResponse(): GroupResponse{
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

fun GroupResponse.toEntity(): GroupEntity{
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
