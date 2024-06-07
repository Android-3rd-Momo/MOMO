package kr.nbc.momo.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
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
) : GroupRepository {
    override fun createGroup(groupEntity: GroupEntity) {
        try {
            val ref = storage.reference.child("groupImage").child("${groupEntity.groupId}.jpeg")
            if (groupEntity.groupThumbnail != null) {
                // 썸네일 있을 때
                val uploadTask = ref.putFile(Uri.parse(groupEntity.groupThumbnail))
                uploadTask.continueWithTask { ref.downloadUrl }
                    .addOnCompleteListener { task ->
                        val downloadUri = task.result
                        val groupResponse = groupEntity.toGroupResponse(downloadUri.toString())
                        fireStore.collection("groups")
                            .document(groupResponse.gorupId)
                            .set(groupResponse)
                    }
            } else {
                // 썸네일 없을 때
                val groupResponse = groupEntity.toGroupResponse(null)
                fireStore.collection("groups")
                    .document(groupResponse.gorupId)
                    .set(groupResponse)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun readGroup(groupId: String): Flow<GroupEntity> = flow {
        val snapshot = fireStore.collection("groups").document(groupId).get().await()
        val response = snapshot.toObject<GroupResponse>()
        if (response != null) {
            emit(response.toEntity())
        }
    }

    override fun updateGroup(groupEntity: GroupEntity) {
        try {
            val storageRef =
                storage.reference.child("groupImage").child("${groupEntity.groupId}.jpeg")
            val uploadTask = storageRef.putFile(Uri.parse(groupEntity.groupThumbnail))
            uploadTask.continueWithTask { storageRef.downloadUrl }
                .addOnCompleteListener { task ->
                    val downloadUri = task.result
                    val groupResponse = groupEntity.toGroupResponse(downloadUri.toString())
                    val ref = fireStore.collection("groups").document(groupResponse.gorupId)
                    fireStore.runTransaction { transaction ->
                        transaction.update(ref, "groupName", groupResponse.groupName)
                        transaction.update(ref, "groupOneLineDescription", groupResponse.groupOneLineDescription)
                        transaction.update(ref, "groupDescription", groupResponse.groupDescription)
                        transaction.update(ref, "firstDate", groupResponse.firstDate)
                        transaction.update(ref, "lastDate", groupResponse.lastDate)
                        transaction.update(ref, "categoryList", groupResponse.categoryList)
                        transaction.update(ref, "groupThumbnail", groupResponse.groupThumbnail)
                        null
                    }
                }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun addUser(userList: List<String>, groupId: String) {
        try {
            val ref = fireStore.collection("groups").document(groupId)
            fireStore.runTransaction { transaction ->
                transaction.update(ref, "userList", userList)
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun deleteGroup(groupId: String) {
        try {
            fireStore.collection("groups")
                .document(groupId)
                .delete()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getGroupList(): Flow<List<GroupEntity>> = flow {
        val snapshot = fireStore.collection("groups").get().await()
        val response = snapshot.toObjects<GroupResponse>()
        emit(response.map { it.toEntity() })
    }
}
