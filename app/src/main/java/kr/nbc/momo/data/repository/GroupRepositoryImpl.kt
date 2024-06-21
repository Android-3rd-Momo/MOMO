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
    private val groupRef = fireStore.collection("groups")
    private val userRef = fireStore.collection("userInfo")

    private suspend fun uploadThumbnail(groupEntity: GroupEntity): String? {
        groupEntity.groupThumbnail?.let {
            val query = storage.reference.child("groupImage").child("${groupEntity.groupId}.jpeg")
            val uploadTask = query.putFile(Uri.parse(it))
            val downloadUrl = uploadTask.continueWithTask { query.downloadUrl }.await()
            return downloadUrl.toString()
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
    override suspend fun createGroup(groupEntity: GroupEntity){
        val thumbnailUrl = uploadThumbnail(groupEntity)
        val groupResponse = groupEntity.toGroupResponse(thumbnailUrl)
        fireStore.collection("groups").document(groupResponse.groupId).set(groupResponse)
    }

    override suspend fun readGroup(groupId: String): Flow<GroupEntity> = callbackFlow {
        val ref = groupRef.document(groupId)
        val registration  = ref.addSnapshotListener{ value, e ->
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

    override suspend fun updateGroup(groupEntity: GroupEntity, imageUri: Uri?) {
            val ref = groupRef.document(groupEntity.groupId)
            val thumbnailUrl = imageUri?.let {
                val storageRef =
                    storage.reference.child("groupImage").child("${groupEntity.groupId}.jpeg")
                val uploadTask = storageRef.putFile(it)
                uploadTask.continueWithTask { storageRef.downloadUrl }.await().toString()
            } ?: groupEntity.groupThumbnail

            val groupResponse = groupEntity.toGroupResponse(thumbnailUrl)
            updateGroupFields(ref, groupResponse)
        }

    override suspend fun addUser(userId: String, groupId: String) {
        val query = userRef.whereEqualTo("userId", userId)
        query.get().addOnSuccessListener { snapshot ->
            for (document in snapshot.documents) {
                fireStore.runTransaction { transaction ->
                    transaction.update(document.reference, "subscriptionList", FieldValue.arrayRemove(groupId))
                    transaction.update(document.reference, "userGroup", FieldValue.arrayUnion(groupId))
                }
            }
        }

        val ref = fireStore.collection("groups").document(groupId)
        fireStore.runTransaction { transaction ->
            transaction.update(ref, "subscriptionList", FieldValue.arrayRemove(userId))
            transaction.update(ref, "userList", FieldValue.arrayUnion(userId))
        }

    }

    override suspend fun subscription(userId: String, groupId: String) {
            val query = userRef.whereEqualTo("userId", userId)
            query.get().addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    fireStore.runTransaction { transaction ->
                        transaction.update(
                            document.reference, "subscriptionList", FieldValue.arrayUnion(groupId)
                        )
                    }
                }
            }

            val ref = fireStore.collection("groups").document(groupId)
            fireStore.runTransaction { transaction ->
                transaction.update(ref, "subscriptionList", FieldValue.arrayUnion(userId))
            }
        }

    override suspend fun rejectionSubscription(userId: String, groupId: String) {
            val query = userRef.whereEqualTo("userId", userId)
            query.get().addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    fireStore.runTransaction { transaction ->
                        transaction.update(
                            document.reference, "subscriptionList", FieldValue.arrayRemove(groupId)
                        )
                    }
                }
            }

            val ref = groupRef.document(groupId)
            fireStore.runTransaction { transaction ->
                transaction.update(ref, "subscriptionList", FieldValue.arrayRemove(userId))
            }
        }

    override suspend fun deleteGroup(groupId: String, userList: List<String>) {
            val query = userRef.whereIn("userId", userList)
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        fireStore.runTransaction { transaction ->
                            transaction.update(document.reference, "userGroup", FieldValue.arrayRemove(groupId))
                        }
                    }
                    fireStore.collection("groups").document(groupId).delete()
                }

        }


    override suspend fun getGroupList(): Flow<List<GroupEntity>> = flow {
        val snapshot = groupRef.get().await()
        val response = snapshot?.toObjects<GroupResponse>() ?: listOf()
        emit(response.map { it.toEntity() })

    }

    override suspend fun changeLeader(groupId: String, leaderId: String) {
            val ref = groupRef.document(groupId)
            fireStore.runTransaction { transaction ->
                transaction.update(ref, "leaderId", leaderId)
            }

        }

    override suspend fun searchLeader(userId: String): Flow<List<String>> = callbackFlow {
        val query = groupRef.whereEqualTo("leaderId", userId)
        query.get()
            .addOnSuccessListener { querySnapshot ->
                val list = emptyList<String>().toMutableList()
                for (document in querySnapshot.documents) {
                    document.getString("groupName")?.let { list.add(it) }
                }
                trySend(list)
            }
        awaitClose()
    }

    override suspend fun deleteUser(userId: String, groupId: String): Flow<List<String>> =
        callbackFlow {
            val ref = groupRef.document(groupId)
            ref.update("userList", FieldValue.arrayRemove(userId)).await()
            val query = userRef.whereEqualTo("userId", userId)
            query.get().await().documents.forEach { document ->
                document.reference.update("userGroup", FieldValue.arrayRemove(groupId)).await()
            }
            val updatedGroup = ref.get().await().toObject<GroupResponse>()?.userList ?: listOf()
            trySend(updatedGroup)

            awaitClose()
        }


    override suspend fun getSubscriptionList(userId: String): Flow<List<GroupEntity>> =
        callbackFlow {
            val query = groupRef.whereEqualTo("leaderId", userId)
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

    override suspend fun getUserGroupList(groupList: List<String>, userId: String): Flow<List<GroupEntity>> =
        callbackFlow {
            val query = groupRef.whereIn("groupId", groupList)
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
            val query = groupRef.whereArrayContains("subscriptionList", userId)
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

    override suspend fun getNotificationCount(userId: String): Flow<Int> =
        callbackFlow {
            val query = groupRef.whereEqualTo("leaderId", userId)
            val registration = query.addSnapshotListener { value, e ->
                if (e != null) {
                    close(e)
                }

                val count = value?.documents?.size ?: 0
                trySend(count)
            }
            awaitClose { registration.remove() }
        }
}
