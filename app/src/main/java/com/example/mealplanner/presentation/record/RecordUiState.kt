package com.example.mealplanner.presentation.record

data class RecordUiState(
    val isRecording: Boolean = false,
    val isListening: Boolean = false,
    val liveText: String = "",   // Text đang nói (Partial)
    val historyText: String = "", // Text đã hoàn tất (Final)
    val error: String? = null
    ) {
}