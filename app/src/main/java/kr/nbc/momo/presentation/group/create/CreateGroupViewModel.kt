package kr.nbc.momo.presentation.group.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.CreateGroupUseCase
import kr.nbc.momo.domain.usecase.JoinGroupUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.asGroupEntity
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewModel  @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase,
    private val joinGroupUseCase: JoinGroupUseCase
) : ViewModel() {

    private val _createState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val createState: StateFlow<UiState<Boolean>> get() = _createState

    fun createGroup(groupModel: GroupModel) {
        viewModelScope.launch {
            _createState.value = UiState.Loading

            createGroupUseCase.invoke(groupModel.asGroupEntity())
                .catch { e ->
                    _createState.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _createState.value = UiState.Success(data)
                }

        }
    }

    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            joinGroupUseCase.invoke(groupId)
        }
    }
}