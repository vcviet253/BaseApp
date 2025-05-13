package com.example.mealplanner.movie.domain.usecase

import androidx.paging.PagingData
import com.example.mealplanner.movie.data.repository.MovieRepositoryImpl
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoviesByCategoryPagedUseCase @Inject constructor(
    private val repository: MovieRepository
){
    operator fun invoke(
        type: String,
        sortField: String? = null,
        sortType:String? = null,
        sortLang:String? =null,
        country: String? =null,
        year: String? = null
    ): Flow<PagingData<Movie>> {
        return repository.getMoviesByCategoryPaged(
            type,
            sortField,
            sortType,
            sortLang,
            country,
            year
        )
    }
}