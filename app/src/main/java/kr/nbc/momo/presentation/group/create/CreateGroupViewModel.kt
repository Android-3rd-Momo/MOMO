package kr.nbc.momo.presentation.group.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.CreateGroupUseCase
import kr.nbc.momo.domain.usecase.JoinGroupUseCase
import kr.nbc.momo.presentation.group.mapper.asGroupEntity
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewModel  @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase,
    private val joinGroupUseCase: JoinGroupUseCase
) : ViewModel() {

    fun createGroup(groupModel: GroupModel) {
        viewModelScope.launch {
            createGroupUseCase(groupModel.asGroupEntity())
        }
    }

    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            joinGroupUseCase(groupId)
        }
    }
}