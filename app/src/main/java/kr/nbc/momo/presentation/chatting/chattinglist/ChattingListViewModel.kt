package kr.nbc.momo.presentation.chatting.chattinglist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.GetChattingListUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.chatting.chattinglist.model.ChattingListModel
import kr.nbc.momo.presentation.chatting.chattinglist.model.toModel
import javax.inject.Inject

@HiltViewModel
class ChattingListViewModel @Inject constructor(
    private val chattingListUseCase: GetChattingListUseCase
) : ViewModel() {
    private val _chattingList = MutableStateFlow<UiState<List<ChattingListModel>>>(UiState.Loading)
    val chattingList: StateFlow<UiState<List<ChattingListModel>>> get() = _chattingList

    fun getChattingList(list: List<String>, userId: String) {
        viewModelScope.launch {
            try {
                _chattingList.value =
                    UiState.Success(chattingListUseCase.invoke(list, userId).map { it.toModel() })
            } catch (e: Exception) {
                Log.e("Chatting List Error", e.toString(), e)
                _chattingList.value = UiState.Error(e.toString())
            }
        }

/*
        _chattingList.value = UiState.Success(chatListDummy)
*/
    }
}