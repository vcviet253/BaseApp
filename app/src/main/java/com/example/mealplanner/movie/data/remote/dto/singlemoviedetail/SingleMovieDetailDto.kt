package com.example.mealplanner.movie.data.remote.dto.singlemoviedetail

data class SingleMovieDetailDto(
    val episodes: List<com.example.mealplanner.movie.data.remote.dto.singlemoviedetail.EpisodeDto>,
    val movie: com.example.mealplanner.movie.data.remote.dto.singlemoviedetail.MovieMetadataDto,
    val msg: String,
    val status: Boolean
)