package kr.nbc.momo.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.nbc.momo.data.repository.GroupRepositoryImpl
import kr.nbc.momo.domain.repository.GroupRepository

@Module
@InstallIn(SingletonComponent::class)
internal interface GroupModule {
    @Binds
    fun bindsGroupRepository(groupRepositoryImpl: GroupRepositoryImpl) : GroupRepository
}