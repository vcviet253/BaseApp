package com.example.mealplanner.movie.presentation.home.state

import com.example.mealplanner.movie.domain.model.Movie

data class MovieCategoryState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String = ""
)