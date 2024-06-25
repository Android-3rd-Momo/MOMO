package kr.nbc.momo.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.GetCurrentUserUseCase
import kr.nbc.momo.domain.usecase.SetLastViewedChatUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.presentation.onboarding.signup.model.toModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val setLastViewedChatUseCase: SetLastViewedChatUseCase
) : ViewModel() {
    private val _groupId: MutableStateFlow<String?> = MutableStateFlow(null)
    val groupId: StateFlow<String?> get() = _groupId.asStateFlow()

    private val _userId: MutableStateFlow<String?> = MutableStateFlow(null)
    val userId: StateFlow<String?> get() = _userId.asStateFlow()

    private val _currentUser = MutableStateFlow<UiState<UserModel?>>(UiState.Loading)
    val currentUser: StateFlow<UiState<UserModel?>> get() = _currentUser

    private val _updateUserState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val updateUserState: StateFlow<UiState<Unit>> get() = _updateUserState

    init {
        getCurrentUser()
    }


    fun getCurrentUser() {
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
        viewModelScope.launch {
            _updateUserState.value = UiState.Loading
            try {
                _currentUser.value = UiState.Success(user)
            } catch (e: Exception) {
                _updateUserState.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun getGroupId(groupId: String) {
        _groupId.value = groupId
    }

    fun getUserId(userId: String) {
        _userId.value = userId
    }


    fun setLastViewedChat(groupId: String, userId: String, userName: String, url: String) {
        viewModelScope.launch {
            setLastViewedChatUseCase.invoke(groupId, userId, userName, url)
        }
    }


}