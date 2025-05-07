package com.example.mealplanner.movie.data.dto.recentlyupdated

data class RecentlyUpdatedMovies(
    val items: List<MovieDto>,
    val pagination: Pagination,
    val status: Boolean
)