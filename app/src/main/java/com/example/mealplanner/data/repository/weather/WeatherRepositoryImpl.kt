package com.example.mealplanner.data.repository.weather

import com.example.mealplanner.core.common.Constants
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.data.mapper.toDomain
import com.example.mealplanner.data.remote.WeatherApi
import com.example.mealplanner.data.remote.dto.weather.currentweather.CurrentWeatherResponse
import com.example.mealplanner.domain.model.weather.CurrentWeather
import com.example.mealplanner.domain.model.weather.WeatherInfo
import com.example.mealplanner.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {
    override suspend fun getCurrentWeather(location: String, aqi: String): Resource<WeatherInfo> {
        return try {
            val response = api.getCurrentWeather(
                apiKey = Constants.WEATHER_API_KEY,
                location =  location,
                airQualityIncluded = aqi
            )
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val weatherInfo = dto.toDomain()
                    Resource.Success(weatherInfo)
                } ?: Resource.Error("Empty response body")
            } else {
                println("Error ${response.code()}: ${response.message()}")
                Resource.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            println("ERROR ${e.localizedMessage ?: "Unknown error"}")
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}