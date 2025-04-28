package com.example.mealplanner.presentation.speaking_helper

data class SampleAnswersUiState(
    val topics: List<String> =  emptyList(),
    val selectedTopic: String = "",
    val selectedBand: Float = 7f,
    val questionText: String = "",
    val modelAnswer:String = "",
    val isLoading: Boolean = false,
    val isTopicMenuExpanded: Boolean = false,
    val isBandMenuExpanded: Boolean = false,
) {
}