package kr.nbc.momo.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.GetCurrentUserUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.chatting.chattinglist.model.ChattingListModel
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.presentation.onboarding.signup.model.toModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _groupId: MutableLiveData<String?> = MutableLiveData()
    val groupId: MutableLiveData<String?> get() = _groupId

    private val _currentUser = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val currentUser: StateFlow<UiState<UserModel>> get() = _currentUser

    init {
        getCurrentUser()
    }

    fun getCurrentUser() { //로그인된 정보
        viewModelScope.launch {
            _currentUser.value = UiState.Loading
            getCurrentUserUseCase().collect { userEntity ->
                _currentUser.value = if (userEntity != null) {
                    UiState.Success(userEntity.toModel())
                } else {
                    UiState.Error("Do not log in")
                }

            }
        }
    }

    fun updateUser(user: UserModel) {
        _currentUser.value = UiState.Success(user)
    }

    fun getGroupId(groupId: String) {
        _groupId.value = groupId
    }



    private val _groupIdToGroupChat: MutableStateFlow<ChattingListModel?> = MutableStateFlow(null)
    val groupIdToGroupChat: StateFlow<ChattingListModel?> get() = _groupIdToGroupChat

    fun setGroupIdToGroupChat(chattingListModel: ChattingListModel){
        _groupIdToGroupChat.value = chattingListModel
    }
    fun removeGroupIdToGroupChat(){
        _groupIdToGroupChat.value = null
    }

}