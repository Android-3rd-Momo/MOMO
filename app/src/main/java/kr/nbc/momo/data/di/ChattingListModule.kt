package kr.nbc.momo.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.nbc.momo.data.repository.ChatListRepositoryImpl
import kr.nbc.momo.domain.repository.ChatListRepository

@Module
@InstallIn(SingletonComponent::class)
internal interface ChattingListModule {
    @Binds
    fun bindChattingListRepository(chatListRepositoryImpl: ChatListRepositoryImpl): ChatListRepository
}