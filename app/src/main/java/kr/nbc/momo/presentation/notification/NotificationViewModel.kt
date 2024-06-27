package kr.nbc.momo.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.AddUserUseCase
import kr.nbc.momo.domain.usecase.GetSubscriptionListUseCase
import kr.nbc.momo.domain.usecase.RejectionSubscriptionUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getSubscriptionListUseCase: GetSubscriptionListUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val rejectionSubscriptionUseCase: RejectionSubscriptionUseCase
) : ViewModel() {

    private val _subscriptionListState = MutableStateFlow<UiState<List<GroupModel>>>(UiState.Loading)
    val subscriptionListState: StateFlow<UiState<List<GroupModel>>> get() = _subscriptionListState

    fun getSubscriptionList(userId: String) {
        viewModelScope.launch {
            _subscriptionListState.value = UiState.Loading

            getSubscriptionListUseCase(userId)
                .catch { e ->
                    _subscriptionListState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _subscriptionListState.value = UiState.Success(data.map { it.toGroupModel() })
                }

        }
    }

    fun addUser(userId: String, groupId: String) {
        viewModelScope.launch {
            addUserUseCase(userId, groupId)
        }
    }

    fun rejectUser(userId: String, groupId: String) {
        viewModelScope.launch {
            rejectionSubscriptionUseCase(userId, groupId)
        }
    }
}