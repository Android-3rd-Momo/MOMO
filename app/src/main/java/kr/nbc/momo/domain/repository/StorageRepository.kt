package kr.nbc.momo.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    fun getGroupUri(groupName: String): Flow<Uri>
}