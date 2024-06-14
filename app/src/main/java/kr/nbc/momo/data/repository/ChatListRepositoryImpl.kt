package kr.nbc.momo.data.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
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
    override suspend fun getChattingListByGroupIdList(list: List<String>, userId: String): List<ChattingListEntity> {
        val newGroupList = mutableListOf<ChattingListResponse>()
        Log.d("GroupIDList", "$list")

        for (i in list) {
            val storeSnapshot = fireStore.collection("groups").document(i).get().await()

            val storeResponse = storeSnapshot.toObject<GroupResponse>() ?: GroupResponse(
                groupName = "Error"
            )

            val databaseSnapshot = fireStoreDatabase.getReference("Chatting").child(i).get().await()
            val databaseResponse =
                (databaseSnapshot.getValue(GroupChatResponse::class.java) ?: GroupChatResponse())
            val chatListResponse = databaseResponse.chatList
            val latestChatting = chatListResponse.lastOrNull() ?: ChatResponse(
                text = "그룹 채팅을 시작해보세요!"
            )
            val lastViewedChatting = databaseResponse.userList.firstOrNull { it.userId == userId }?.lastViewedChat ?: ChatResponse()

            val chattingIndexGap = if(chatListResponse.indexOf(lastViewedChatting) == -1) chatListResponse.size else chatListResponse.lastIndex - chatListResponse.indexOf(lastViewedChatting)

            val chattingListResponse = ChattingListResponse(
                groupName = storeResponse.groupName,
                groupId = i,
                groupThumbnailUrl = storeResponse.groupThumbnail,
                latestChatMessage = latestChatting.text,
                latestChatTimeGap = latestChatting.dateTime.getTimeGap(),
                chattingIndexGap
            )
            newGroupList.add(chattingListResponse)
        }

        return newGroupList.map { it.toEntity() }
    }

    override suspend fun getChattingListByGroupId(string: String): ChattingListEntity {
        val storeSnapshot = fireStore.collection("groups").document(string).get().await()

        val storeResponse = storeSnapshot.toObject<GroupResponse>() ?: GroupResponse(
            groupName = "Error"
        )

        val databaseSnapshot = fireStoreDatabase.getReference("Chatting").child(string).get().await()
        val databaseResponse =
            (databaseSnapshot.getValue(GroupChatResponse::class.java) ?: GroupChatResponse())
        val latestChatting = databaseResponse.chatList.lastOrNull() ?: ChatResponse(
            text = "그룹 채팅을 시작해보세요!"
        )

        val chattingListResponse = ChattingListResponse(
            groupName = storeResponse.groupName,
            groupId = string,
            groupThumbnailUrl = storeResponse.groupThumbnail,
            latestChatMessage = latestChatting.text,
            latestChatTimeGap = latestChatting.dateTime.getTimeGap()
        )

        return chattingListResponse.toEntity()
    }
}
