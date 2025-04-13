package com.example.mealplanner.data.remote

import com.example.mealplanner.data.remote.dto.gemini.GeminiRequestBody
import com.example.mealplanner.data.remote.dto.gemini.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GeminiApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String = "gemini-2.0-flash",
        @Query("key") apiKey: String,
        @Body body: GeminiRequestBody
    ): GeminiResponse
}