package kr.nbc.momo.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kr.nbc.momo.data.model.GroupResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.data.model.toGroupResponse
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import kr.nbc.momo.domain.usecase.CreateGroupUseCase
import kr.nbc.momo.presentation.group.mapper.asGroupEntity
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
): GroupRepository {
    private val groupResponse = MutableStateFlow<GroupResponse>(GroupResponse())

    override fun createGroup(groupEntity: GroupEntity, callback: (Boolean, Exception?) -> Unit) {
        val groupResponse = groupEntity.toGroupResponse()
        val group = hashMapOf(
            "groupName" to groupResponse.groupName,
            "groupOneLineDescription" to groupResponse.groupOneLineDescription,
            "groupThumbnail" to groupResponse.groupThumbnail,
            "groupDescription" to groupResponse.groupDescription,
            "firstDate" to groupResponse.firstDate,
            "lastDate" to groupResponse.lastDate,
            "leaderId" to groupResponse.leaderId,
            "category" to groupResponse.categoryList,
            "userList" to groupResponse.userList
        )
        fireStore.collection("groups").add(group)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e) }
    }

    override fun readGroup(groupId: String): Flow<GroupEntity> {
        fireStore.collection("groups").document(groupId).get()
            .addOnSuccessListener { documentSnapshot  ->
                if (documentSnapshot != null) {
                    groupResponse.value = documentSnapshot.toObject<GroupResponse>()!!

                } else {
                    Log.d("ReadGroupSuccess", "No such document")
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
}