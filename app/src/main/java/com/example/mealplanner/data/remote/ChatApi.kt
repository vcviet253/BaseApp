package com.example.mealplanner.data.remote

import com.example.mealplanner.data.remote.dto.chat.SendMessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {
    @POST("/send_message")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<Unit>
}