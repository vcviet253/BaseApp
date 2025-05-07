package com.example.mealplanner.movie.domain.model

data class MovieDetail(
    val metadata: MovieMetadata,
    val episodes: List<Episode>
) {
}