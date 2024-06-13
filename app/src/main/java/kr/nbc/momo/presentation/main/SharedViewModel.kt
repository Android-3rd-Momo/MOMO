package kr.nbc.momo.presentation.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.GetChattingListByIdUseCase
import kr.nbc.momo.domain.usecase.GetCurrentUserUseCase
import kr.nbc.momo.domain.usecase.SetLastViewedChatUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.chatting.chattinglist.model.ChattingListModel
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.presentation.onboarding.signup.model.toModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val setLastViewedChatUseCase: SetLastViewedChatUseCase
) : ViewModel() {
    private val _groupId: MutableLiveData<String?> = MutableLiveData()
    val groupId: MutableLiveData<String?> get() = _groupId

    private val _currentUser = MutableStateFlow<UiState<UserModel?>>(UiState.Loading)
    val currentUser: StateFlow<UiState<UserModel?>> get() = _currentUser

    init {
        getCurrentUser()
    }


    fun getCurrentUser() { // 변경된 내용
        viewModelScope.launch {
            _currentUser.value = UiState.Loading
            try {
                getCurrentUserUseCase().collect { userEntity ->
                    _currentUser.value = UiState.Success(userEntity?.toModel())
                }
            } catch (e: Exception) {
                _currentUser.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun updateUser(user: UserModel) {
        _currentUser.value = UiState.Success(user)
    }

    fun getGroupId(groupId: String) {
        _groupId.value = groupId
    }


    fun setLastViewedChat(groupId: String, userId: String, userName: String) {
        viewModelScope.launch {
            setLastViewedChatUseCase.invoke(groupId, userId, userName)
        }
    }


}