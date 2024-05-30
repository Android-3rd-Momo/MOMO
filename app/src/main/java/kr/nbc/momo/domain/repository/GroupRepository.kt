package kr.nbc.momo.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.GroupChatEntity

interface GroupRepository {
    suspend fun createGroup()
    fun readGroup()
    fun updateGroup()
    fun deleteGroup()
}