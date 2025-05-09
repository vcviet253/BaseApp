package com.example.mealplanner.data.repository

import android.util.Log
import com.example.mealplanner.data.remote.ChatApi
import com.example.mealplanner.data.remote.WebSocketChatClient
import com.example.mealplanner.data.remote.dto.chat.SendMessageRequest
import com.example.mealplanner.data.remote.dto.chat.WebSocketMessage
import com.example.mealplanner.data.remote.dto.chat.toMessage
import com.example.mealplanner.domain.model.Message
import com.example.mealplanner.domain.model.toSendMessageRequest
import com.example.mealplanner.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val api: ChatApi,
    private val client: WebSocketChatClient,
) : ChatRepository {

    private var currentUserId: String? = null

    override suspend fun sendMessage(message: Message) {
        api.sendMessage(message.toSendMessageRequest())
    }

    override fun observeMessages(): Flow<Message> {
        return client.incomingMessages.map { it.toMessage() }
    }

    override fun connectWebSocket(userId: String) {
        client.connect(userId)
    }

    override fun disconnectWebSocket() {
        client.disconnect()
    }


//    fun connect(userId: String): Flow<Message> {
//        val serverUrl = "ws://10.0.2.2:8000/ws/$userId"
//        Log.d("ChatRepository", "Connecting to $serverUrl")
//
//        // Nếu userId thay đổi thì disconnect trước
//        if (currentUserId != userId) {
//            client.disconnect() // ❗reset WebSocket và Flow
//            currentUserId = userId
//        }
//
//        client.connect(userId, serverUrl)
//        return client.messageFlow
//    }
//
//    fun sendMessage(to: String, text: String) {
//        Log.d("ChatRepository", "Sending message to $to: $text")
//        client.sendMessage(to, text)
//    }
//
//    fun disconnect() {
//        Log.d("ChatRepository", "Disconnecting WebSocket")
//        client.disconnect()
//    }
}
