package com.example.mealplanner.movie.presentation.moviesbycategory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.usecase.GetMoviesByCategoryPagedUseCase
import com.example.mealplanner.movie.presentation.navigation.MovieAppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MoviesByCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMoviesByCategoryPagedUseCase: GetMoviesByCategoryPagedUseCase
): ViewModel() {
    val categorySlug: String =
        checkNotNull(savedStateHandle[MovieAppDestinations.MOVIES_BY_CATEGORY_ARG_URL])

    // Khai báo Flow<PagingData<Movie>> để cung cấp dữ liệu cho UI
    val movies: Flow<PagingData<Movie>> =
        // Gọi Use Case, truyền categorySlug vừa lấy được
        getMoviesByCategoryPagedUseCase(type = categorySlug)
            // Sử dụng cachedIn để cache PagingData trong ViewModel scope.
            // Điều này giúp giữ lại dữ liệu khi cấu hình thay đổi (ví dụ: xoay màn hình).
            .cachedIn(viewModelScope)
}