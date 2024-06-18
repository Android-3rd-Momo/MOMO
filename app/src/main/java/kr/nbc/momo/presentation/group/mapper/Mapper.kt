package kr.nbc.momo.presentation.group.mapper

import kr.nbc.momo.domain.model.CategoryEntity
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.presentation.group.model.CategoryModel
import kr.nbc.momo.presentation.group.model.GroupModel

fun GroupModel.asGroupEntity(): GroupEntity {
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

fun GroupEntity.toGroupModel(): GroupModel {
    return GroupModel(
        groupId,
        groupName,
        groupOneLineDescription,
        groupThumbnail,
        groupDescription,
        firstDate,
        lastDate,
        leaderId,
        category.toModel(),
        userList,
        limitPerson,
        subscriptionList
    )
}


fun CategoryEntity.toModel(): CategoryModel {
    return CategoryModel(
        classification,
        developmentOccupations,
        programingLanguage
    )
}

fun CategoryModel.toEntity(): CategoryEntity {
    return CategoryEntity(
        classification,
        developmentOccupations,
        programingLanguage
    )
}