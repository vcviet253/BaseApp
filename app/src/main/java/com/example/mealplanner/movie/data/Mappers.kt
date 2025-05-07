package com.example.mealplanner.movie.data

import com.example.mealplanner.movie.domain.IMovieRepository
import com.example.mealplanner.movie.domain.model.MovieDetail
import javax.inject.Inject

class MovieRepository @Inject constructor(): IMovieRepository {
    override suspend fun getRecentlyUpdatedMovies(): List<MovieDetail> {
        TODO("Not yet implemented")
    }
}