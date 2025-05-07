package com.example.mealplanner.movie.data.dto.recentlyupdated

data class Pagination(
    val currentPage: Int,
    val totalItems: Int,
    val totalItemsPerPage: Int,
    val totalPages: Int,
    val updateToday: Int
)