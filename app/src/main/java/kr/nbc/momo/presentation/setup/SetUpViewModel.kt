package kr.nbc.momo.presentation.setup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.SearchLeaderUseCase
import kr.nbc.momo.domain.usecase.SignOutUserCase
import kr.nbc.momo.domain.usecase.SignWithdrawalUserUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import javax.inject.Inject

@HiltViewModel
class SetUpViewModel @Inject constructor(
    private val signOutUserUseCase: SignOutUserCase,
    private val signWithdrawalUserUseCase: SignWithdrawalUserUseCase,
    private val searchLeaderUseCase: SearchLeaderUseCase
) : ViewModel() {

    private val _searchLeaderState = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val searchLeaderState: StateFlow<UiState<List<String>>> get() = _searchLeaderState

    fun searchLeader(userId: String) {
        viewModelScope.launch {
            _searchLeaderState.value = UiState.Loading

            searchLeaderUseCase.invoke(userId)
                .catch { e ->
                    _searchLeaderState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _searchLeaderState.value = UiState.Success(data)
                }
        }
    }

    fun signOut(){
        viewModelScope.launch {
            try {
                signOutUserUseCase()
            }catch (e:Exception){
                Log.d("error", e.toString())
            }
        }
    }

    fun withdrawal() {
        viewModelScope.launch {
            try {
                signWithdrawalUserUseCase()
            } catch (e: Exception) {
                Log.d("error", e.toString())
            }
        }
    }
}