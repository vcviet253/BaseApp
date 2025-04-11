package com.example.mealplanner.data.remote

import com.example.mealplanner.domain.model.Message
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject

class WebSocketChatClient @Inject constructor(
    private val client: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private var isConnected = false
    private lateinit var userId: String

    private val _messageFlow = MutableSharedFlow<Message>(
        replay = 0,
        extraBufferCapacity = 64
    )
    val messageFlow = _messageFlow.asSharedFlow()

    fun connect(userId: String, serverUrl: String) {
        // Nếu đã kết nối với đúng userId → không cần reconnect
        if (isConnected && userId == this.userId) {
            println("✅ Already connected as $userId. No need to reconnect.")
            return
        }

        // Nếu đang kết nối với user khác → ngắt và kết nối lại
        if (isConnected) {
            println("🔄 Reconnecting: Switching from $this.userId to $userId")
            disconnect()
        }


        this.userId = userId
        val request = Request.Builder().url(serverUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                super.onOpen(ws, response)
                isConnected = true
                println("✅ WebSocket connected to $serverUrl as $userId")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("📩 Received: $text")
                try {
                    val json = JSONObject(text)
                    val msg = Message(
                        from = json.getString("from"),
                        to = json.getString("to"),
                        text = json.getString("text"),
                        timestamp = json.getLong("timestamp")
                    )
                    _messageFlow.tryEmit(msg)
                } catch (e: Exception) {
                    println("❌ Parse error: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                println("❌ WebSocket failure: ${t.message}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                println("🔌 WebSocket closed: $code $reason")
            }
        })
    }

    fun sendMessage(to: String, text: String) {
        val json = JSONObject().apply {
            put("to", to)
            put("text", text)
        }
        println("📤 Sending: $json")
        webSocket?.send(json.toString()) ?: println("❌ WebSocket is null")
    }

    fun disconnect() {
        webSocket?.close(1000, "Manual disconnect")
        webSocket = null
        isConnected = false
    }
}