package kr.nbc.momo.presentation.group.mapper

import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.presentation.group.model.GroupModel

fun GroupModel.asGroupEntity(): GroupEntity {
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

fun GroupEntity.toGroupModel(): GroupModel {
    return GroupModel(
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