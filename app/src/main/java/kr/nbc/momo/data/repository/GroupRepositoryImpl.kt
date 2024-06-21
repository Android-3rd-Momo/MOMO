package kr.nbc.momo.data.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
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
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.data.model.toGroupResponse
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : GroupRepository {
    private suspend fun uploadThumbnail(groupEntity: GroupEntity): String? {
        groupEntity.groupThumbnail?.let {
            val ref = storage.reference.child("groupImage").child("${groupEntity.groupId}.jpeg")
            val uploadTask = ref.putFile(Uri.parse(it))
            val downloadUri = uploadTask.continueWithTask { ref.downloadUrl }.await()
            return downloadUri.toString()
        }
        return null
    }

    private suspend fun updateGroupFields(ref: DocumentReference, groupResponse: GroupResponse) {
        fireStore.runTransaction { transaction ->
            transaction.update(ref, mapOf(
                "groupName" to groupResponse.groupName,
                "groupOneLineDescription" to groupResponse.groupOneLineDescription,
                "groupDescription" to groupResponse.groupDescription,
                "firstDate" to groupResponse.firstDate,
                "lastDate" to groupResponse.lastDate,
                "category" to groupResponse.category,
                "groupThumbnail" to groupResponse.groupThumbnail,
                "limitPerson" to groupResponse.limitPerson
            ))
        }.await()
    }
    override suspend fun createGroup(groupEntity: GroupEntity): Flow<Boolean> = callbackFlow {
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

        awaitClose()

    }

    override suspend fun readGroup(groupId: String): Flow<GroupEntity> = callbackFlow {
        val query = fireStore.collection("groups").document(groupId)
        val registration  = query.addSnapshotListener{ value, e ->
                if (e != null) {
                    close(e)
                }

                val response = value?.toObject<GroupResponse>()
                if (response != null) {
                    trySend(response.toEntity())
                } else {
                    trySend(GroupEntity(groupId = "error"))
                }
            }

        awaitClose { registration.remove() }

    }

    override suspend fun updateGroup(groupEntity: GroupEntity, imageUri: Uri?): Flow<GroupEntity> =
        callbackFlow {
            try {
                val ref = fireStore.collection("groups").document(groupEntity.groupId)
                val thumbnailUrl = imageUri?.let {
                    val storageRef = storage.reference.child("groupImage").child("${groupEntity.groupId}.jpeg")
                    val uploadTask = storageRef.putFile(it)
                    uploadTask.continueWithTask { storageRef.downloadUrl }.await().toString()
                } ?: groupEntity.groupThumbnail

                val groupResponse = groupEntity.toGroupResponse(thumbnailUrl)
                updateGroupFields(ref, groupResponse)
                trySend(groupResponse.toEntity())
            } catch (e: Exception) {
                close(e)
            }
            awaitClose()

        }

    override suspend fun addUser(userId: String, groupId: String): Flow<Boolean> = callbackFlow {
        val query = fireStore.collection("userInfo").whereEqualTo("userId", userId)
        query.get().addOnSuccessListener { snapshot ->
            for (document in snapshot.documents) {
                fireStore.runTransaction { transaction ->
                    transaction.update(
                        document.reference,
                        "subscriptionList",
                        FieldValue.arrayRemove(groupId)
                    )
                    transaction.update(
                        document.reference,
                        "userGroup",
                        FieldValue.arrayUnion(groupId)
                    )
                }.addOnFailureListener { e ->
                    close(e)
                }
            }
        }

        val ref = fireStore.collection("groups").document(groupId)
        fireStore.runTransaction { transaction ->
            transaction.update(ref, "subscriptionList", FieldValue.arrayRemove(userId))
            transaction.update(ref, "userList", FieldValue.arrayUnion(userId))
        }.addOnFailureListener { e ->
            close(e)
        }

        trySend(true)
        awaitClose()
    }

    override suspend fun subscription(userId: String, groupId: String): Flow<Boolean> =
        callbackFlow {
            val query = fireStore.collection("userInfo").whereEqualTo("userId", userId)
            query.get().addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    fireStore.runTransaction { transaction ->
                        transaction.update(
                            document.reference, "subscriptionList", FieldValue.arrayUnion(groupId)
                        )
                    }.addOnFailureListener { e ->
                        close(e)
                    }
                }
            }

            val ref = fireStore.collection("groups").document(groupId)
            fireStore.runTransaction { transaction ->
                transaction.update(ref, "subscriptionList", FieldValue.arrayUnion(userId))
            }.addOnFailureListener { e ->
                close(e)
            }

            trySend(true)
            awaitClose()
        }

    override suspend fun rejectionSubscription(userId: String, groupId: String): Flow<Boolean> =
        callbackFlow {
            val query = fireStore.collection("userInfo").whereEqualTo("userId", userId)
            query.get().addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    fireStore.runTransaction { transaction ->
                        transaction.update(
                            document.reference,
                            "subscriptionList",
                            FieldValue.arrayRemove(groupId)
                        )
                    }.addOnFailureListener { e ->
                        close(e)
                    }
                }
            }

            val ref = fireStore.collection("groups").document(groupId)
            fireStore.runTransaction { transaction ->
                transaction.update(ref, "subscriptionList", FieldValue.arrayRemove(userId))
            }.addOnFailureListener { e ->
                close(e)
            }

            trySend(true)
            awaitClose()
        }

    override suspend fun deleteGroup(groupId: String, userList: List<String>): Flow<Boolean> =
        callbackFlow {
            val query = fireStore.collection("userInfo").whereIn("userId", userList)

            query.get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        fireStore.runTransaction { transaction ->
                            transaction.update(
                                document.reference,
                                "userGroup",
                                FieldValue.arrayRemove(groupId)
                            )
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

            awaitClose()
        }


    override suspend fun getGroupList(): Flow<List<GroupEntity>> = flow {
        val snapshot = fireStore.collection("groups").get().await()
        val response = snapshot?.toObjects<GroupResponse>() ?: listOf()
        emit(response.map { it.toEntity() })

    }

    override suspend fun changeLeader(groupId: String, leaderId: String): Flow<Boolean> =
        callbackFlow {
            val ref = fireStore.collection("groups").document(groupId)
            fireStore.runTransaction { transaction ->
                transaction.update(ref, "leaderId", leaderId)
                null
            }.addOnSuccessListener {
                trySend(true)
            }.addOnFailureListener { e ->
                close(e)
            }

            awaitClose()
        }

    override suspend fun searchLeader(userId: String): Flow<List<String>> = callbackFlow {
        val ref = fireStore.collection("groups").whereEqualTo("leaderId", userId)
        ref.get()
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
        awaitClose()
    }

    override suspend fun deleteUser(userId: String, groupId: String): Flow<List<String>> =
        callbackFlow {
            val ref = fireStore.collection("groups").document(groupId)
            ref.update("userList", FieldValue.arrayRemove(userId))
                .addOnSuccessListener {
                    val query = fireStore.collection("userInfo").whereEqualTo("userId", userId)
                    query.get().addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot) {
                            document.reference.update("userGroup", FieldValue.arrayRemove(groupId))
                                .addOnSuccessListener {
                                    ref.get().addOnSuccessListener {
                                        it.toObject<GroupResponse>()?.userList?.let { it1 ->
                                            trySend(
                                                it1
                                            )
                                        }
                                    }.addOnFailureListener { e ->
                                        close(e)
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

            awaitClose()
        }


    override suspend fun getSubscriptionList(userId: String): Flow<List<GroupEntity>> =
        callbackFlow {
            val query = fireStore.collection("groups").whereEqualTo("leaderId", userId)
            val registration = query.addSnapshotListener { value, e ->
                if (e != null) {
                    close(e)
                }

                val list = emptyList<GroupResponse>().toMutableList()
                if (value != null) {
                    for (i in value.documents) {
                        i.toObject<GroupResponse>()?.let { list.add(it) }
                    }
                }

                trySend(list.map { it.toEntity() })
            }
            awaitClose { registration.remove() }
        }

    override suspend fun getUserGroupList(
        groupList: List<String>,
        userId: String
    ): Flow<List<GroupEntity>> =
        callbackFlow {
            val query = fireStore.collection("groups").whereIn("groupId", groupList)
            val registration = query.addSnapshotListener { value, e ->
                if (e != null) {
                    close(e)
                }

                val list = listOf<GroupResponse>().toMutableList()
                if (value != null) {
                    for (i in value.documents) {
                        i.toObject<GroupResponse>()?.let { list.add(it) }
                    }
                }
                trySend(list.map { it.toEntity() })
            }
            awaitClose { registration.remove() }
        }

    override suspend fun getAppliedGroup(userId: String): Flow<List<GroupEntity>> =
        callbackFlow {
            val query = fireStore.collection("groups").whereArrayContains("subscriptionList", userId)
            val registration = query.addSnapshotListener { value, e ->
                if (e != null) {
                    close(e)
                }

                val list = listOf<GroupResponse>().toMutableList()
                if (value != null) {
                    for (i in value.documents) {
                        i.toObject<GroupResponse>()?.let { list.add(it) }
                    }
                }
                trySend(list.map { it.toEntity() })
            }

            awaitClose { registration.remove() }
        }
}
