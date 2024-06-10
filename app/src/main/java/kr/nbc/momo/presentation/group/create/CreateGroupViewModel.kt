package kr.nbc.momo.presentation.group.create

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.nbc.momo.domain.usecase.CreateGroupUseCase
import kr.nbc.momo.presentation.group.mapper.asGroupEntity
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewModel  @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase
) : ViewModel() {
    suspend fun createGroup(groupModel: GroupModel) {
        createGroupUseCase.invoke(groupModel.asGroupEntity())
    }
}