package com.example.mealplanner.movie.presentation.moviesbycategory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.mealplanner.movie.presentation.navigation.MovieAppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoviesByCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val categorySlug: String =
        checkNotNull(savedStateHandle[MovieAppDestinations.MOVIES_BY_CATEGORY_ARG_URL])
}