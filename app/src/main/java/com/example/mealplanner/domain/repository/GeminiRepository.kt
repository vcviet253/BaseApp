package com.example.mealplanner.domain.repository

import com.example.mealplanner.data.remote.dto.gemini.GeminiContent

interface GeminiRepository {
    suspend fun generateDiaryFromContent(contents: List<GeminiContent>): String
}