package kr.nbc.momo.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.model.ChatResponse
import kr.nbc.momo.data.model.GroupChatResponse
import kr.nbc.momo.data.model.GroupUserResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.domain.model.GroupChatEntity
import kr.nbc.momo.domain.repository.ChatRepository
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    chatDataBase: FirebaseDatabase
) : ChatRepository {
    private val chatRef = chatDataBase.getReference("Chatting")

    override suspend fun getGroupChatByGroupId(groupId: String): Flow<GroupChatEntity> =
        callbackFlow {
            val groupChatListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (groupId.isNotBlank()) {
                        val chatMessages = snapshot.getValue(GroupChatResponse::class.java)
                        Log.d("repository", "$groupId + $chatMessages ")
                        val groupChatEntity = chatMessages?.toEntity() ?: GroupChatEntity()
                        trySend(groupChatEntity).isSuccess
                    }
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


    override suspend fun sendChat(
        groupId: String,
        userId: String,
        text: String,
        userName: String,
        groupName: String,
        url: String
    ) {
        try {
            if (groupId.isNotBlank()) {
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

                val updatedChatList =
                    groupChatResponse?.chatList?.toMutableList() ?: mutableListOf()
                updatedChatList.add(newChatResponse)
                val userList = groupChatResponse?.userList ?: listOf()

                val newUserList =
                    if (userList.none { it.userId == userId }) userList.toMutableList().apply {
                        add(
                            GroupUserResponse(
                                userId,
                                userName,
                                "",
                                newChatResponse
                            )
                        )
                    }
                    else userList

                val updatedGroupChatResponse = groupChatResponse?.copy(
                    groupId = groupId,
                    groupName = groupName,
                    userList = newUserList,
                    chatList = updatedChatList
                ) ?: GroupChatResponse(
                    groupId = groupId,
                    groupName = groupName,
                    userList = newUserList,
                    chatList = updatedChatList
                )

                chatRef.child(groupId).setValue(updatedGroupChatResponse).await()
            }
        } catch (e: Exception) {
            Log.e("repository", "Failed to send message", e)
        }
    }

    override suspend fun setLastViewedChat(groupId: String, userId: String, userName: String) {
        try {
            if (groupId.isNotBlank()) {
                val groupSnapshot = chatRef.child(groupId).get().await()
                val groupChatResponse = groupSnapshot.getValue(GroupChatResponse::class.java)
                val groupUserResponse = groupChatResponse?.userList ?: listOf()

                val lastViewChatResponse = groupChatResponse?.chatList?.last() ?: ChatResponse()
                val newGroupUserResponse = groupUserResponse.map {
                    if (it.userId == userId) it.copy(lastViewedChat = lastViewChatResponse) else it
                }

                val newGroupChatResponse = groupChatResponse?.copy(
                    userList = newGroupUserResponse
                )
                chatRef.child(groupId).setValue(newGroupChatResponse).await()
            }
        } catch (e: Exception) {
            Log.e("repository", "Failed to update last viewed chat", e)

        }
    }
}