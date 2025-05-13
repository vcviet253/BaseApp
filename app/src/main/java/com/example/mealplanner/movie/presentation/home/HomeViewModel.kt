package com.example.mealplanner.movie.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.data.model.MovieCategory
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.usecase.GetMoviesByCategoryUseCase
import com.example.mealplanner.movie.domain.usecase.GetRecentlyUpdatedMoviesUseCase
import com.example.mealplanner.movie.presentation.home.state.MovieCategoryState
import com.example.mealplanner.movie.presentation.home.state.RecentlyUpdatedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentlyUpdatedMoviesUseCase: GetRecentlyUpdatedMoviesUseCase,
    private val getMoviesByCategoryUseCase: GetMoviesByCategoryUseCase
) : ViewModel() {

    // Map cho các category thông thường
    private val _movieStates = MutableStateFlow<Map<String, MovieCategoryState>>(emptyMap())
    val movieStates: StateFlow<Map<String, MovieCategoryState>> = _movieStates.asStateFlow()

    // State riêng cho Recently Updated
    private val _recentlyUpdatedState = MutableStateFlow(RecentlyUpdatedState())
    val recentlyUpdatedState: StateFlow<RecentlyUpdatedState> = _recentlyUpdatedState.asStateFlow()

    val movieCategories = listOf(
        MovieCategory.HOC_DUONG,
        MovieCategory.GIA_DINH,
        MovieCategory.TINH_CAM
    )

    init {
        Log.d(TAG, "Start fetching recently updated movies")
        fetchRecentlyUpdatedMovies()
        Log.d(TAG, "Finished fetching recently updated movies")
        Log.d(TAG, "Start fetching movies by category")
        MovieCategory.entries.forEach { category ->
            Log.d(TAG, "Start fetching $category movies")
            startFetchingMoviesByCategory(category.slug)
        }
    }

    fun fetchRecentlyUpdatedMovies() {
        viewModelScope.launch {
            _recentlyUpdatedState.value = RecentlyUpdatedState(isLoading = true)

            val result = getRecentlyUpdatedMoviesUseCase(
            )

            _recentlyUpdatedState.value = when (result) {
                is Resource.Success -> RecentlyUpdatedState(movies = result.data)
                is Resource.Error -> RecentlyUpdatedState(error = result.message)
                else -> RecentlyUpdatedState(isLoading = true)
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
        viewModelScope.launch {
            _movieStates.update { current ->
                current + (type to MovieCategoryState(isLoading = true))
            }

            val result = fetchMoviesByCategory(type = type)

            _movieStates.update { current ->
                current + (type to when (result) {
                    is Resource.Success -> MovieCategoryState(
                        isLoading = false,
                        movies = result.data
                    )
                    is Resource.Error -> MovieCategoryState(
                        isLoading = false,
                        error = result.message
                    )
                    is Resource.Loading -> MovieCategoryState(isLoading = true)
                })
            }
        }
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