package com.example.mealplanner.movie.data.remote.dto.recentlyupdated

data class PaginationDto(
    val currentPage: Int,
    val totalItems: Int,
    val totalItemsPerPage: Int,
    val totalPages: Int,
    val updateToday: Int
)