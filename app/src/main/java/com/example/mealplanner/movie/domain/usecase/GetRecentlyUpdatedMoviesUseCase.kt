package com.example.mealplanner.movie.domain.usecase

import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.repository.MovieRepository
import javax.inject.Inject

class GetRecentlyUpdatedMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(): Resource<List<Movie>> {
        return movieRepository.getRecentlyUpdatedMovies()
    }
}