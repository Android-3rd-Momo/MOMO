package kr.nbc.momo.presentation.onboarding.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.SignUpUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.presentation.onboarding.signup.model.toEntity
import kr.nbc.momo.presentation.onboarding.signup.model.toModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
): ViewModel() {

    private val _authState = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val authState: StateFlow<UiState<UserModel>> get() = _authState

    fun signUp(email: String, password: String, user: UserModel) {
        viewModelScope.launch {
//            signUpUseCase.invoke(email,password,user.toEntity())
            _authState.value = UiState.Loading
            try {
                val userEntity = signUpUseCase(email, password, user.toEntity())
                _authState.value = UiState.Success(userEntity.toModel())
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.toString())
            }
        }
    }
}