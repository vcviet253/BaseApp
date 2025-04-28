package com.example.mealplanner.data.repository

import android.util.Log
import com.example.mealplanner.data.remote.GeminiApi
import com.example.mealplanner.data.remote.dto.gemini.GeminiContent
import com.example.mealplanner.data.remote.dto.gemini.buildGeminiRequest
import com.example.mealplanner.domain.repository.GeminiRepository
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GeminiRepositoryImpl @Inject constructor(
    private val api: GeminiApi,
    @Named("gemini_api_key") private val apiKey: String
) : GeminiRepository {
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun generateDiaryFromContent(contents: List<GeminiContent>): String {
        val request = buildGeminiRequest(contents)

        val json = Json {
            prettyPrint = true
            encodeDefaults = true
            explicitNulls = false
        }

        // Log ra JSON request để debug
        Log.d("GeminiRequest", json.encodeToString(request))

        val response = api.generateContent(apiKey = apiKey, body = request)

        return response.candidates.firstOrNull()
            ?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("Empty response")
    }

    override suspend fun getModelAnswerForQuestionAndBand(prompt: String): String {

        // Build the Gemini request with the text prompt
        val requestBody = buildGeminiRequest(prompt)

        // Call Gemini API and get the response (replace with actual network request)
        val response = api.generateContent(apiKey = apiKey, body = requestBody)

        // Assuming the API response contains the answer text (update with your actual response model)
        return response.candidates.firstOrNull()
            ?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("Empty response") // This will depend on how the Gemini API returns the answer
    }

}