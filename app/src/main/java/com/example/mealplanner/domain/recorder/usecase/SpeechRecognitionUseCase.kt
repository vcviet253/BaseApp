package com.example.mealplanner.domain.recorder.usecase

import com.example.mealplanner.data.recorder.SpeechRecognitionResult
import com.example.mealplanner.data.recorder.SpeechRecognizerManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechRecognitionUseCase @Inject constructor(
    private val speechRecognizerManager: SpeechRecognizerManager
) {
    fun listen(): Flow<SpeechRecognitionResult> {
        return speechRecognizerManager.listen()
    }
}