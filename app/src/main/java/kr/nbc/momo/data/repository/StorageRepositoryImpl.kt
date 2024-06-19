package kr.nbc.momo.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kr.nbc.momo.domain.repository.StorageRepository
import javax.inject.Inject
import kotlin.coroutines.resume

class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
) : StorageRepository {
    override fun getGroupUri(groupName: String): Flow<Uri> = flow {
        val storageReference = storage.reference.child("$groupName.jpeg")
        val uri = suspendCancellableCoroutine<Uri> { continuation ->
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    continuation.resume(uri)
                }
                .addOnFailureListener {

                }
        }
        emit(uri)
    }
}