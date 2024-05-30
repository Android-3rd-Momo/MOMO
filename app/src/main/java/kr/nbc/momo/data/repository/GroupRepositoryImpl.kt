package kr.nbc.momo.data.repository

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kr.nbc.momo.domain.model.GroupEntity
import kr.nbc.momo.domain.repository.GroupRepository
import kr.nbc.momo.domain.usecase.CreateGroupUseCase
import kr.nbc.momo.presentation.group.mapper.asGroupEntity
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
): GroupRepository {
    override fun createGroup(groupEntity: GroupEntity, callback: (Boolean, Exception?) -> Unit) {
        val group = hashMapOf(
            "groupName" to groupEntity.groupName,
            "groupOneLineDescription" to groupEntity.groupOneLineDescription,
            "groupThumbnail" to groupEntity.groupThumbnail,
            "groupDescription" to groupEntity.groupDescription,
            "firstDate" to groupEntity.firstDate,
            "lastDate" to groupEntity.lastDate,
            "leaderId" to groupEntity.leaderId,
            "category" to groupEntity.categoryList,
            "userList" to groupEntity.userList
        )
        fireStore.collection("groups").add(group)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e) }
    }

    override fun readGroup(groupId: String): Flow<GroupEntity> {
        TODO("Not yet implemented")
    }

    override fun updateGroup(groupEntity: GroupEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteGroup(groupId: String) {
        TODO("Not yet implemented")
    }
}