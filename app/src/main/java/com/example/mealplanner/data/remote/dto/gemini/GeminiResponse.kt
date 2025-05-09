package com.example.mealplanner.data.remote.dto.gemini

import kotlinx.serialization.Serializable


@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

@Serializable
data class GeminiCandidate(
    val content: GeminiGeneratedContent
)

@Serializable
data class GeminiGeneratedContent(
    val parts: List<GeminiGeneratedPart>
)

@Serializable
data class GeminiGeneratedPart(
    val text: String
)

fun GeminiResponse.extractText(): String {
    return this.candidates.firstOrNull()
        ?.content?.parts?.joinToString(separator = "\n") { it.text }
        ?: "No response from Gemini"
}