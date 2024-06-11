package kr.nbc.momo.presentation.chatting.chattingroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.GetChattingListByIdUseCase
import kr.nbc.momo.domain.usecase.GetChattingUseCase
import kr.nbc.momo.domain.usecase.SendChatUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.chatting.chattinglist.model.ChattingListModel
import kr.nbc.momo.presentation.chatting.chattinglist.model.toModel
import kr.nbc.momo.presentation.chatting.chattingroom.model.GroupChatModel
import kr.nbc.momo.presentation.chatting.chattingroom.model.toModel
import javax.inject.Inject

@HiltViewModel
class ChattingRoomViewModel @Inject constructor(
    private val getChattingUseCase: GetChattingUseCase,
    private val sendChatUseCase: SendChatUseCase,
    private val getChattingListByIdUseCase: GetChattingListByIdUseCase
): ViewModel() {
    private val _chatMessages = MutableStateFlow<UiState<GroupChatModel>>(UiState.Loading)
    val chatMessages: StateFlow<UiState<GroupChatModel>> get() = _chatMessages

    private val _chatListItem = MutableStateFlow<UiState<ChattingListModel>>(UiState.Loading)
    val chatListItem: StateFlow<UiState<ChattingListModel>> get() = _chatListItem

    fun getChatMessages(groupId: String) {
        viewModelScope.launch {
            getChattingUseCase.invoke(groupId).catch{
                _chatMessages.value = UiState.Error(it.toString())
            }.collectLatest {
                _chatMessages.value = UiState.Success(it.toModel())
            }
        }
    }

    fun sendChat(groupId: String, userId: String, text: String, userName: String, groupName: String, url: String){
        viewModelScope.launch{
            sendChatUseCase.invoke(groupId, userId, text, userName, groupName, url)
        }
    }

    fun getChatListItemById(groupId: String){
        viewModelScope.launch{
            _chatListItem.value = UiState.Success(getChattingListByIdUseCase.invoke(groupId).toModel())
        }
    }
}