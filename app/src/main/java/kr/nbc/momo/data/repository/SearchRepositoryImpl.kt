package kr.nbc.momo.data.repository

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.model.GroupResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
) : SearchRepository {
    override suspend fun getSearchResult(query: String): List<GroupEntity> {
        val resultGroupList = mutableListOf<GroupResponse>()
        val queryList = query.split(" ")
        val storeSnapshot = fireStore.collection("groups").where(
            Filter.or(
                //Filter.arrayContains("")
            )
        )



        return resultGroupList.map { it.toEntity() }
    }
}