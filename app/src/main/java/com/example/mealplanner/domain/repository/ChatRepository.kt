package com.example.mealplanner.domain.repository

import com.example.mealplanner.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun connect(userId: String) : Flow<Message>
    fun sendMessage(to: String, text: String)
    fun disconnect()
}