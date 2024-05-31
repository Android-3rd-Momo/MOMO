package kr.nbc.momo.presentation.home.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.GetGroupListUseCase
import kr.nbc.momo.domain.usecase.ReadGroupUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGroupListUseCase: GetGroupListUseCase
) : ViewModel() {
    private val _getGroupList =  MutableStateFlow<UiState<List<GroupModel>>>(UiState.Loading)
    val getGroupList: StateFlow<UiState<List<GroupModel>>> get() = _getGroupList

    init {
        getGroupList()
    }
    private fun getGroupList() {
        viewModelScope.launch {
            _getGroupList.value = UiState.Loading

            getGroupListUseCase.invoke()
                .catch { e ->
                    _getGroupList.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _getGroupList.value = UiState.Success(data.map { it.toGroupModel() })
                }
        }
    }

}
