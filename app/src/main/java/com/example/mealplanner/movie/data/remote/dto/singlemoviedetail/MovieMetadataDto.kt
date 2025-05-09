package com.example.mealplanner.movie.data.remote.dto.singlemoviedetail

import com.example.mealplanner.movie.data.remote.dto.common.CategoryDto
import com.example.mealplanner.movie.data.remote.dto.common.CountryDto
import com.example.mealplanner.movie.data.remote.dto.common.ImdbDto
import com.example.mealplanner.movie.data.remote.dto.common.ModifiedDto
import com.example.mealplanner.movie.data.remote.dto.common.TmdbDto

data class MovieMetadataDto(
    val _id: String,
    val actor: List<String>,
    val category: List<com.example.mealplanner.movie.data.remote.dto.common.CategoryDto>,
    val chieurap: Boolean,
    val content: String,
    val country: List<com.example.mealplanner.movie.data.remote.dto.common.CountryDto>,
    val created: com.example.mealplanner.movie.data.remote.dto.singlemoviedetail.CreatedDto,
    val director: List<String>,
    val episode_current: String,
    val episode_total: String,
    val imdb: com.example.mealplanner.movie.data.remote.dto.common.ImdbDto,
    val is_copyright: Boolean,
    val lang: String,
    val modified: com.example.mealplanner.movie.data.remote.dto.common.ModifiedDto,
    val name: String,
    val notify: String,
    val origin_name: String,
    val poster_url: String,
    val quality: String,
    val showtimes: String,
    val slug: String,
    val status: String,
    val sub_docquyen: Boolean,
    val thumb_url: String,
    val time: String,
    val tmdb: com.example.mealplanner.movie.data.remote.dto.common.TmdbDto,
    val trailer_url: String,
    val type: String,
    val view: Int,
    val year: Int
)