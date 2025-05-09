package com.example.mealplanner.presentation.login

sealed class UiEvent {
    data class Navigation(val route: String): UiEvent()
    data class ShowSnackBar(val message: String): UiEvent()
    data class ShowToast(val message: String): UiEvent()
}