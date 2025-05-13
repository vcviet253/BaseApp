package com.example.mealplanner.movie.domain.usecase

import com.example.mealplanner.movie.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckFavoriteStatusUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(movieId: String): Flow<Boolean> {
        return repository.isFavorite(movieId)
    }
}