package com.example.mealplanner.data.remote.dto.weather.currentweather

data class CurrentWeatherResponse(
    val current: CurrentWeatherDto,
    val location: LocationDto
)