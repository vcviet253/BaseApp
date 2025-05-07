package com.example.mealplanner.movie.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.usecase.GetRecentlyUpdatedMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentlyUpdatedMoviesUseCase: GetRecentlyUpdatedMoviesUseCase
) : ViewModel() {
    private val _recentlyUpdatedMovies = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading())
    val recentlyUpdatedMovies = _recentlyUpdatedMovies.asStateFlow()

    fun fetchRecentlyUpdatedMovies() {
        _recentlyUpdatedMovies.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            val result = getRecentlyUpdatedMoviesUseCase()
            _recentlyUpdatedMovies.value = result
        }
    }

    init {
        fetchRecentlyUpdatedMovies()
    }
}