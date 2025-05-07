package com.example.mealplanner.movie.data.dto.singlemoviedetail

data class SingleMovieDetailDto(
    val episodes: List<EpisodeDto>,
    val movieMetadata: MovieMetadataDto,
    val msg: String,
    val status: Boolean
)