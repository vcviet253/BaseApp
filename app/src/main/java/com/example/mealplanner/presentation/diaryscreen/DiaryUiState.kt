package com.example.mealplanner.presentation.diaryscreen

data class DiaryUiState(
    val images: List<String> = emptyList(),
    val prompt: String = "",
    val diaryText: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)