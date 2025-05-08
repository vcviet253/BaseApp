package com.example.mealplanner.movie.data.dto.moviesbycategory

data class Params(
    val filterCategory: List<String>,
    val filterCountry: List<String>,
    val filterType: List<String>,
    val filterYear: List<String>,
    val pagination: Pagination,
    val slug: String,
    val sortField: String,
    val sortType: String,
    val type_slug: String
)