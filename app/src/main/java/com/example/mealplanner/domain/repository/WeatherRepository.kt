package com.example.mealplanner.domain.repository

import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.domain.model.weather.CurrentWeather
import com.example.mealplanner.domain.model.weather.WeatherInfo

interface WeatherRepository {
    suspend fun getCurrentWeather(location: String, aqi: String): Resource<WeatherInfo>
}