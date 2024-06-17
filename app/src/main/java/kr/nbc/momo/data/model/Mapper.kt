package kr.nbc.momo.data.model

import kr.nbc.momo.domain.model.CategoryEntity
import kr.nbc.momo.domain.model.ChatEntity
import kr.nbc.momo.domain.model.ChattingListEntity
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
        userId, userName, userProfileUrl, lastViewedChat.toEntity()
    )
}

fun GroupChatResponse.toEntity(): GroupChatEntity {
    return GroupChatEntity(
        groupId,
        groupName,
        userList.map { it.toEntity() },
        chatList.map { it.toEntity() }
    )
}


fun GroupEntity.toGroupResponse(downloadUri: String?): GroupResponse{
    return GroupResponse(
        groupId,
        groupName,
        groupOneLineDescription,
        downloadUri,
        groupDescription,
        firstDate,
        lastDate,
        leaderId,
        category.toResponse(),
        userList,
        limitPerson
    )
}

fun CategoryEntity.toResponse(): CategoryResponse{
    return CategoryResponse(
        classification,
        developmentOccupations,
        programingLanguage
    )
}


fun CategoryResponse.toEntity(): CategoryEntity{
    return CategoryEntity(
        classification,
        developmentOccupations,
        programingLanguage
    )
}

fun GroupResponse.toEntity(): GroupEntity {
    return GroupEntity(
        groupId,
        groupName,
        groupOneLineDescription,
        groupThumbnail,
        groupDescription,
        firstDate,
        lastDate,
        leaderId,
        category.toEntity(),
        userList,
        limitPerson
    )
}

fun UserResponse.toEntity(): UserEntity {
    return UserEntity(
        userEmail = this.userEmail,
        userName = this.userName,
        userNumber = this.userNumber,
        userId = this.userId,
        userProfileThumbnailUrl = this.userProfileThumbnailUrl,
        userBackgroundThumbnailUrl = this.userBackgroundThumbnailUrl,
        userPortfolioImageUrl = this.userPortfolioImageUrl,
        userSelfIntroduction = this.userSelfIntroduction,
        typeOfDevelopment = this.typeOfDevelopment,
        programOfDevelopment = this.programOfDevelopment,
        stackOfDevelopment = this.stackOfDevelopment,
        userGroup = this.userGroup,
        userPortfolioText = this.userPortfolioText,
        blackList = this.blackList
    )
}

fun UserEntity.toUserResponse(): UserResponse {
    return UserResponse(
        userEmail = this.userEmail,
        userName = this.userName,
        userNumber = this.userNumber,
        userId = this.userId,
        userProfileThumbnailUrl = this.userProfileThumbnailUrl,
        userBackgroundThumbnailUrl = this.userBackgroundThumbnailUrl,
        userPortfolioImageUrl = this.userPortfolioImageUrl,
        userSelfIntroduction = this.userSelfIntroduction,
        typeOfDevelopment = this.typeOfDevelopment,
        programOfDevelopment = this.programOfDevelopment,
        stackOfDevelopment = this.stackOfDevelopment,
        userGroup = this.userGroup,
        userPortfolioText = this.userPortfolioText,
        blackList = this.blackList
    )
}

fun ChattingListResponse.toEntity(): ChattingListEntity {
    return ChattingListEntity(
        groupName,
        groupId,
        groupThumbnailUrl,
        latestChatMessage,
        latestChatTimeGap,
        latestChatIndexGap
    )
}