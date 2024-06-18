package kr.nbc.momo.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.nbc.momo.data.repository.StorageRepositoryImpl
import kr.nbc.momo.domain.repository.StorageRepository

@Module
@InstallIn(SingletonComponent::class)
internal interface StorageModule {
    @Binds
    fun bindsStorageRepository(storageRepositoryImpl: StorageRepositoryImpl) : StorageRepository
}