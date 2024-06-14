package kr.nbc.momo.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.model.GroupResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
) : SearchRepository {
    override suspend fun getSearchResult(
        query1: String,
        query2: String,
        query3: String
    ): List<GroupEntity> {
        val query3List = query3.replace(" ", ",").split(",")

        var query: Query = fireStore.collection("groups")

        if (query1.isNotEmpty()) {
            query = query.whereEqualTo("category.classification", query1)
        }

        if (query2.isNotEmpty()) {
            query = query.whereArrayContains("category.developmentOccupations", query2)
        }
        val storeSnapshot = query.get().await()

        var response = storeSnapshot.documents.map {
            it.toObject(GroupResponse::class.java) ?: GroupResponse()
        }
        Log.d("test", "$response + $query1 + $query2 + $query3")


        if (query3.isNotBlank()) response =
            response.filter { groupResponse ->
                groupResponse.category.programingLanguage.any {
                    it in query3List
                } || query3List.any {
                    groupResponse.groupDescription.contains(it)
                            || groupResponse.groupName.contains(it)
                }
            }
        Log.d("test", "$response + $query1 + $query2")

        return response.map { it.toEntity() }
    }

}