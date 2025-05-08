package com.example.mealplanner.movie.data.dto.moviesbycategory

data class BreadCrumb(
    val isCurrent: Boolean,
    val name: String,
    val position: Int,
    val slug: String
)