package com.example.mealplanner.movie.data.dto.recentlyupdated

data class RecentlyUpdatedMovies(
    val items: List<RecentlyUpdatedMovieDto>,
    val pagination: PaginationDto,
    val status: Boolean
)