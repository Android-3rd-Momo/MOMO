package kr.nbc.momo.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.data.model.UserResponse
import kr.nbc.momo.domain.usecase.GetCurrentUserUseCase
import kr.nbc.momo.domain.usecase.SignInUseCase
import kr.nbc.momo.domain.usecase.SignUpUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.signup.model.UserModel
import kr.nbc.momo.presentation.signup.model.toEntity
import kr.nbc.momo.presentation.signup.model.toModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
): ViewModel() {

    private val _authState = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val authState: StateFlow<UiState<UserModel>> get() = _authState

    fun signIn(email: String, password: String) { //미구현
        viewModelScope.launch {
            _authState.value = UiState.Loading
            runCatching {
                signInUseCase(email, password)
            }.onSuccess { userEntity ->
                _authState.value = UiState.Success(userEntity.toModel())
            }.onFailure { exception ->
                _authState.value = UiState.Error(exception.message ?: "Unknown error")
            }
        }
    }

    fun signUp(email: String, password: String, user: UserModel) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            runCatching { //todo
                signUpUseCase(email, password, user.toEntity())
            }.onSuccess {
                _authState.value = UiState.Success(it.toModel())
            }.onFailure { exception ->
                _authState.value = UiState.Error(exception.message ?: "Unknown error")
            }
        }
    }
}