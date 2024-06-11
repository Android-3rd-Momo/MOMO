package kr.nbc.momo.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kr.nbc.momo.domain.model.GroupEntity

interface GroupRepository {
    suspend fun createGroup(groupEntity: GroupEntity)
    suspend fun readGroup(groupId: String): Flow<GroupEntity>
    suspend fun updateGroup(groupEntity: GroupEntity, imageUri: Uri?): Flow<GroupEntity>
    suspend fun addUser(userList: List<String>, groupId: String): Flow<List<String>>
    suspend fun deleteGroup(groupId: String): Flow<Boolean>
    suspend fun getGroupList(): Flow<List<GroupEntity>>
}