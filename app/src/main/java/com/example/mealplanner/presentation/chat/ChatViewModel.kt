package com.example.mealplanner.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.common.UserSession
import com.example.mealplanner.domain.model.Message
import com.example.mealplanner.domain.model.MessageStatus
import com.example.mealplanner.domain.repository.ChatRepository
import com.example.mealplanner.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

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
    private var observeJob: Job? = null

    init {
        val userId = UserSession.userId
        userId?.let {
            _uiState.update { it.copy(currentUserId = userId) }
            connect(userId)
        }
    }

    fun connect(userId: String) {
        repository.connectWebSocket(userId)

        observeJob?.cancel()

//        repository.observeMessages().onEach { msg ->
//            val otherUser = if (msg.fromUser == currentUserId) msg.toUser else msg.fromUser
//            _messages.update { map ->
//                val currentList = map[otherUser].orEmpty()
//                map + (otherUser to (currentList + msg))
//            }
//        }.launchIn(viewModelScope)

        observeJob = repository.observeMessages().onEach { serverMsg ->
            _uiState.update { state ->
                val updatedMessages = state.messages.map { localMsg ->
                    // Nếu là tin nhắn do chính mình gửi, có tempId, đang ở trạng thái SENDING và nội dung trùng
                    if (
                        localMsg.tempId != null &&
                        localMsg.text == serverMsg.text &&
                        localMsg.toUser == serverMsg.toUser &&
                        localMsg.fromUser == serverMsg.fromUser &&
                        localMsg.status == MessageStatus.SENDING
                    ) {
                        // Update lại từ message của server, giữ lại tempId để đồng bộ
                        serverMsg.copy(tempId = localMsg.tempId, status = MessageStatus.SENT)
                    } else {
                        localMsg
                    }
                }

                // Nếu không khớp tin nhắn nào, tức là tin nhắn mới → thêm vào
                val isDuplicate = updatedMessages.any { it.serverId == serverMsg.serverId }

                state.copy(
                    messages = if (isDuplicate) updatedMessages else updatedMessages + serverMsg
                )
            }
        }.launchIn(viewModelScope)
    }

    fun sendMessage(to: String, text: String, from: String) {
        val tempId = UUID.randomUUID().toString()
        val tempMessage = Message(
            serverId = null,
            tempId = tempId,
            fromUser = from,
            toUser = to,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        _uiState.update { state -> state.copy(messages = state.messages + tempMessage) }
        toggleExpandedMessageId(tempMessage.tempId)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                sendMessageUseCase(tempMessage)
            } catch (e: Exception) {
                Log.d(TAG, "Error when sending message: ${e.localizedMessage}")

                // Cập nhật message sang FAILED
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages.map {
                            if (it.tempId == tempId) it.copy(status = MessageStatus.FAILED) else it
                        }
                    )
                }
            }
        }
    }

    fun toggleExpandedMessageId(messageId: String) {
        _uiState.update { state -> state.copy(expandedMessageId =  if (state.expandedMessageId == messageId) null else messageId) }
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
        observeJob?.cancel()
        observeJob = null
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}