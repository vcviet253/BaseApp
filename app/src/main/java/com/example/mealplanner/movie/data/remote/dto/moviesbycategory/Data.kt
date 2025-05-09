package com.example.mealplanner.movie.data.remote.dto.moviesbycategory

data class Data(
    val APP_DOMAIN_CDN_IMAGE: String,
    val APP_DOMAIN_FRONTEND: String,
    val breadCrumb: List<com.example.mealplanner.movie.data.remote.dto.moviesbycategory.BreadCrumb>,
    val items: List<com.example.mealplanner.movie.data.remote.dto.moviesbycategory.ItemDto>,
    val params: com.example.mealplanner.movie.data.remote.dto.moviesbycategory.Params,
    val seoOnPage: com.example.mealplanner.movie.data.remote.dto.moviesbycategory.SeoOnPage,
    val titlePage: String,
    val type_list: String
)