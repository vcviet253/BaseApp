package com.example.mealplanner.movie.domain.repository

import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Movie

interface MovieRepository {
    suspend fun getRecentlyUpdatedMovies(): Resource<List<Movie>>
    suspend fun getMovie(slug: String): Resource<Movie>
    suspend fun getMoviesByCategory(
        type: String,
        page: Int? = null,
        sort_field: String? = null,
        sort_type: String? = null,
        sort_lang: String? = null,
        country: String? = null,
        year: String? = null,
        limit: Int? = null
    ): Resource<List<Movie>>
}