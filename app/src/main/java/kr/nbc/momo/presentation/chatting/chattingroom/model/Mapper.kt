package kr.nbc.momo.presentation.chatting.chattingroom.model

import kr.nbc.momo.domain.model.ChatEntity
import kr.nbc.momo.domain.model.GroupChatEntity
import kr.nbc.momo.domain.model.GroupUserEntity

fun GroupUserEntity.toModel() = GroupUserModel(userId, userName, userProfileUrl)

fun ChatEntity.toModel() = ChatModel(userName, userId, text, dateTime)

fun GroupChatEntity.toModel() = GroupChatModel(
    groupId = groupId,
    userList = userList.map { it.toModel() },
    chatList = chatList.map { it.toModel() }
)