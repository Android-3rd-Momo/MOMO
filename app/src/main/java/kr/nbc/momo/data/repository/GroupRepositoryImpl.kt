package kr.nbc.momo.data.repository

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.model.GroupResponse
import kr.nbc.momo.data.model.UserResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.data.model.toGroupResponse
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import java.io.File
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage
): GroupRepository {
    override fun createGroup(groupEntity: GroupEntity, callback: (Boolean, Exception?) -> Unit) {
        val groupResponse = groupEntity.toGroupResponse()
        fireStore.collection("groups").add(groupResponse)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e) }

        storage.reference.child("${groupResponse.groupName}.jpeg").putFile(Uri.parse(groupResponse.groupThumbnail))
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    override fun readGroup(groupName: String): Flow<GroupEntity> = flow {
        val snapshot = fireStore.collection("groups").whereEqualTo("groupName", groupName).get().await()
        val response = snapshot.toObjects<GroupResponse>()
        emit(response[0].toEntity())

        storage.reference.child("${groupName}.jpeg").getFile(Uri.parse(response.get(0).groupThumbnail))
            .addOnSuccessListener { uri ->
                response[0].groupThumbnail = uri.toString()
            }.addOnFailureListener {
                // Handle any errors
            }
    }

    override fun updateGroup(groupEntity: GroupEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteGroup(groupId: String) {
        TODO("Not yet implemented")
    }

    override fun getGroupList(): Flow<List<GroupEntity>> = flow {
        val snapshot = fireStore.collection("groups").get().await()
        val response = snapshot.toObjects<GroupResponse>()
        emit(
            response.map {
                it.toEntity()
            }
        )
    }
}
