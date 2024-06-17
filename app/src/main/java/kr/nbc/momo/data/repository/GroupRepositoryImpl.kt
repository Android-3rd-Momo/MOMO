package kr.nbc.momo.data.repository

import android.net.Uri
import android.util.Log
import androidx.room.util.query
import com.google.firebase.firestore.FieldValue
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
import kr.nbc.momo.data.model.UserResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.data.model.toGroupResponse
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : GroupRepository {
    override suspend fun createGroup(groupEntity: GroupEntity): Flow<Boolean> = callbackFlow {
        val ref = storage.reference.child("groupImage").child("${groupEntity.groupId}.jpeg")
        val listener = if (groupEntity.groupThumbnail != null) {
            // 썸네일 있을 때
            val uploadTask = ref.putFile(Uri.parse(groupEntity.groupThumbnail))
            uploadTask.continueWithTask { ref.downloadUrl }
                .addOnCompleteListener { task ->
                    val downloadUri = task.result
                    val groupResponse = groupEntity.toGroupResponse(downloadUri.toString())
                    fireStore.collection("groups")
                        .document(groupResponse.groupId)
                        .set(groupResponse)
                        .addOnSuccessListener {
                            trySend(true)
                        }.addOnFailureListener { e ->
                            close(e)
                        }
                }
        } else {
            // 썸네일 없을 때
            val groupResponse = groupEntity.toGroupResponse(null)
            fireStore.collection("groups")
                .document(groupResponse.groupId)
                .set(groupResponse)
                .addOnSuccessListener {
                    trySend(true)
                }.addOnFailureListener { e ->
                    close(e)
                }
        }

        awaitClose { listener.result }

    }

    override suspend fun readGroup(groupId: String): Flow<GroupEntity> = flow {
        val snapshot = fireStore.collection("groups").document(groupId).get().await()
        val response = snapshot.toObject<GroupResponse>()
        if (response != null) {
            emit(response.toEntity())
            Log.d("categoryList", "${response.category.programingLanguage}")
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
                transaction.update(ref, "category", groupResponse.category)
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
                        transaction.update(ref, "category", groupResponse.category)
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
        }
        awaitClose { listener.result }
    }

    override suspend fun deleteGroup(groupId: String, userList: List<String>): Flow<Boolean> = callbackFlow {
        val query = fireStore.collection("userInfo").whereIn("userId", userList)

        val listener = query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    fireStore.runTransaction { transaction ->
                        transaction.update(document.reference, "userGroup", FieldValue.arrayRemove(groupId))
                    }.addOnFailureListener { e ->
                        close(e)
                    }
                }
                fireStore.collection("groups").document(groupId).delete()
                    .addOnFailureListener { e ->
                        close(e)
                    }

                trySend(true)
            }

        awaitClose { listener.result }
    }

    override suspend fun getGroupList(): Flow<List<GroupEntity>> = flow {
        val snapshot = fireStore.collection("groups").get().await()
        val response = snapshot.toObjects<GroupResponse>()
        emit(response.map { it.toEntity() })
    }

    override suspend fun changeLeader(groupId: String, leaderId: String): Flow<Boolean> = callbackFlow {
        val ref = fireStore.collection("groups").document(groupId)
        val listener = fireStore.runTransaction { transaction ->
            transaction.update(ref, "leaderId", leaderId)
            null
        }.addOnSuccessListener {
            trySend(true)
        }. addOnFailureListener { e ->
            close(e)
        }

        awaitClose { listener.result }
    }

    override suspend fun searchLeader(userId: String): Flow<List<String>> = callbackFlow {
        val ref = fireStore.collection("groups").whereEqualTo("leaderId", userId)
        val listener = ref.get()
            .addOnSuccessListener { querySnapshot ->
                val list = emptyList<String>().toMutableList()
                for (document in querySnapshot.documents) {
                    document.getString("groupName")?.let { list.add(it) }
                }
                trySend(list)
            }
            .addOnFailureListener { e ->
                close(e)
            }
        awaitClose { listener.result }
    }

    override suspend fun deleteUser(userId: String, groupId: String): Flow<List<String>> = callbackFlow {
        val ref = fireStore.collection("groups").document(groupId)
        val listener = ref.update("userList", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                val query = fireStore.collection("userInfo").whereEqualTo("userId", userId)
                query.get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        document.reference.update("userGroup", FieldValue.arrayRemove(groupId))
                            .addOnSuccessListener {
                                ref.get().result.toObject<GroupResponse>()?.userList?.let { list ->
                                    trySend(list)
                                }
                            }.addOnFailureListener { e ->
                                close(e)
                            }
                    }

                }.addOnFailureListener { e ->
                    close(e)
                }

            }.addOnFailureListener { e ->
                close(e)
            }

        awaitClose { listener.isComplete }
    }
}
