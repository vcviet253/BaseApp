package com.example.mealplanner.data.remote.dto.chat

import com.google.gson.annotations.SerializedName

data class SendMessageRequest(
    val tempId: String, //Id for syncing between client and server (when echo back)
    @SerializedName("from_user") val fromUser: String,
    @SerializedName("to_user") val toUser: String,
    val text: String
) {
}