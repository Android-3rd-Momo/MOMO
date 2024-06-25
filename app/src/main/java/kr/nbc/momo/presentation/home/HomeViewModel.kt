package kr.nbc.momo.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.GetGroupListUseCase
import kr.nbc.momo.domain.usecase.GetNotificationCountUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGroupListUseCase: GetGroupListUseCase,
    private val getNotificationCountUseCase: GetNotificationCountUseCase
) : ViewModel() {
    private val _getGroupList = MutableStateFlow<UiState<List<GroupModel>>>(UiState.Loading)
    val getGroupList: StateFlow<UiState<List<GroupModel>>> get() = _getGroupList

    private val _getNotificationCount = MutableStateFlow<UiState<Int>>(UiState.Loading)
    val getNotificationCount: StateFlow<UiState<Int>> get() = _getNotificationCount

    init {
        getGroupList()
    }
    fun getGroupList() {
        viewModelScope.launch {
            getGroupListUseCase()
                .catch { e ->
                    Log.e("HomeViewModel", "Error fetching group list", e)
                    _getGroupList.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _getGroupList.value = UiState.Success(data.map { it.toGroupModel() })
                }
        }
    }

    fun getNotificationCount(userId: String) {
        viewModelScope.launch {
            getNotificationCountUseCase(userId)
                .catch { e ->
                    Log.e("HomeViewModel", "Error fetching group list", e)
                    _getNotificationCount.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _getNotificationCount.value = UiState.Success(data)
                }
        }
    }
}