package kr.nbc.momo.data.di

import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FireBaseModule {
    private val firebaseDatabase = FirebaseDatabase.getInstance().apply {
        setPersistenceEnabled(true)
        setPersistenceCacheSizeBytes(1024 * 1024 * 100)
    }
    @Provides
    fun provideFireBaseDataBase(): FirebaseDatabase {
        return firebaseDatabase
    }
    @Provides
    fun provideFireBaseFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage("gs://moigae.appspot.com")
    }
}