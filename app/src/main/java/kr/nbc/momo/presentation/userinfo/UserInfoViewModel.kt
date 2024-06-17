package kr.nbc.momo.presentation.userinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.BlockUserUseCase
import kr.nbc.momo.domain.usecase.ReportUserUseCase
import kr.nbc.momo.domain.usecase.UserInfoUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.presentation.onboarding.signup.model.toModel
import javax.inject.Inject
@HiltViewModel
class UserInfoViewModel  @Inject constructor(
    private val userInfoUseCase: UserInfoUseCase,
    private val reportUserUseCase: ReportUserUseCase,
    private val blockUserUseCase: BlockUserUseCase
) : ViewModel() {
    private val _userState = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val userState: StateFlow<UiState<UserModel>> get() = _userState

    private val _reportUserState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val reportUserState: StateFlow<UiState<Boolean>> get() = _reportUserState

    private val _blockUserState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val blockUserState: StateFlow<UiState<Boolean>> get() = _blockUserState
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

    fun reportUser(reportedUser: String) {
        viewModelScope.launch {
            _reportUserState.value = UiState.Loading

            reportUserUseCase.invoke(reportedUser)
                .catch { e ->
                    _reportUserState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _reportUserState.value = UiState.Success(data)
                }
        }
    }

    fun blockUser(blockUser: String) {
        viewModelScope.launch {
            _blockUserState.value = UiState.Loading

            blockUserUseCase.invoke(blockUser)
                .catch { e ->
                    _blockUserState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _blockUserState.value = UiState.Success(data)
                }
        }
    }
}