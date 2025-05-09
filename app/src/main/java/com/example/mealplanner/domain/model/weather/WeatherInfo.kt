package com.example.mealplanner.domain.model.weather

import com.example.mealplanner.data.remote.dto.weather.currentweather.CurrentWeatherDto
import com.example.mealplanner.data.remote.dto.weather.currentweather.LocationDto

data class WeatherInfo(
    val currentWeather: CurrentWeather,
    val location: Location
)