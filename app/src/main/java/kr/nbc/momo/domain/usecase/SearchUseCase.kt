package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.SearchRepository
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query1: String, query2: String, query3: String): List<GroupEntity> {
        return searchRepository.getSearchResult(query1, query2, query3)
    }
}
