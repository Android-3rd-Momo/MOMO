package kr.nbc.momo.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.data.datastore.UserPreferences
import kr.nbc.momo.data.model.UserResponse
import kr.nbc.momo.data.model.toEntity
import kr.nbc.momo.data.model.toUserResponse
import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val userPreferences: UserPreferences //todo 보류
) : UserRepository {

    //todo 중복으로 로그인 된 유저를 호출하는 부분 메서드 생성
    //auth.currentUser.uid

    //todo signUp, save 부분 줄이기 -> 메서드 생성
    private suspend fun getCurrentUserUid(): String {
        return auth.currentUser?.uid ?: throw Exception("User not login")
    }

    private suspend fun saveUserInfo(user: UserEntity) { //signUp, saveUserProfile
        val currentUserUid = getCurrentUserUid()
        val userResponse = user.toUserResponse()
        fireStore.collection("userInfo").document(currentUserUid).set(userResponse).await()
        userPreferences.saveUserInfo(user)
    }

    override suspend fun signUpUser(email: String, password: String, user: UserEntity): UserEntity {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            saveUserInfo(user)
            user
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signInUser(email: String, password: String): UserEntity {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val currentUserUid = getCurrentUserUid()
            val snapshot = fireStore.collection("userInfo").document(currentUserUid).get().await()
            val userResponse = snapshot.toObject(UserResponse::class.java) ?: throw Exception("Do not log in")
            userPreferences.saveUserInfo(userResponse.toEntity()) //dataStore
            userResponse.toEntity()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveUserProfile(user: UserEntity) { //저장
        try {
            val updateUser = user.copy(
                userProfileThumbnailUrl = uploadImageToStorage(user.userProfileThumbnailUrl,"profile"),
                userBackgroundThumbnailUrl = uploadImageToStorage(user.userBackgroundThumbnailUrl,"background"),
                userPortfolioImageUrl = uploadImageToStorage(user.userPortfolioImageUrl, "portfolio")
            )
            saveUserInfo(updateUser)
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun uploadImageToStorage(imageUrl: String, path: String): String {
        return if (imageUrl.startsWith("content://")) {
            val uri = Uri.parse(imageUrl)
            try {
                val ref = storage.reference.child("userProfile/$path/${getCurrentUserUid()}.jpeg")
                ref.putFile(uri).await()
                ref.downloadUrl.await().toString()
            } catch (e: Exception) {
                throw e
            }
        } else{
            imageUrl
        }
    }

        override suspend fun isUserIdDuplicate(userId: String): Boolean {
            return try {
                val querySnapshot = fireStore.collection("userInfo")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                !querySnapshot.isEmpty
            } catch (e: Exception) {
                throw e
            }
        }

        override fun getCurrentUser(): Flow<UserEntity?> = callbackFlow {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val listenerRegistration = fireStore.collection("userInfo").document(currentUser.uid)
                    .addSnapshotListener { snapshot, e -> //실시간 업데이트
                        if (e != null) {
                            close(e)
                            return@addSnapshotListener
                        }
                        val userEntity = snapshot?.toObject(UserResponse::class.java)?.toEntity()
                        trySend(userEntity).isSuccess
                    }
                awaitClose { listenerRegistration.remove() }
            } else {
                trySend(null).isSuccess
                close()
            }
        }
    }