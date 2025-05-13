package com.example.mealplanner.movie.domain.usecase

import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.repository.MovieRepository
import javax.inject.Inject

class ToggleFavoriteStatusUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(movie: Movie) {
        repository.toggleFavoriteStatus(movie)
    }
}
