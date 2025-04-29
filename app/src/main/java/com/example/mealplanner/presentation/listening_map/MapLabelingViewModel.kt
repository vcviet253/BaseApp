package com.example.mealplanner.presentation.listening_map

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MapLabelingViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(MapLabelingUiState())
    val uiState = _uiState.asStateFlow()
}