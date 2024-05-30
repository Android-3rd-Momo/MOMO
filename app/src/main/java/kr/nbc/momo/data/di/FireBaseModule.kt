package kr.nbc.momo.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
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
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {}) //메모리 캐시 설정
            setLocalCacheSettings(persistentCacheSettings {}) //종료 후에도 캐시 데이터 유지
//            setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED) //캐시크기 무제한 설정
        }
        val firebaseFireStore = FirebaseFirestore.getInstance()
        firebaseFireStore.firestoreSettings = settings
        return firebaseFireStore
    }
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}