package kr.nbc.momo.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.GroupEntity

interface GroupRepository {
    fun createGroup(groupEntity: GroupEntity, callback: (Boolean, Exception?) -> Unit)
    fun readGroup(groupId: String): Flow<GroupEntity>
    fun updateGroup(groupEntity: GroupEntity)
    fun deleteGroup(groupId: String)
}