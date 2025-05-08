package com.example.mealplanner.movie.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.usecase.GetMoviesByCategoryUseCase
import com.example.mealplanner.movie.domain.usecase.GetRecentlyUpdatedMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentlyUpdatedMoviesUseCase: GetRecentlyUpdatedMoviesUseCase,
    private val getMoviesByCategoryUseCase: GetMoviesByCategoryUseCase
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    fun fetchRecentlyUpdatedMovies() {
        _homeUiState.update { state -> state.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val result = getRecentlyUpdatedMoviesUseCase()
            when (result) {
                is Resource.Success -> {
                    val movies = result.data
                    _homeUiState.update { state ->
                        state.copy(
                            isLoading = false,
                            moviesByType = state.moviesByType + ("recently-updated" to movies)
                        )
                    }
                }

                is Resource.Error -> _homeUiState.update { it.copy(isLoading = false) }

                is Resource.Loading -> {
                    // optional, probably not needed here since you already set isLoading = true
                }
            }
        }
    }

    fun startFetchingMoviesByCategory(
        type: String = "hanh-dong",
        page: Int? = null,
        sort_field: String? = null,
        sort_type: String? = null,
        sort_lang: String? = null,
        country: String? = null,
        year: String? = null,
        limit: Int? = null
    ) {
        _homeUiState.update { state -> state.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val result = fetchMoviesByCategory(type)
            when (result) {
                is Resource.Success -> {
                    val movies = result.data
                    _homeUiState.update { state ->
                        state.copy(
                            isLoading = false,
                            moviesByType = state.moviesByType + (type to movies)
                        )
                    }
                }

                is Resource.Error -> _homeUiState.update { it.copy(isLoading = false) }

                is Resource.Loading -> {
                    // optional, probably not needed here since you already set isLoading = true
                }
            }
        }
    }

    init {
        fetchRecentlyUpdatedMovies()
        startFetchingMoviesByCategory()
    }

    suspend fun fetchMoviesByCategory(
        type: String,
        page: Int? = null,
        sort_field: String? = null,
        sort_type: String? = null,
        sort_lang: String? = null,
        country: String? = null,
        year: String? = null,
        limit: Int? = null
    ): Resource<List<Movie>> {
        return getMoviesByCategoryUseCase(
            type,
            page,
            sort_field,
            sort_type,
            sort_lang,
            country,
            year,
            limit
        )
    }
}