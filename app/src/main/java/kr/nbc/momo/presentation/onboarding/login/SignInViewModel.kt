package kr.nbc.momo.presentation.onboarding.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.SignInUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.presentation.onboarding.signup.model.toModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {
    private val _authState = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val authState: StateFlow<UiState<UserModel>> get() = _authState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            try {
                val user = signInUseCase(email, password)
                _authState.value = UiState.Success(user.toModel())
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.toString())
            }
        }
    }

}