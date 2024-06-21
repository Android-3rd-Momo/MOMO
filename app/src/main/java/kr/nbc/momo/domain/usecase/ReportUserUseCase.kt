package kr.nbc.momo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject
class ReportUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun invoke(reportedUser: String) {
        return userRepository.reportUser(reportedUser)
    }
}