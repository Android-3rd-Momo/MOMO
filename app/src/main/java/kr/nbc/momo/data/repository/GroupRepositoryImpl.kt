package kr.nbc.momo.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kr.nbc.momo.data.model.GroupResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.data.model.toGroupResponse
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
): GroupRepository {
    private val groupResponse = MutableStateFlow<GroupResponse>(GroupResponse())
    private val groupListResponse = MutableStateFlow<List<GroupResponse>>(listOf())
    override fun createGroup(groupEntity: GroupEntity, callback: (Boolean, Exception?) -> Unit) {
        val groupResponse = groupEntity.toGroupResponse()
        fireStore.collection("groups").add(groupResponse)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e) }
    }

    override fun readGroup(groupId: String): Flow<GroupEntity> {
        fireStore.collection("groups").document(groupId).get()
            .addOnSuccessListener { documentSnapshot  ->
                if (documentSnapshot.toObject<GroupResponse>() == null) {
                    Log.d("ReadGroupSuccess", "No such document")

                } else {
                    groupResponse.value = documentSnapshot.toObject<GroupResponse>()!!
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        return groupResponse.map { data ->
            data.toEntity()
        }
    }

    override fun updateGroup(groupEntity: GroupEntity) {

        TODO("Not yet implemented")
    }

    override fun deleteGroup(groupId: String) {
        TODO("Not yet implemented")
    }

    override fun getGroupList(): Flow<List<GroupEntity>> {
        fireStore.collection("groups")
            // .whereEqualTo("groupName", "123")
            .get()
            .addOnSuccessListener { documents ->
                groupListResponse.value = documents.toObjects<GroupResponse>()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        return groupListResponse.map { list ->
            list.map { data ->
                data.toEntity()
            }
        }
    }
}
