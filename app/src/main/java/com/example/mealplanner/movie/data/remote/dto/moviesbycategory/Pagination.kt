package com.example.mealplanner.movie.data.remote.dto.moviesbycategory

data class Pagination(
    val currentPage: Int,
    val totalItems: Int,
    val totalItemsPerPage: Int,
    val totalPages: Int
)