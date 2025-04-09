package com.example.mealplanner.domain.usecase.getmeal

import com.example.mealplanner.common.Resource
import com.example.mealplanner.data.remote.dto.MealDto
import com.example.mealplanner.data.remote.dto.toMeal
import com.example.mealplanner.domain.model.Meal
import com.example.mealplanner.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetRandomMealUseCase @Inject constructor(
    private val repository: MealRepository
) {
    operator fun invoke(): Flow<Resource<List<Meal>>> = flow {
        try {
            emit(Resource.Loading<List<Meal>>())
            val meal = repository.getRandomMeal()
            emit(Resource.Success<List<Meal>>(data = meal))
        } catch (e: HttpException) {
            emit(
                Resource.Error<List<Meal>>(
                    message = e.localizedMessage ?: "An unexpected error has occurred"
                )
            )
        } catch (e: IOException) {
            emit(Resource.Error<List<Meal>>("Couldn't reach server. Check your internet connection"))
        }
    }
}