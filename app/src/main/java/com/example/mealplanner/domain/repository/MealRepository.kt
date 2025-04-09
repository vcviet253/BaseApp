package com.example.mealplanner.domain.repository

import com.example.mealplanner.data.remote.dto.MealDto
import com.example.mealplanner.data.remote.dto.MealsDto
import com.example.mealplanner.domain.model.Meal

interface MealRepository {
    suspend fun getMealByName(mealName : String): List<Meal>
    suspend fun getRandomMeal(): List<Meal>
}