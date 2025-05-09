package com.example.mealplanner.movie.domain.usecase

import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.repository.MovieRepository
import javax.inject.Inject

class GetMoviesByCategoryUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(
        type: String,
        page: Int? = null,
        sort_field: String? = null,
        sort_type: String? = null,
        sort_lang: String? = null,
        country: String? = null,
        year: String? = null,
        limit: Int? = null
    ) : Resource<List<Movie>> {
        return movieRepository.getMoviesByCategory(type,page,sort_field, sort_type, sort_lang, country, year, limit)
    }
}