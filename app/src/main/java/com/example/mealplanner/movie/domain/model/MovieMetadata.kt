package com.example.mealplanner.movie.domain.model

import com.example.mealplanner.movie.data.remote.dto.common.CategoryDto
import com.example.mealplanner.movie.data.remote.dto.common.CountryDto
import com.example.mealplanner.movie.data.remote.dto.common.ImdbDto
import com.example.mealplanner.movie.data.remote.dto.common.ModifiedDto
import com.example.mealplanner.movie.data.remote.dto.common.TmdbDto
import com.example.mealplanner.movie.data.remote.dto.singlemoviedetail.CreatedDto

data class MovieMetadata(
    val id: String,
    val actor: List<String>? = null,
    val category: List<Category>,
    val chieurap: Boolean? = null,
    val content: String? = null,
    val country: List<Country>,
    val created: String? = null,
    val director: List<String>? = null,
    val episode_current: String,
    val episode_total: String? = null,
    val imdb: String? = null,
    val is_copyright: Boolean? = null,
    val lang: String,
    val modified: String,
    val name: String,
    val notify: String? = null,
    val origin_name: String,
    val poster_url: String,
    val quality: String,
    val showtimes: String? =null ,
    val slug: String,
    val status: String? =null,
    val sub_docquyen: Boolean,
    val thumb_url: String,
    val time: String,
    val tmdb: Tmdb? = null,
    val trailer_url: String? =null,
    val type: String,
    val view: Int? = null,
    val year: Int
)
