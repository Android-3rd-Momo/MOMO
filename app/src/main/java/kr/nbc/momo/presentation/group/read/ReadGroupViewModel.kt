package kr.nbc.momo.presentation.group.read

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.BlockUserUseCase
import kr.nbc.momo.domain.usecase.DeleteGroupUseCase
import kr.nbc.momo.domain.usecase.DeleteUserUseCase
import kr.nbc.momo.domain.usecase.ReadGroupUseCase
import kr.nbc.momo.domain.usecase.ReportUserUseCase
import kr.nbc.momo.domain.usecase.SubscriptionUseCase
import kr.nbc.momo.presentation.UiState
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
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {
    private val _groupState = MutableStateFlow<UiState<GroupModel>>(UiState.Loading)
    val groupState: StateFlow<UiState<GroupModel>> get() = _groupState

    private val _userDeleteState = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val userDeleteState: StateFlow<UiState<List<String>>> get() = _userDeleteState

    fun readGroup(groupId: String) {
        viewModelScope.launch {
            _groupState.value = UiState.Loading

            readGroupUseCase(groupId)
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

    fun deleteUser(userId: String, groupId: String) {
        viewModelScope.launch {
            _userDeleteState.value = UiState.Loading

            deleteUserUseCase(userId, groupId)
                .catch { e ->
                    _userDeleteState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _userDeleteState.value = UiState.Success(data)
                }
        }
    }

    fun subscription(userId: String, groupId: String) {
        viewModelScope.launch {
            subscriptionUseCase(userId, groupId)
        }
    }


    fun deleteGroup(groupId: String, userList: List<String>) {
        viewModelScope.launch {
            deleteGroupUseCase(groupId, userList)
        }
    }

    fun reportUser(reportedUser: String) {
        viewModelScope.launch {
            reportUserUseCase(reportedUser)
        }
    }

    fun blockUser(blockUser: String) {
        viewModelScope.launch {
            blockUserUseCase(blockUser)
        }
    }


}
