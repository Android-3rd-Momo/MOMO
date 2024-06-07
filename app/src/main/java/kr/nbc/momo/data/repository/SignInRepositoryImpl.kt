package kr.nbc.momo.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kr.nbc.momo.domain.model.SignInEntity
import kr.nbc.momo.domain.repository.SignInRepository
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : SignInRepository {

    override suspend fun login(email: String, password: String): SignInEntity {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val currentUser = auth.currentUser ?: throw Exception("SignIn Failed")
            SignInEntity(
                uId = currentUser.uid,
                userPassword = password,
                userName = currentUser.displayName ?: "",
                userEmail = currentUser.email ?: "",
                isFirstLaunch = true, // Set to default or fetch from a data source
                isLoggedIn = true // Mark user as logged in
            )
        } catch (e: Exception) {
            throw e
        }
    }
}