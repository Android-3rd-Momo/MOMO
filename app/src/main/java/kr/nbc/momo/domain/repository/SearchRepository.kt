package kr.nbc.momo.domain.repository

import kr.nbc.momo.domain.model.GroupEntity


interface SearchRepository {
    suspend fun getSearchResult(query1: String, query2: String, query3: String): List<GroupEntity>
}