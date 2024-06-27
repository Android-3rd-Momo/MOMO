package kr.nbc.momo.domain.usecase

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.StorageRepository
import javax.inject.Inject

class GetGroupUriUseCase@Inject constructor(
    private val storageRepository: StorageRepository
) {
    operator fun invoke(groupName: String): Flow<Uri> {
        return storageRepository.getGroupUri(groupName)
    }
}