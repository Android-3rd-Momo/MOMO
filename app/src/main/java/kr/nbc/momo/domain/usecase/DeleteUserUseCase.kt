package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.GroupRepository
import javax.inject.Inject
class DeleteUserUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(userId: String, groupId: String): Flow<List<String>> {
        return groupRepository.deleteUser(userId, groupId)
    }
}
