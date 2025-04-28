package com.example.mealplanner.domain.usecase

import com.example.mealplanner.domain.repository.GeminiRepository
import javax.inject.Inject

class GetModelAnswerForQuestionAndBandUseCase @Inject constructor(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(prompt: String): String {
        return repository.getModelAnswerForQuestionAndBand(prompt)
    }
}