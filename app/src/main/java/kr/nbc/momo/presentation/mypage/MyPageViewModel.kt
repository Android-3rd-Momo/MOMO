package kr.nbc.momo.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.GetCurrentUserUseCase
import kr.nbc.momo.domain.usecase.SaveUserProfileUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.signup.model.UserModel
import kr.nbc.momo.presentation.signup.model.toEntity
import kr.nbc.momo.presentation.signup.model.toModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val userProfile: StateFlow<UiState<UserModel>> get() = _userProfile

    fun getUserProfile() {
        viewModelScope.launch {
            _userProfile.value = UiState.Loading
            getCurrentUserUseCase().collect { userEntity ->
                _userProfile.value = if (userEntity != null) {
                    UiState.Success(userEntity.toModel())
                } else {
                    UiState.Error("User not found")
                }
            }
        }
    }

    fun saveUserProfile(user: UserModel) {
        viewModelScope.launch {
            _userProfile.value = UiState.Loading
            try {
                saveUserProfileUseCase(user.toEntity())
                _userProfile.value = UiState.Success(user)
            } catch (e: Exception) {
                _userProfile.value = UiState.Error(e.toString())
            }
        }
    }
}
