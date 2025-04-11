package com.example.mealplanner.presentation.chat

import com.example.mealplanner.domain.model.Message

data class ChatUiState(val messages: List<Message> = emptyList()) {
}