package com.example.mealplanner.movie.domain

import com.example.mealplanner.movie.domain.model.MovieDetail

interface IMovieRepository {
    suspend fun getRecentlyUpdatedMovies() : List<MovieDetail>
}