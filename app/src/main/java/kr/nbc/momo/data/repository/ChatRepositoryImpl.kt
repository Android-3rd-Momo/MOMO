package kr.nbc.momo.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.model.ChatResponse
import kr.nbc.momo.data.model.GroupChatResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.domain.model.GroupChatEntity
import kr.nbc.momo.domain.repository.ChatRepository
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    chatDataBase: FirebaseDatabase
) : ChatRepository {
    // apply 이후 코드는 리얼타임 DB에서 제공하는 로컬캐싱 코드
    private val chatRef = chatDataBase.getReference("Chatting")
    //private val groupChatStateFlow = MutableStateFlow<GroupChatEntity>(GroupChatEntity())

/*    override fun getGroupChatByGroupId(groupId: String): StateFlow<GroupChatEntity> {
        chatRef.child(groupId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatMessages = dataSnapshot.getValue(GroupChatResponse::class.java)
                val groupChatEntity = chatMessages?.toEntity() ?: GroupChatEntity()
                groupChatStateFlow.value = groupChatEntity
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("repository", "Database error: ${databaseError.message}", databaseError.toException())
            }
        })
        return groupChatStateFlow
    }*/

    override suspend fun getGroupChatByGroupId(groupId: String): Flow<GroupChatEntity> = callbackFlow {
        val groupChatListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatMessages = snapshot.getValue(GroupChatResponse::class.java)
                val groupChatEntity = chatMessages?.toEntity()?: GroupChatEntity()
                trySend(groupChatEntity).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("repository", "Database error: ${error.message}", error.toException())
                close(error.toException())
            }

        }
        chatRef.child(groupId).addValueEventListener(groupChatListener)
        //로그아웃하면 이벤트리스너 없에야함
        awaitClose { chatRef.child(groupId).removeEventListener(groupChatListener) }

    }

/*    override suspend fun getGroupChatByGroupId(groupId: String): Flow<GroupChatEntity> = flow {
        var groupChatEntity = GroupChatEntity()
        val groupChatListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatMessages = snapshot.getValue(GroupChatResponse::class.java)
                groupChatEntity = chatMessages?.toEntity()?: GroupChatEntity()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("repository", "Database error: ${error.message}", error.toException())
            }

        }
        chatRef.child(groupId).addValueEventListener(groupChatListener)

        emit(groupChatEntity)
        //로그아웃하면 이벤트리스너 없에야함
        //awaitClose { chatRef.child(groupId).removeEventListener(groupChatListener) }

    }*/

    override suspend fun sendChat(groupId: String, userId: String, text: String, userName: String, groupName: String) {
        try {
            val groupSnapshot = chatRef.child(groupId).get().await()
            val groupChatResponse = groupSnapshot.getValue(GroupChatResponse::class.java)
            val koreaZoneId = ZoneId.of("Asia/Seoul")
            val koreaTime = ZonedDateTime.now(koreaZoneId)

            val newChatResponse = ChatResponse(
                userName = userName,
                userId = userId,
                text = text,
                dateTime = koreaTime.toString()
            )

            val updatedChatList = groupChatResponse?.chatList?.toMutableList() ?: mutableListOf()
            updatedChatList.add(newChatResponse)

            val updatedGroupChatResponse = groupChatResponse?.copy(groupId = groupId, groupName = groupName, chatList = updatedChatList)
                ?: GroupChatResponse(groupId = groupId, groupName = groupName, chatList = updatedChatList)

            chatRef.child(groupId).setValue(updatedGroupChatResponse).await()
        } catch (e: Exception) {
            Log.e("repository", "Failed to send message", e)
        }
    }
}