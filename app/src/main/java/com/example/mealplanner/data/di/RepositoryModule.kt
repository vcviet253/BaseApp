package com.example.mealplanner.data.di

import com.example.mealplanner.data.repository.ChatRepositoryImpl
import com.example.mealplanner.data.repository.LoginRepositoryImpl
import com.example.mealplanner.data.repository.TokenRepositoryImpl
import com.example.mealplanner.domain.repository.ChatRepository
import com.example.mealplanner.domain.repository.LoginRepository
import com.example.mealplanner.domain.repository.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
 
    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        tokenRepositoryImpl: TokenRepositoryImpl
    ): TokenRepository
}