package com.example.mealplanner.domain.model

import com.example.mealplanner.data.remote.dto.chat.SendMessageRequest
import java.util.concurrent.TimeUnit

data class Message(
    val serverId: String?,
    val tempId: String,
    val fromUser: String,
    val toUser: String,
    val text: String,
    val timestamp: Long,
    val status: MessageStatus = MessageStatus.SENDING,
)

fun Message.toSendMessageRequest() : SendMessageRequest {
    return SendMessageRequest(
        tempId = this.tempId,
        fromUser = this.fromUser,
        toUser = this.toUser,
        text= this.text
        )
}

enum class MessageStatus {
    SENDING,
    SENT,
    FAILED
}