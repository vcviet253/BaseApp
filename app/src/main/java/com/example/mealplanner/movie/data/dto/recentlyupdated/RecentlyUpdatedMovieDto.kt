package com.example.mealplanner.movie.data.dto.recentlyupdated

import com.example.mealplanner.movie.data.dto.common.CategoryDto
import com.example.mealplanner.movie.data.dto.common.CountryDto
import com.example.mealplanner.movie.data.dto.common.ImdbDto
import com.example.mealplanner.movie.data.dto.common.ModifiedDto
import com.example.mealplanner.movie.data.dto.common.TmdbDto

data class RecentlyUpdatedMovieDto(
    val _id: String,
    val category: List<CategoryDto>,
    val country: List<CountryDto>,
    val episode_current: String,
    val imdb: ImdbDto,
    val lang: String,
    val modified: ModifiedDto,
    val name: String,
    val origin_name: String,
    val poster_url: String,
    val quality: String,
    val slug: String,
    val sub_docquyen: Boolean,
    val thumb_url: String,
    val time: String,
    val tmdb: TmdbDto,
    val type: String,
    val year: Int
)