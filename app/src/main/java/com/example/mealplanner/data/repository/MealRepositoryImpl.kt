package com.example.mealplanner.data.repository

import com.example.mealplanner.data.remote.MealDbApi
import com.example.mealplanner.data.remote.dto.MealDto
import com.example.mealplanner.data.remote.dto.MealsDto
import com.example.mealplanner.data.remote.dto.toMeal
import com.example.mealplanner.domain.model.Meal
import com.example.mealplanner.domain.repository.MealRepository
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val api: MealDbApi
): MealRepository {
    override suspend fun getMealByName(mealName: String): List<Meal> {
        return api.getMealByName(mealName).meals?.map { it.toMeal()} ?: emptyList()
     }

    override suspend fun getRandomMeal(): List<Meal> {
        return api.getRandomMeal().meals?.map {it.toMeal()} ?: emptyList()
    }
}