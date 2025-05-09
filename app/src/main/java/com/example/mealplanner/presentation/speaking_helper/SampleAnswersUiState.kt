package com.example.mealplanner.presentation.speaking_helper

import android.view.Menu

data class SampleAnswersUiState(
    val topics: List<String> =  emptyList(),
    val selectedTopic: String? = null,
    val selectedBand: Float? = null,
    val selectedPart: String? = null,
    val questionText: String = "",
    val modelAnswer:String = "",
    val isLoading: Boolean = false,
    val isTopicMenuExpanded: Boolean = false,
    val isBandMenuExpanded: Boolean = false,
    val isPartMenuExpanded: Boolean =  false,
) {
}