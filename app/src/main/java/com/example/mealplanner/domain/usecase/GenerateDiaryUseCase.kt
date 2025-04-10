package com.example.mealplanner.domain.usecase

import com.example.mealplanner.common.Constants
import com.example.mealplanner.data.remote.dto.GeminiContent
import com.example.mealplanner.domain.repository.GeminiRepository
import javax.inject.Inject

class GenerateDiaryUseCase @Inject constructor(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(images: List<String>, prompt: String): String {
        val contents = images.map { GeminiContent.Image(it) } +
                GeminiContent.Text(Constants.PROMPT)

        return repository.generateDiaryFromContent(contents)
    }
}