package com.example.mealplanner.movie.domain.repository

import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Movie

interface MovieRepository {
    suspend fun getRecentlyUpdatedMovies() : Resource<List<Movie>>
    suspend fun getMovie(slug: String): Resource<Movie>
}