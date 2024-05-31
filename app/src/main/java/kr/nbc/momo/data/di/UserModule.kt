package kr.nbc.momo.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.nbc.momo.data.repository.UserRepositoryImpl
import kr.nbc.momo.domain.repository.UserRepository

@Module
@InstallIn(SingletonComponent::class)
internal interface UserModule {
    @Binds
    fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
}