package com.example.mealplanner.core.di

import com.example.mealplanner.domain.recorder.usecase.RecordingUseCase
import com.example.mealplanner.domain.recorder.usecase.RecordingUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    @Singleton
    abstract fun bindStopRecordingUseCase(
        recordingUseCaseImpl: RecordingUseCaseImpl
    ): RecordingUseCase
}