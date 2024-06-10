package kr.nbc.momo.presentation.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.SignOutUserCase
import kr.nbc.momo.domain.usecase.SignWithdrawalUserUseCase
import kr.nbc.momo.presentation.UiState
import javax.inject.Inject

@HiltViewModel
class SetUpViewModel @Inject constructor(
    private val signOutUserUseCase: SignOutUserCase,
    private val signWithdrawalUserUseCase: SignWithdrawalUserUseCase
) : ViewModel() {

    fun signOut(){
        viewModelScope.launch {
            try {
                signOutUserUseCase()
            }catch (e:Exception){
            }
        }
    }

    fun withdrawal() {
        viewModelScope.launch {
            try {
                signWithdrawalUserUseCase()
            } catch (e: Exception) {
            }
        }
    }
}