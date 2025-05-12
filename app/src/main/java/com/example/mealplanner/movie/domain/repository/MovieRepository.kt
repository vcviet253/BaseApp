package com.example.mealplanner.movie.domain.repository

import androidx.paging.PagingData
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Movie
import kotlinx.coroutines.flow.Flow

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
    fun getMoviesByCategoryPaged(
        type: String,
        sortField: String? = null,
        sortType: String? = null,
        sortLang: String? = null,
        country: String? = null,
        year: String? = null,
    ): Flow<PagingData<Movie>>
    fun getMoviesByKeywordPaged(
        keyword: String,
        sortField: String? = null,
        sortType: String? = null,
        sortLang: String? = null,
        category: String? = null,
        country: String? = null,
        year: String? = null,
    ): Flow<PagingData<Movie>>
}