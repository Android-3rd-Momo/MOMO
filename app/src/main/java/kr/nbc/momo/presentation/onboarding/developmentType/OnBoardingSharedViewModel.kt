package kr.nbc.momo.presentation.onboarding.developmentType

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.GetCurrentUserUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.onboarding.signup.model.UserModel
import kr.nbc.momo.presentation.onboarding.signup.model.toModel
import javax.inject.Inject

@HiltViewModel
class OnBoardingSharedViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _typeOfDevelopment = MutableStateFlow<List<String>>(emptyList())

    private val _programOfDevelopment = MutableStateFlow<List<String>>(emptyList())

    private val _stackOfDevelopment = MutableStateFlow<String>("")
    val stackOfDevelopment: StateFlow<String> get() = _stackOfDevelopment

    private val _currentUser = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val currentUser: StateFlow<UiState<UserModel>> get() = _currentUser
    private val _saveProfileState = MutableStateFlow<UiState<Unit>>(UiState.Loading)

    private val _selectedTypeChipIds = MutableStateFlow<List<String>>(emptyList())

    private val _selectedProgramChipIds = MutableStateFlow<List<String>>(emptyList())

    fun addSelectedTypeChipId(chipId: String) {
        _selectedTypeChipIds.value += chipId
    }

    fun removeSelectedTypeChipId(chipId: String) {
        _selectedTypeChipIds.value -= chipId
    }

    fun addSelectedProgramChipId(chipId: String) {
        _selectedProgramChipIds.value += chipId
    }

    fun removeSelectedProgramChipId(chipId: String) {
        _selectedProgramChipIds.value -= chipId
    }

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _currentUser.value = if (user != null) {
                    UiState.Success(user.toModel())
                } else {
                    UiState.Error("User not found")
                }
            }
        }
    }

    fun updateStackOfDevelopment(stack: String) {
        _stackOfDevelopment.value = stack
    }

    fun saveUserProfile() {
        viewModelScope.launch {
            _saveProfileState.value = UiState.Loading
            try {
                val currentUser = (_currentUser.value as? UiState.Success)?.data
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        typeOfDevelopment = _selectedTypeChipIds.value,
                        programOfDevelopment = _selectedProgramChipIds.value,
                        stackOfDevelopment = _stackOfDevelopment.value
                    )
                    _saveProfileState.value = UiState.Success(Unit)
                    updateUser(updatedUser)
                }
            } catch (e: Exception) {
                _saveProfileState.value = UiState.Error("Failed to save user profile: ${e.message}")
            }
        }
    }

    private fun updateUser(user: UserModel) {
        Log.d("onBoardingSharedViewModel UserInfo", "$user")
        _currentUser.value = UiState.Success(user)
    }

    fun clearTemporaryData() {
        _typeOfDevelopment.value = emptyList()
        _programOfDevelopment.value = emptyList()
        _stackOfDevelopment.value = ""
        _selectedTypeChipIds.value = emptyList()
        _selectedProgramChipIds.value = emptyList()
    }
}
