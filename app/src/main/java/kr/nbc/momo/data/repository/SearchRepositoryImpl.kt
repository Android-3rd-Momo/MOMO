package kr.nbc.momo.data.repository

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
    override suspend fun getSearchResult(query1: String, query2: String, query3: String): List<GroupEntity> {
        val query3List = query3.replace(" ", ",").split(",")
        val storeSnapshot = fireStore.collection("groups")
            .whereArrayContains("category", query1)
            .whereArrayContains("works", query2)
            .whereArrayContainsAny("language", query3List).get().await()

        val response = storeSnapshot.documents.map { it.toObject(GroupResponse::class.java) }

        return response.map { it?.toEntity()?:GroupEntity() }
    }

}