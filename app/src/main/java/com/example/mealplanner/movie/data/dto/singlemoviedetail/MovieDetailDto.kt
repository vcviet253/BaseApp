package com.example.mealplanner.movie.data.dto.singlemoviedetail

data class MovieDetailDto(
    val episodes: List<EpisodeDto>,
    val movie: MovieMetadataDto,
    val msg: String,
    val status: Boolean
)