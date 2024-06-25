package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.SearchRepository
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(
        classificationQuery: String,
        occupationQuery: String,
        stringQuery: String
    ): List<GroupEntity> {
        return searchRepository.getSearchResult(classificationQuery, occupationQuery, stringQuery)
    }
}
