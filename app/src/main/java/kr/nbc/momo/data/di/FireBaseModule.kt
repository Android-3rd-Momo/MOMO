package kr.nbc.momo.data.di

import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
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
    fun provideFireStore(): FirebaseFirestore {
        val firebaseFirestore = Firebase.firestore
        return firebaseFirestore
    }
}