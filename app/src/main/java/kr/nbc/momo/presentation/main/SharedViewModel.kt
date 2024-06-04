package kr.nbc.momo.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.GetCurrentUserUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.signup.model.UserModel
import kr.nbc.momo.presentation.signup.model.toModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _groupName: MutableLiveData<String> = MutableLiveData()
    val groupName: LiveData<String> get() = _groupName

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

    fun getGroupName(groupName: String) {
        _groupName.value = groupName
    }

}