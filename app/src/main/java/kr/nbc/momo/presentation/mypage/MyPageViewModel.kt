package kr.nbc.momo.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.SaveUserProfileUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.presentation.onboarding.signup.model.toEntity
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val saveUserProfileUseCase: SaveUserProfileUseCase
) : ViewModel() {

    private val _userProfileUpdate = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val userProfileUpdate: StateFlow<UiState<UserModel>> get() = _userProfileUpdate

    fun saveUserProfile(updatedUser: UserModel) {
        viewModelScope.launch {
            _userProfileUpdate.value = UiState.Loading
            try {
                saveUserProfileUseCase(updatedUser.toEntity())
                _userProfileUpdate.value = UiState.Success(updatedUser)
            } catch (e: Exception) {
                _userProfileUpdate.value = UiState.Error(e.toString())
            }
        }
    }
}
