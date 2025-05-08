package com.example.mealplanner.movie.presentation.home

import com.example.mealplanner.movie.domain.model.Movie

data class HomeUiState (
    val isLoading: Boolean = false,
    val moviesByType: Map<String, List<Movie>> = emptyMap(),
    val errorMessage:String = "",
)