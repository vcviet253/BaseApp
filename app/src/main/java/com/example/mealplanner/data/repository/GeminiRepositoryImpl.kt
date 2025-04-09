package com.example.mealplanner.data.repository

import android.util.Log
import com.example.mealplanner.data.remote.GeminiApi
import com.example.mealplanner.data.remote.dto.GeminiContent
import com.example.mealplanner.data.remote.dto.GeminiRequestBody
import com.example.mealplanner.data.remote.dto.buildGeminiRequest
import com.example.mealplanner.domain.model.DiaryEntry
import com.example.mealplanner.domain.repository.GeminiRepository
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Named

class GeminiRepositoryImpl @Inject constructor(private val api: GeminiApi,
                                               @Named("gemini_api_key") private val apiKey: String) : GeminiRepository {
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

}