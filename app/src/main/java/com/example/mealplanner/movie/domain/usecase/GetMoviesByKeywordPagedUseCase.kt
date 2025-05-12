package com.example.mealplanner.movie.domain.usecase

import androidx.paging.PagingData
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoviesByKeywordPagedUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(
        keyword: String,
        sortField: String? = null,
        sortType:String? = null,
        sortLang:String? =null,
        category: String? = null,
        country: String? =null,
        year: String? = null
    ): Flow<PagingData<Movie>> {
        return repository.getMoviesByKeywordPaged(
            keyword,
            sortField,
            sortType,
            sortLang,
            category,
            country,
            year
        )
    }
}