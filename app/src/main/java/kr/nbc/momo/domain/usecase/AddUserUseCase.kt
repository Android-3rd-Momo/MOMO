package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject
class AddUserUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend fun invoke(userId: String, groupId: String): Flow<Boolean> {
        return groupRepository.addUser(userId, groupId)
    }
}
