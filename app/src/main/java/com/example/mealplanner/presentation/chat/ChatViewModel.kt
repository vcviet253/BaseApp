package com.example.mealplanner.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.domain.model.Message
import com.example.mealplanner.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    //@Named("UserId") private val userId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    private var connectionJob: Job? = null
    private var currentUserId: String? = null

    fun setUserId(newUserId: String) {
        if (newUserId == currentUserId) return // Không làm gì nếu cùng user

        disconnect() // Dừng kết nối cũ nếu có
        currentUserId = newUserId
        connect(newUserId)
    }

    private fun connect(userId: String) {
        Log.d("ChatViewModel", "🔌 Connecting as $userId")

        connectionJob = repository.connect(userId)
            .onStart {
                Log.d("ChatViewModel", "✅ Connected to WebSocket as $userId")
            }
            .onEach { message ->
                Log.d("ChatViewModel", "📩 Received: $message")
                _uiState.update { current ->
                    current.copy(messages = current.messages + message)
                }
            }
            .catch { e ->
                Log.e("ChatViewModel", "❌ Connection error: ${e.message}", e)
            }
            .launchIn(viewModelScope)
    }

    fun sendMessage(to: String, text: String) {
        Log.e("ChatViewModel", "Sending message")
        repository.sendMessage(to, text)
    }

    private fun disconnect() {
        Log.d("ChatViewModel", "🔌 Disconnecting WebSocket")
        repository.disconnect()
        connectionJob?.cancel()
        connectionJob = null
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}