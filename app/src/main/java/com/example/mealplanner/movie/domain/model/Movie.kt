package com.example.mealplanner.movie.domain.model

data class Movie(
    val metadata: MovieMetadata,
    val episodes: List<Episode>? = null
) {
}