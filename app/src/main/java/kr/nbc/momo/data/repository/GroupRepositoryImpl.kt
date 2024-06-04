package kr.nbc.momo.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.model.GroupResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.data.model.toGroupResponse
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage
): GroupRepository {
    override fun createGroup(groupEntity: GroupEntity, callback: (Boolean, Exception?) -> Unit) {
        var downloadUri : Uri? = null
        val refGroupImage = storage.reference.child("groupImage").child("${groupEntity.groupName}.jpeg")
        if (groupEntity.groupThumbnail != null) {
            val uploadTask = refGroupImage.putFile(Uri.parse(groupEntity.groupThumbnail))
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                refGroupImage.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    downloadUri = task.result
                    val groupResponse = groupEntity.toGroupResponse(downloadUri.toString())
                    fireStore.collection("groups").add(groupResponse)
                        .addOnSuccessListener { callback(true, null) }
                        .addOnFailureListener { e -> callback(false, e) }

                } else {
                    // Handle failures
                }
            }
        } else {
            val groupResponse = groupEntity.toGroupResponse(downloadUri.toString())
            fireStore.collection("groups").add(groupResponse)
                .addOnSuccessListener { callback(true, null) }
                .addOnFailureListener { e -> callback(false, e) }
        }
    }

    override fun readGroup(groupName: String): Flow<GroupEntity> = flow {
        val snapshot = fireStore.collection("groups").whereEqualTo("groupName", groupName).get().await()
        val response = snapshot.toObjects<GroupResponse>()
        emit(response[0].toEntity())
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
