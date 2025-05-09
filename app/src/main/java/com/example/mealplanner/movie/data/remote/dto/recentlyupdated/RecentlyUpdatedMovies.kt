package com.example.mealplanner.movie.data.remote.dto.recentlyupdated

data class RecentlyUpdatedMovies(
    val items: List<com.example.mealplanner.movie.data.remote.dto.recentlyupdated.RecentlyUpdatedMovieDto>,
    val pagination: com.example.mealplanner.movie.data.remote.dto.recentlyupdated.PaginationDto,
    val status: Boolean
)