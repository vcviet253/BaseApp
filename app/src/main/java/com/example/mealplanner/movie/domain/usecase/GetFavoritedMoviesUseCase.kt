package com.example.mealplanner.movie.domain.usecase

import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritedMoviesUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(): Flow<List<Movie>> { // Hoặc Flow<PagingData<Movie>>
        return repository.getFavoritedMovies()
    }
}