package com.example.mealplanner.presentation.chat

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.domain.model.Message
import com.example.mealplanner.domain.repository.ChatRepository
import com.example.mealplanner.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttp
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "ChatViewModel"

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val repository: ChatRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages = _messages.asStateFlow()

    private var connectionJob: Job? = null
    private var currentUserId: String? = null

    fun setUserId(newUserId: String) {
        if (newUserId == currentUserId) return // Không làm gì nếu cùng user

        disconnect() // Dừng kết nối cũ nếu có
        currentUserId = newUserId
        connect(newUserId)
    }

    fun connect(userId: String) {
        repository.connectWebSocket(userId)

        repository.observeMessages().onEach { msg ->
            val otherUser = if (msg.fromUser == currentUserId) msg.toUser else msg.fromUser
            _messages.update { map ->
                val currentList = map[otherUser].orEmpty()
                map + (otherUser to (currentList + msg))
            }
        }.launchIn(viewModelScope)
    }

    fun sendMessage(to: String, text: String, from: String) {
        val tempId = UUID.randomUUID().toString()
        val messageRequest = Message(
            serverId = null,
            tempId = tempId,
            fromUser =  from,
            toUser = to,
            text = text,
            timestamp = System.currentTimeMillis())

        viewModelScope.launch(Dispatchers.IO) {
            try {
                sendMessageUseCase(messageRequest)
            } catch(e: Exception) {
                Log.d(TAG, "Error when sending message: ${e.localizedMessage}")
            }
        }
    }

//    private fun connect(userId: String) {
//        Log.d("ChatViewModel", "🔌 Connecting as $userId")
//
//        connectionJob = repository.connect(userId)
//            .onStart {
//                Log.d("ChatViewModel", "✅ Connected to WebSocket as $userId")
//            }
//            .onEach { message ->
//                Log.d("ChatViewModel", "📩 Received: $message")
//                _uiState.update { current ->
//                    current.copy(messages = current.messages + message)
//                }
//            }
//            .catch { e ->
//                Log.e("ChatViewModel", "❌ Connection error: ${e.message}", e)
//            }
//            .launchIn(viewModelScope)
//    }
//
//    fun sendMessage(to: String, text: String) {
//        Log.e("ChatViewModel", "Sending message")
//        repository.sendMessage(to, text)
//    }

    private fun disconnect() {
        Log.d("ChatViewModel", "🔌 Disconnecting WebSocket")
        repository.disconnectWebSocket()
        connectionJob?.cancel()
        connectionJob = null
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}