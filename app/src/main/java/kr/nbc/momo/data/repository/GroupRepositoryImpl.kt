package kr.nbc.momo.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    override suspend fun createGroup(groupEntity: GroupEntity) {
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
                            .document(groupResponse.groupId)
                            .set(groupResponse)
                    }
            } else {
                // 썸네일 없을 때
                val groupResponse = groupEntity.toGroupResponse(null)
                fireStore.collection("groups")
                    .document(groupResponse.groupId)
                    .set(groupResponse)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun readGroup(groupId: String): Flow<GroupEntity> = flow {
        val snapshot = fireStore.collection("groups").document(groupId).get().await()
        val response = snapshot.toObject<GroupResponse>()
        if (response != null) {
            emit(response.toEntity())
        }
    }

    override suspend fun updateGroup(groupEntity: GroupEntity, imageUri: Uri?): Flow<GroupEntity> = callbackFlow {
        if (imageUri == null) {
            val groupResponse = groupEntity.toGroupResponse(groupEntity.groupThumbnail.toString())
            val ref = fireStore.collection("groups").document(groupResponse.groupId)
            val listener = fireStore.runTransaction { transaction ->
                transaction.update(ref, "groupName", groupResponse.groupName)
                transaction.update(ref, "groupOneLineDescription", groupResponse.groupOneLineDescription)
                transaction.update(ref, "groupDescription", groupResponse.groupDescription)
                transaction.update(ref, "firstDate", groupResponse.firstDate)
                transaction.update(ref, "lastDate", groupResponse.lastDate)
                transaction.update(ref, "categoryList", groupResponse.categoryList)
                transaction.update(ref, "groupThumbnail", groupResponse.groupThumbnail)
                trySend(groupResponse.toEntity())
            }
            awaitClose { listener.result }
        } else {
            val storageRef = storage.reference.child("groupImage").child("${groupEntity.groupId}.jpeg")
            val uploadTask = storageRef.putFile(imageUri)
            val listener = uploadTask.continueWithTask { storageRef.downloadUrl }
                .addOnCompleteListener { task ->
                    val downloadUri = task.result
                    val groupResponse = groupEntity.toGroupResponse(downloadUri.toString())
                    val ref = fireStore.collection("groups").document(groupResponse.groupId)
                    fireStore.runTransaction { transaction ->
                        transaction.update(ref, "groupName", groupResponse.groupName)
                        transaction.update(ref, "groupOneLineDescription", groupResponse.groupOneLineDescription)
                        transaction.update(ref, "groupDescription", groupResponse.groupDescription)
                        transaction.update(ref, "firstDate", groupResponse.firstDate)
                        transaction.update(ref, "lastDate", groupResponse.lastDate)
                        transaction.update(ref, "categoryList", groupResponse.categoryList)
                        transaction.update(ref, "groupThumbnail", groupResponse.groupThumbnail)
                        trySend(groupResponse.toEntity())
                    }
                }
            awaitClose { listener.result }
        }

    }

    override suspend fun addUser(userList: List<String>, groupId: String): Flow<List<String>> = callbackFlow  {
        val ref = fireStore.collection("groups").document(groupId)
        val listener = fireStore.runTransaction { transaction ->
            transaction.update(ref, "userList", userList)
            null
        }.addOnSuccessListener {
            trySend(userList)
            close()
        }
        awaitClose { listener.result }
    }

    override suspend fun deleteGroup(groupId: String): Flow<Boolean> = callbackFlow {
        val listener = fireStore.collection("groups")
            .document(groupId)
            .delete()
            .addOnSuccessListener {
                trySend(true)
            }.addOnFailureListener {
                trySend(false)
            }
        awaitClose { listener.result }

    }

    override suspend fun getGroupList(): Flow<List<GroupEntity>> = flow {
        val snapshot = fireStore.collection("groups").get().await()
        val response = snapshot.toObjects<GroupResponse>()
        emit(response.map { it.toEntity() })
    }
}
