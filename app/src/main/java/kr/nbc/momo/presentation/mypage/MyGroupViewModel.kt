package kr.nbc.momo.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.AddUserUseCase
import kr.nbc.momo.domain.usecase.GetAppliedUseCase
import kr.nbc.momo.domain.usecase.GetSubscriptionListUseCase
import kr.nbc.momo.domain.usecase.GetUserGroupListUseCase
import kr.nbc.momo.domain.usecase.RejectionSubscriptionUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

@HiltViewModel
class MyGroupViewModel @Inject constructor(
    private val getSubscriptionListUseCase: GetSubscriptionListUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val getUserGroupListUseCase: GetUserGroupListUseCase,
    private val getAppliedUseCase: GetAppliedUseCase,
    private val rejectionSubscriptionUseCase: RejectionSubscriptionUseCase
) : ViewModel() {

    private val _subscriptionListState = MutableStateFlow<UiState<List<GroupModel>>>(UiState.Loading)
    val subscriptionListState: StateFlow<UiState<List<GroupModel>>> get() = _subscriptionListState

    private val _userGroupList = MutableStateFlow<UiState<List<GroupModel>>>(UiState.Loading)
    val userGroupList: StateFlow<UiState<List<GroupModel>>> get() = _userGroupList

    private val _userAppliedGroupList = MutableStateFlow<UiState<List<GroupModel>>>(UiState.Loading)
    val userAppliedGroupList: StateFlow<UiState<List<GroupModel>>> get() = _userAppliedGroupList

    fun getSubscriptionList(userId: String) {
        viewModelScope.launch {
            _subscriptionListState.value = UiState.Loading

            getSubscriptionListUseCase.invoke(userId)
                .catch { e ->
                    _subscriptionListState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _subscriptionListState.value = UiState.Success(data.map { it.toGroupModel() })
                }

        }
    }

    fun getAppliedGroupList(userId: String) {
        viewModelScope.launch {
            _userAppliedGroupList.value = UiState.Loading

            getAppliedUseCase.invoke(userId)
                .catch { e ->
                    _userAppliedGroupList.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _userAppliedGroupList.value = UiState.Success(data.map { it.toGroupModel() })
                }

        }
    }


    fun getUserGroup(groupList: List<String>, userId: String) {
        viewModelScope.launch {
            _userGroupList.value = UiState.Loading

            getUserGroupListUseCase.invoke(groupList, userId)
                .catch { e ->
                    _userGroupList.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _userGroupList.value = UiState.Success(data.map { it.toGroupModel() })
                }

        }
    }

    fun addUser(userId: String, groupId: String) {
        viewModelScope.launch {
            addUserUseCase.invoke(userId, groupId)
        }
    }

    fun rejectUser(userId: String, groupId: String) {
        viewModelScope.launch {
            rejectionSubscriptionUseCase.invoke(userId, groupId)
        }
    }
}