package kr.nbc.momo.domain.repository

import kr.nbc.momo.domain.model.GroupEntity


interface SearchRepository {
    suspend fun getSearchResult(query: String): List<GroupEntity>
}