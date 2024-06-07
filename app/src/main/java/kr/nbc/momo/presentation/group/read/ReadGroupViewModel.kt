package kr.nbc.momo.presentation.group.read

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
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
    private val _readGroup =  MutableStateFlow<UiState<GroupModel>>(UiState.Loading)
    val readGroup: StateFlow<UiState<GroupModel>> get() = _readGroup

    fun readGroup(groupId: String) {
        viewModelScope.launch {
            _readGroup.value = UiState.Loading

            readGroupUseCase.invoke(groupId)
                .catch { e ->
                    _readGroup.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _readGroup.value = UiState.Success(data.toGroupModel())
                }
        }
    }

    fun addUser(userList : List<String>, groupId: String) {
        updateGroupUserListUseCase.invoke(userList, groupId)
    }

    fun updateGroup(groupModel: GroupModel) {
        updateGroupUseCase.invoke(groupModel.asGroupEntity())
    }

    fun deleteGroup(groupId: String) {
        deleteGroupUseCase.invoke(groupId)
    }

}
