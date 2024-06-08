package kr.nbc.momo.presentation.group.read

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.usecase.DeleteGroupUseCase
import kr.nbc.momo.domain.usecase.ReadGroupUseCase
import kr.nbc.momo.domain.usecase.UpdateGroupUseCase
import kr.nbc.momo.domain.usecase.UpdateGroupUserListUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.asGroupEntity
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

@HiltViewModel
class ReadGroupViewModel  @Inject constructor(
    private val readGroupUseCase: ReadGroupUseCase,
    private val updateGroupUserListUseCase: UpdateGroupUserListUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase
) : ViewModel() {
    private val _groupState = MutableStateFlow<UiState<GroupModel>>(UiState.Loading)
    val groupState: StateFlow<UiState<GroupModel>> get() = _groupState

    private val _updateState = MutableStateFlow<UiState<GroupModel>>(UiState.Loading)
    val updateState: StateFlow<UiState<GroupModel>> get() = _updateState

    private val _userListState = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val userListState: StateFlow<UiState<List<String>>> get() = _userListState

    private val _deleteGroupState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val deleteGroupState: StateFlow<UiState<Boolean>> get() = _deleteGroupState

    fun readGroup(groupId: String) {
        viewModelScope.launch {
            _groupState.value = UiState.Loading

            readGroupUseCase.invoke(groupId)
                .catch { e ->
                    _groupState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _groupState.value = UiState.Success(data.toGroupModel())
                }
        }
    }

    fun addUser(userList: List<String>, groupId: String) {
        viewModelScope.launch {
            _userListState.value = UiState.Loading

            updateGroupUserListUseCase.invoke(userList, groupId)
                .catch { e ->
                    _userListState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _userListState.value = UiState.Success(data)
                }
        }

    }

    fun updateGroup(groupModel: GroupModel, imageUri: Uri?) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading

            updateGroupUseCase.invoke(groupModel.asGroupEntity(), imageUri)
                .catch { e ->
                    _updateState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _updateState.value = UiState.Success(data.toGroupModel())
                }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            _deleteGroupState.value = UiState.Loading

            deleteGroupUseCase.invoke(groupId)
                .catch { e ->
                    _deleteGroupState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _deleteGroupState.value = UiState.Success(data)
                }
        }
    }
}
