package kr.nbc.momo.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.nbc.momo.data.repository.ChatRepositoryImpl
import kr.nbc.momo.domain.repository.ChatRepository

@Module
@InstallIn(SingletonComponent::class)
internal interface ChatModule {
    @Binds
    fun bindingChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository
}