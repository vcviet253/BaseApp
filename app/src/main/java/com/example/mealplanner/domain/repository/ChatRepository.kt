package com.example.mealplanner.domain.repository

import com.example.mealplanner.data.remote.dto.chat.SendMessageRequest
import com.example.mealplanner.data.remote.dto.chat.WebSocketMessage
import com.example.mealplanner.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(message: Message)
    fun observeMessages(): Flow<Message>
    fun connectWebSocket(userId: String)
    fun disconnectWebSocket()
}