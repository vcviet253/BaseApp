package com.example.mealplanner.domain.model

data class Meal(
    val mealName: String?,
    val area: String?,
    val category: String?,
    val imageSource: Any?,
    val instructions: String?,
    val ingredients: Map<String, String>,
    val tags: List<String>,
    val youtubeUrl: String?,
) {
}
