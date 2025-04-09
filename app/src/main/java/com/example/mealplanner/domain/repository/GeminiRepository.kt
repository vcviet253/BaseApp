package com.example.mealplanner.domain.repository

import com.example.mealplanner.data.remote.dto.GeminiContent
import com.example.mealplanner.domain.model.DiaryEntry

interface GeminiRepository {
    suspend fun generateDiaryFromContent(contents: List<GeminiContent>): String
}