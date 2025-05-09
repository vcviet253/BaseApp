package com.example.mealplanner.data.remote.dto.chat

import com.example.mealplanner.domain.model.Message
import com.google.gson.annotations.SerializedName

data class WebSocketMessage(
    val serverId: String,
    val tempId: String,
    @SerializedName("from_user") val fromUser: String,
    @SerializedName("to_user") val toUser: String,
    val text: String,
    val timestamp: Long
) {
}

fun WebSocketMessage.toMessage() : Message {
    return Message(
        serverId = this.serverId,
        tempId = this.tempId,
        fromUser = this.fromUser,
        toUser = this.toUser,
        text = this.text,
        timestamp = this.timestamp
    )
}

