package com.example.mealplanner.movie.data.remote.dto.moviesbycategory

data class Params(
    val filterCategory: List<String>,
    val filterCountry: List<String>,
    val filterType: List<String>,
    val filterYear: List<String>,
    val pagination: com.example.mealplanner.movie.data.remote.dto.moviesbycategory.Pagination,
    val slug: String,
    val sortField: String,
    val sortType: String,
    val type_slug: String
)