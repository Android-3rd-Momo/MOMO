package kr.nbc.momo.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.data.datastore.UserPreferences
import kr.nbc.momo.domain.repository.UserRepository
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.signup.model.UserModel
import kr.nbc.momo.presentation.signup.model.toEntity
import kr.nbc.momo.presentation.signup.model.toModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val userProfile: StateFlow<UiState<UserModel>> get() = _userProfile

    fun getUserProfile() {
        viewModelScope.launch {
            _userProfile.value = UiState.Loading
            userRepository.getCurrentUser().catch {
                _userProfile.value = UiState.Error(it.toString()) }
                .collect { userEntity ->
                    userEntity?.let {
                        _userProfile.value = UiState.Success(it.toModel())
                        userPreferences.saveUserInfo(it) //dataStore
                    }//
                }
        }
    }

    fun saveUserProfile(user: UserModel) {
        viewModelScope.launch {
            _userProfile.value = UiState.Loading
            try {
                userRepository.saveUserProfile(user.toEntity())
                userPreferences.saveUserInfo(user.toEntity())
                _userProfile.value = UiState.Success(user)
            } catch (e: Exception) {
                _userProfile.value = UiState.Error(e.toString())
            }
        }
    }
}