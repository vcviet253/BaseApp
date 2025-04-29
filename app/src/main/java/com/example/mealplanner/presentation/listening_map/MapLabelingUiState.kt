package com.example.mealplanner.presentation.listening_map

enum class AudioPlayerState { IDLE, PLAYING, PAUSED, BUFFERING, COMPLETED, ERROR }

data class MapLabelingUiState(
    val isLoading: Boolean = false, // Đang tải dữ liệu ban đầu?
) {
}