package kr.nbc.momo.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
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
            val storeSnapshot = fireStore.collection("groups").document(i).get().await()
            val storeResponse =
                (storeSnapshot.toObject(GroupResponse::class.java) ?: GroupResponse())
            val databaseSnapshot = fireStoreDatabase.getReference(i).get().await()
            val databaseResponse =
                (databaseSnapshot.getValue(GroupChatResponse::class.java) ?: GroupChatResponse())
            val latestChatting = databaseResponse.chatList.last()

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