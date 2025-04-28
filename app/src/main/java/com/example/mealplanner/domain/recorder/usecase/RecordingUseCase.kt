package com.example.mealplanner.domain.recorder.usecase

interface RecordingUseCase {
    suspend fun startRecording()
    suspend fun stopRecording()
}