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
        classificationQuery: String,
        occupationQuery: String,
        stringQuery: String
    ): List<GroupEntity> {
        val stringQueryList = stringQuery.replace(" ", ",").split(",")

        var query: Query = fireStore.collection("groups")

        if (classificationQuery.isNotEmpty()) {
            query = query.whereEqualTo("category.classification", classificationQuery)
        }

        if (occupationQuery.isNotEmpty()) {
            query = query.whereArrayContains("category.developmentOccupations", occupationQuery)
        }
        val storeSnapshot = query.get().await()

        var response = storeSnapshot.documents.map {
            it.toObject(GroupResponse::class.java) ?: GroupResponse()
        }
        Log.d("test", "$response + $classificationQuery + $occupationQuery + $stringQuery")


        if (stringQuery.isNotBlank()) response =
            response.filter { groupResponse ->
                groupResponse.category.programingLanguage.any {
                    it in stringQueryList
                } || stringQueryList.any {
                    groupResponse.groupDescription.contains(it)
                            || groupResponse.groupName.contains(it)
                }
            }
        Log.d("test", "$response + $classificationQuery + $occupationQuery")

        return response.map { it.toEntity() }
    }

}