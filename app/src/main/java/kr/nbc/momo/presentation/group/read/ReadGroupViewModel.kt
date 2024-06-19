package kr.nbc.momo.presentation.group.read

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.JoinGroupUseCase
import kr.nbc.momo.domain.usecase.BlockUserUseCase
import kr.nbc.momo.domain.usecase.ChangeLeaderUseCase
import kr.nbc.momo.domain.usecase.DeleteGroupUseCase
import kr.nbc.momo.domain.usecase.DeleteUserUseCase
import kr.nbc.momo.domain.usecase.ReadGroupUseCase
import kr.nbc.momo.domain.usecase.ReportUserUseCase
import kr.nbc.momo.domain.usecase.UpdateGroupUseCase
import kr.nbc.momo.domain.usecase.AddUserUseCase
import kr.nbc.momo.domain.usecase.SubscriptionUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.asGroupEntity
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

@HiltViewModel
class ReadGroupViewModel @Inject constructor(
    private val readGroupUseCase: ReadGroupUseCase,
    private val subscriptionUseCase: SubscriptionUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val reportUserUseCase: ReportUserUseCase,
    private val blockUserUseCase: BlockUserUseCase,
) : ViewModel() {
    private val _groupState = MutableStateFlow<UiState<GroupModel>>(UiState.Loading)
    val groupState: StateFlow<UiState<GroupModel>> get() = _groupState

    private val _subscriptionState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val subscriptionState: StateFlow<UiState<Boolean>> get() = _subscriptionState

    private val _deleteGroupState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val deleteGroupState: StateFlow<UiState<Boolean>> get() = _deleteGroupState

    private val _reportUserState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val reportUserState: StateFlow<UiState<Boolean>> get() = _reportUserState

    private val _blockUserState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val blockUserState: StateFlow<UiState<Boolean>> get() = _blockUserState

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

    fun subscription(userId: String, groupId: String) {
        viewModelScope.launch {
            _subscriptionState.value = UiState.Loading

            subscriptionUseCase.invoke(userId, groupId)
                .catch { e ->
                    _subscriptionState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _subscriptionState.value = UiState.Success(data)
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

    fun reportUser(reportedUser: String) {
        viewModelScope.launch {
            _reportUserState.value = UiState.Loading

            reportUserUseCase.invoke(reportedUser)
                .catch { e ->
                    _reportUserState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _reportUserState.value = UiState.Success(data)
                }
        }
    }

    fun blockUser(blockUser: String) {
        viewModelScope.launch {
            _blockUserState.value = UiState.Loading

            blockUserUseCase.invoke(blockUser)
                .catch { e ->
                    _blockUserState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _blockUserState.value = UiState.Success(data)
                }
        }
    }


}
