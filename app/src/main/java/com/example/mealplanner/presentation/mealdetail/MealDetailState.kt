package com.example.mealplanner.presentation.mealdetail

import com.example.mealplanner.domain.model.Meal

data class MealDetailState(
    val isLoading: Boolean = false,
    val meal: Meal? = null,
    val error: String = ""
) {

}