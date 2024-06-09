package kr.nbc.momo.presentation.onboarding.developmentType

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.usecase.GetCurrentUserUseCase
import kr.nbc.momo.domain.usecase.SaveUserProfileUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.signup.model.UserModel
import kr.nbc.momo.presentation.signup.model.toEntity
import kr.nbc.momo.presentation.signup.model.toModel
import javax.inject.Inject

@HiltViewModel
class OnBoardingSharedViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase
) : ViewModel() {

    private val _typeOfDevelopment = MutableStateFlow<List<String>>(emptyList())
    val typeOfDevelopment: StateFlow<List<String>> get() = _typeOfDevelopment

    private val _programOfDevelopment = MutableStateFlow<List<String>>(emptyList())
    val programOfDevelopment: StateFlow<List<String>> get() = _programOfDevelopment

    private val _stackOfDevelopment = MutableStateFlow<String>("")
    val stackOfDevelopment: StateFlow<String> get() = _stackOfDevelopment

    private val _currentUser = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val currentUser: StateFlow<UiState<UserModel>> get() = _currentUser

    private val _authState = MutableStateFlow<UiState<UserModel>>(UiState.Loading)
    val authState: StateFlow<UiState<UserModel>> get() = _authState

    private val _selectedTypeChipIds = MutableStateFlow<List<String>>(emptyList())
    val selectedTypeChipIds: StateFlow<List<String>> get() = _selectedTypeChipIds

    private val _selectedProgramChipIds = MutableStateFlow<List<String>>(emptyList())
    val selectedTypeProgramIds: StateFlow<List<String>> get() = _selectedTypeChipIds

    // Adds a chip ID to the selected list
    fun addSelectedTypeChipId(chipId: String) {
        _selectedTypeChipIds.value = _selectedTypeChipIds.value + chipId
    }

    // Removes a chip ID from the selected list
    fun removeSelectedTypeChipId(chipId: String) {
        _selectedTypeChipIds.value = _selectedTypeChipIds.value - chipId
    }

    fun updateSelectedTypeChipIds(chipIds: List<String>) {
        _selectedTypeChipIds.value = chipIds
    }

    fun addSelectedProgramChipId(chipId: String) {
        _selectedTypeChipIds.value = _selectedTypeChipIds.value + chipId
    }

    // Removes a chip ID from the selected list
    fun removeSelectedPregramChipId(chipId: String) {
        _selectedTypeChipIds.value = _selectedTypeChipIds.value - chipId
    }

    fun updateSelectedProgramchips(chipIds: List<String>) {
        _selectedTypeChipIds.value = chipIds
    }

    // Initializes the ViewModel by fetching the current user
    init {
        getCurrentUser()
    }

    // Fetches the current user using the use case
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

    // Updates the type of development
    fun updateTypeOfDevelopment(types: List<String>) {
        _typeOfDevelopment.value = types
    }

    // Updates the program of development
    fun updateProgramOfDevelopment(programs: List<String>) {
        _programOfDevelopment.value = programs
    }

    // Updates the stack of development
    fun updateStackOfDevelopment(stack: String) {
        _stackOfDevelopment.value = stack
    }

    // Saves the user profile with updated details
    fun saveUserProfile() {
        viewModelScope.launch {
            _authState.value = UiState.Loading

            try {
                val currentUser = (_currentUser.value as? UiState.Success)?.data
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        typeOfDevelopment = _typeOfDevelopment.value,
                        programOfDevelopment = _programOfDevelopment.value,
                        stackOfDevelopment = _stackOfDevelopment.value
                    )
                    saveUserProfileUseCase(updatedUser.toEntity())
                    _authState.value = UiState.Success(updatedUser)
                    updateUser(updatedUser)
                } else {
                    _authState.value = UiState.Error("User not found")
                }
            } catch (e: Exception) {
                _authState.value = UiState.Error("Failed to save user profile: ${e.message}")
            }
        }
    }

    // Updates the current user state
    fun updateUser(user: UserModel) {
        _currentUser.value = UiState.Success(user)
    }

    // Clears all chip data
    fun clearChipData() {
        _typeOfDevelopment.value = emptyList()
        _programOfDevelopment.value = emptyList()
        _stackOfDevelopment.value = ""
    }

}