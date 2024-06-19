package kr.nbc.momo.presentation.group.read

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.ChangeLeaderUseCase
import kr.nbc.momo.domain.usecase.DeleteGroupUseCase
import kr.nbc.momo.domain.usecase.DeleteUserUseCase
import kr.nbc.momo.domain.usecase.ReadGroupUseCase
import kr.nbc.momo.domain.usecase.UpdateGroupUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.asGroupEntity
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

@HiltViewModel
class EditReadGroupViewModel @Inject constructor(
    private val readGroupUseCase: ReadGroupUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val changeLeaderUseCase: ChangeLeaderUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {
    private val _groupState = MutableStateFlow<UiState<GroupModel>>(UiState.Loading)
    val groupState: StateFlow<UiState<GroupModel>> get() = _groupState

    private val _updateState = MutableStateFlow<UiState<GroupModel>>(UiState.Loading)
    val updateState: StateFlow<UiState<GroupModel>> get() = _updateState

    private val _subscriptionState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val subscriptionState: StateFlow<UiState<Boolean>> get() = _subscriptionState

    private val _deleteGroupState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val deleteGroupState: StateFlow<UiState<Boolean>> get() = _deleteGroupState


    private val _leaderChangeState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val leaderChangeState: StateFlow<UiState<Boolean>> get() = _leaderChangeState

    private val _userDeleteState = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val userDeleteState: StateFlow<UiState<List<String>>> get() = _userDeleteState

    fun readGroup(groupId: String) {
        viewModelScope.launch {
            _groupState.value = UiState.Loading

            readGroupUseCase.invoke(groupId)
                .catch { e ->
                    _groupState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    if (data.groupId == "error") {
                        _groupState.value = UiState.Error("error")
                    } else {
                        _groupState.value = UiState.Success(data.toGroupModel())
                    }
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

    fun deleteGroup(groupId: String, userList: List<String>) {
        viewModelScope.launch {
            _deleteGroupState.value = UiState.Loading

            deleteGroupUseCase.invoke(groupId, userList)
                .catch { e ->
                    _deleteGroupState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _deleteGroupState.value = UiState.Success(data)
                }
        }
    }


    fun leaderChange(groupId: String, leaderId: String) {
        viewModelScope.launch {
            _leaderChangeState.value = UiState.Loading

            changeLeaderUseCase.invoke(groupId, leaderId)
                .catch { e ->
                    _leaderChangeState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _leaderChangeState.value = UiState.Success(data)
                }
        }
    }

    fun deleteUser(userId: String, groupId: String) {
        viewModelScope.launch {
            _userDeleteState.value = UiState.Loading

            deleteUserUseCase.invoke(userId, groupId)
                .catch { e ->
                    _userDeleteState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _userDeleteState.value = UiState.Success(data)
                }
        }
    }
}
