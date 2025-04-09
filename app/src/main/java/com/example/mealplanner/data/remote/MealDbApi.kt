package com.example.mealplanner.data.remote

import com.example.mealplanner.data.remote.dto.MealDto
import com.example.mealplanner.data.remote.dto.MealsDto
import com.example.mealplanner.domain.model.Meal
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MealDbApi {
    @GET("/api/json/v1/1/search.php")
    suspend fun getMealByName(@Query("s") mealName: String): MealsDto

    @GET("/api/json/v1/1/random.php")
    suspend fun getRandomMeal(): MealsDto
}