package com.example.mealplanner.core.di

import com.example.mealplanner.core.audio.SimpleAudioPlayer
import com.example.mealplanner.core.audio.SimpleAudioPlayerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AudioModule {
    @Binds
    @Singleton
    abstract fun bindSimpleAudioPlayer(
        simpleAudioPlayerImpl: SimpleAudioPlayerImpl
    ): SimpleAudioPlayer
}