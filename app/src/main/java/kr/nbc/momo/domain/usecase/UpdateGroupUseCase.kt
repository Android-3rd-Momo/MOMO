package kr.nbc.momo.domain.usecase

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject
class UpdateGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend fun invoke(groupEntity: GroupEntity, imageUri: Uri?) {
        return groupRepository.updateGroup(groupEntity, imageUri)
    }
}
