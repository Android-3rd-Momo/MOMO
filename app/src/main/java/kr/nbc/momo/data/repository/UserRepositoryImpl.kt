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
    override suspend fun signUpUser(email: String, password: String, user: UserEntity): UserEntity {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val currentUser = auth.currentUser ?: throw Exception("SignUp Failed")
            val userResponse = user.toUserResponse()
            fireStore.collection("userInfo").document(currentUser.uid).set(userResponse).await()
            userPreferences.saveUserInfo(user) //dataStore
            userResponse.toEntity()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signInUser(email: String, password: String): UserEntity {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val currentUser = auth.currentUser ?: throw Exception("SignIn Failed")
            val snapshot = fireStore.collection("userInfo").document(currentUser.uid).get().await()
            val userResponse = snapshot.toObject(UserResponse::class.java) ?: throw Exception("Do not log in")
            userPreferences.saveUserInfo(userResponse.toEntity()) //dataStore
            userResponse.toEntity()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveUserProfile(user: UserEntity) { //저장
        try {
            val currentUser = auth.currentUser ?: throw Exception("saveProfile Failed")

            val profileImageUrl = if (user.userProfileThumbnailUrl.startsWith("content://")) {
                val uri = Uri.parse(user.userProfileThumbnailUrl)
                uploadStorage(storage.reference.child("userProfile/profile/${user.userId}.jpeg"), uri)
            } else {
                user.userProfileThumbnailUrl
            }

            val backgroundImageUrl = if (user.userBackgroundThumbnailUrl.startsWith("content://")) {
                val uri = Uri.parse(user.userBackgroundThumbnailUrl)
                uploadStorage(storage.reference.child("userProfile/background/${user.userId}.jpeg"), uri)
            } else {
                user.userBackgroundThumbnailUrl
            }

            val portfolioImageUrl = if (user.userPortfolioImageUrl.startsWith("content://")) {
                val uri = Uri.parse(user.userPortfolioImageUrl)
                uploadStorage(storage.reference.child("userProfile/portfolio/${user.userId}.jpeg"), uri)
            } else {
                user.userPortfolioImageUrl
            }

            val updateUser = user.copy(
                userProfileThumbnailUrl = profileImageUrl,
                userBackgroundThumbnailUrl = backgroundImageUrl,
                userPortfolioImageUrl = portfolioImageUrl
            )

            val userResponse = updateUser.toUserResponse()
            fireStore.collection("userInfo").document(currentUser.uid).set(userResponse).await()
            userPreferences.saveUserInfo(user) //dataStore
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun uploadStorage(ref: StorageReference, uri: Uri): String {
        return try {
            val uploadTask = ref.putFile(uri)
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.await()
            urlTask.toString()
        } catch (e: Exception) {
            throw e
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