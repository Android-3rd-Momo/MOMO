package kr.nbc.momo.data.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.model.ChatResponse
import kr.nbc.momo.data.model.ChattingListResponse
import kr.nbc.momo.data.model.GroupChatResponse
import kr.nbc.momo.data.model.GroupResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.domain.model.ChattingListEntity
import kr.nbc.momo.domain.repository.ChatListRepository
import kr.nbc.momo.util.getTimeGap
import javax.inject.Inject

class ChatListRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val fireStoreDatabase: FirebaseDatabase
) : ChatListRepository {
    override suspend fun getChattingListByGroupIdList(list: List<String>): List<ChattingListEntity> {
        val newGroupList = mutableListOf<ChattingListResponse>()

        for (i in list) {
            val storeSnapshot = fireStore.collection("groups").document(i).get().addOnFailureListener{
                Log.d("error", it.toString())
            }.addOnSuccessListener {
                if (it.data == null) Log.d("error", "${it.id}")
            }.await()

            val storeResponse = storeSnapshot.toObject<GroupResponse>() ?: GroupResponse(
                groupName = "Error"
            )

            val databaseSnapshot = fireStoreDatabase.getReference("Chatting").child(i).get().await()
            val databaseResponse =
                (databaseSnapshot.getValue(GroupChatResponse::class.java) ?: GroupChatResponse())
            Log.d("asd", "${databaseResponse}")
            val latestChatting = databaseResponse.chatList.lastOrNull() ?: ChatResponse(
                text = "그룹 채팅을 시작해보세요!"
            )

            val chattingListResponse = ChattingListResponse(
                groupName = storeResponse.groupName,
                groupId = i,
                groupThumbnailUrl = storeResponse.groupThumbnail,
                latestChatMessage = latestChatting.text,
                latestChatTimeGap = latestChatting.dateTime.getTimeGap()
            )
            newGroupList.add(chattingListResponse)
        }

        return newGroupList.map { it.toEntity() }
    }
}