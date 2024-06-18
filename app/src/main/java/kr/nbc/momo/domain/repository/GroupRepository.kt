package kr.nbc.momo.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.GroupEntity

interface GroupRepository {
    suspend fun createGroup(groupEntity: GroupEntity): Flow<Boolean>
    suspend fun readGroup(groupId: String): Flow<GroupEntity>
    suspend fun updateGroup(groupEntity: GroupEntity, imageUri: Uri?): Flow<GroupEntity>
    suspend fun addUser(userList: List<String>, groupId: String): Flow<List<String>>
    suspend fun deleteGroup(groupId: String, userList: List<String>): Flow<Boolean>
    suspend fun getGroupList(): Flow<List<GroupEntity>>
    suspend fun changeLeader(groupId: String, leaderId: String): Flow<Boolean>
    suspend fun searchLeader(userId: String): Flow<List<String>>
    suspend fun deleteUser(userId: String, groupId: String): Flow<List<String>>
}