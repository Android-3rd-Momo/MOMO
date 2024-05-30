package kr.nbc.momo.data.di

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FireBaseModule {
    @Provides
    fun provideFireBaseDataBase(): FirebaseDatabase{
        val firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.setPersistenceEnabled(true)
        firebaseDatabase.setPersistenceCacheSizeBytes(5000000)
        return firebaseDatabase
    }


    @Provides
    fun provideFireBaseFireStore(): FirebaseFirestore {
        val settings = FirebaseFirestoreSettings.Builder() //todo 캐시 설정
            .build()
        val firebaseFireStore = FirebaseFirestore.getInstance()
        firebaseFireStore.firestoreSettings = settings
        return firebaseFireStore
    }
}