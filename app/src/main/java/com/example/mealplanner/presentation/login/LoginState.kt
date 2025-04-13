package com.example.mealplanner.presentation.login

data class LoginState(
    val isLoading: Boolean = false,
    val token: String = "",
    var error: String = "",
) {
}