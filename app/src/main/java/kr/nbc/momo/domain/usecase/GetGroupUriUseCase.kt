package kr.nbc.momo.domain.usecase

import android.net.Uri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.repository.StorageRepository
import javax.inject.Inject
class GetGroupUriUseCase@Inject constructor(
    private val storageRepository: StorageRepository
) {
    fun invoke(groupName: String): Flow<Uri> {
        return storageRepository.getGroupUri(groupName)
    }
}