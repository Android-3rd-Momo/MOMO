package kr.nbc.momo.domain.repository

import kr.nbc.momo.domain.model.GroupEntity


interface SearchRepository {
    suspend fun getSearchResult(
        classificationQuery: String,
        occupationQuery: String,
        stringQuery: String
    ): List<GroupEntity>
}