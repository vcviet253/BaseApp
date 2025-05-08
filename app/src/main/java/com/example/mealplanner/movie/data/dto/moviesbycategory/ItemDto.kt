package com.example.mealplanner.movie.data.dto.moviesbycategory

import com.example.mealplanner.movie.data.dto.common.CategoryDto
import com.example.mealplanner.movie.data.dto.common.CountryDto
import com.example.mealplanner.movie.data.dto.common.ModifiedDto

data class ItemDto(
    val _id: String,
    val category: List<CategoryDto>,
    val chieurap: Boolean,
    val country: List<CountryDto>,
    val episode_current: String,
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
    val type: String,
    val year: Int
)