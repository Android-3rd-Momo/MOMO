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

    fun addSelectedTypeChipId(chipId: String) {
        _selectedTypeChipIds.value = _selectedTypeChipIds.value + chipId
    }

    fun removeSelectedTypeChipId(chipId: String) {
        _selectedTypeChipIds.value = _selectedTypeChipIds.value - chipId
    }

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                if (user != null) {
                    _currentUser.value = UiState.Success(user.toModel())
                } else {
                    _currentUser.value = UiState.Error("")
                }
            }
        }
    }

    fun updateTypeOfDevelopment(types:List<String>){
        _typeOfDevelopment.value = types
    }

    fun updateProgramOfDevelopment(programs: List<String>){
        _programOfDevelopment.value = programs
    }

    fun updateStackOfDevelopment(stack: String) {
        _stackOfDevelopment.value = stack
    }

    fun saveUserProfile(){
        viewModelScope.launch{
            _authState.value = UiState.Loading

            try{
                val currentUser = (_currentUser.value as? UiState.Success)?.data
                if(currentUser != null){
                    val updatedUser = currentUser.copy(
                        typeOfDevelopment = _typeOfDevelopment.value,
                        programOfDevelopment = _programOfDevelopment.value,
                        stackOfDevelopment = _stackOfDevelopment.value
                    )
                    saveUserProfileUseCase(updatedUser.toEntity())
                    _authState.value = UiState.Success(updatedUser)
                    updateUser(updatedUser)
                } else {
                    _authState.value = UiState.Error("")
                }
            } catch (e: Exception) {
                _authState.value = UiState.Error("")
            }
        }
    }

    fun updateUser(user: UserModel) {
        _currentUser.value = UiState.Success(user)
    }

    fun clearChipData() {
        _typeOfDevelopment.value = emptyList()
        _programOfDevelopment.value = emptyList()
        _stackOfDevelopment.value = ""
    }

}