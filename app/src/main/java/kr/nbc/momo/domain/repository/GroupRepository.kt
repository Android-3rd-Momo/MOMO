package kr.nbc.momo.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.GroupEntity

interface GroupRepository {
    fun createGroup(groupEntity: GroupEntity)
    fun readGroup(groupId: String): Flow<GroupEntity>
    fun updateGroup(groupEntity: GroupEntity)
    fun addUser(userList: List<String>, groupId: String)
    fun deleteGroup(groupId: String)
    fun getGroupList(): Flow<List<GroupEntity>>
}