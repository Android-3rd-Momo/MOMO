package kr.nbc.momo.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.model.ChatResponse
import kr.nbc.momo.data.model.GroupChatResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.domain.model.GroupChatEntity
import kr.nbc.momo.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    chatDataBase: FirebaseDatabase
) : ChatRepository {
    // apply 이후 코드는 리얼타임 DB에서 제공하는 로컬캐싱 코드
    private val chatRef = chatDataBase.getReference("Chatting")
    private val groupChatStateFlow = MutableStateFlow<GroupChatEntity>(GroupChatEntity())

    override fun getGroupChatByGroupId(groupId: String): StateFlow<GroupChatEntity> {
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
    }

    override suspend fun sendChat(groupId: String, userId: String, text: String, userName: String) {
        try {
            val groupSnapshot = chatRef.child(groupId).get().await()
            val groupChatResponse = groupSnapshot.getValue(GroupChatResponse::class.java)

            val newChatResponse = ChatResponse(
                userName = userName,
                userId = userId,
                text = text,
                dateTime = System.currentTimeMillis().toString()
            )

            val updatedChatList = groupChatResponse?.chatList?.toMutableList() ?: mutableListOf()
            updatedChatList.add(newChatResponse)

            val updatedGroupChatResponse = groupChatResponse?.copy(chatList = updatedChatList)
                ?: GroupChatResponse(groupId, chatList = updatedChatList)

            chatRef.child(groupId).setValue(updatedGroupChatResponse).await()
        } catch (e: Exception) {
            Log.e("repository", "Failed to send message", e)
        }
    }
}