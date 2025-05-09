package com.example.mealplanner.movie.data.remote.dto.moviesbycategory

data class BreadCrumb(
    val isCurrent: Boolean,
    val name: String,
    val position: Int,
    val slug: String
)