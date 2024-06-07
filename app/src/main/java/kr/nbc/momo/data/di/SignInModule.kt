package kr.nbc.momo.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.nbc.momo.data.repository.SignInRepositoryImpl
import kr.nbc.momo.domain.repository.SignInRepository

@Module
@InstallIn(SingletonComponent::class)
internal interface SignInModule {
    @Binds
    fun bindSignInRepository(signInRepositoryImpl: SignInRepositoryImpl): SignInRepository
}