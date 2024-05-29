package kr.nbc.momo.presentation.chattingroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.ChatUseCase
import kr.nbc.momo.presentation.chattingroom.model.GroupChatModel
import kr.nbc.momo.presentation.chattingroom.model.toModel
import javax.inject.Inject
@HiltViewModel
class ChattingRoomViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase
): ViewModel() {
    private val _chatMessages = MutableStateFlow<GroupChatModel>(GroupChatModel())
    val chatMessages: StateFlow<GroupChatModel> get() = _chatMessages

    fun getChatMessages(groupId: String) {
        viewModelScope.launch {
            chatUseCase.invoke(groupId).collectLatest {
                _chatMessages.value = it.toModel()
            }
        }
    }

    fun sendChat(groupId: String, userId: String, text: String, userName: String){
        viewModelScope.launch{
            chatUseCase.invoke(groupId, userId, text, userName)
        }
    }
}