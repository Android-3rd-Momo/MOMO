package kr.nbc.momo.presentation.userinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.UserInfoUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.presentation.onboarding.signup.model.toModel
import javax.inject.Inject
@HiltViewModel
class UserInfoViewModel  @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase
) : ViewModel() {
    private val _userState = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val userState: StateFlow<UiState<UserModel>> get() = _userState
    fun userInfo(userId: String) {
        viewModelScope.launch {
            _userState.value = UiState.Loading

            userInfoUseCase.invoke(userId)
                .catch { e ->
                    _userState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _userState.value = UiState.Success(data.toModel())
                }
        }
    }
}