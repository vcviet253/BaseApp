package com.example.mealplanner.domain.model.weather

data class CurrentWeather(
    val temperatureC: Double,
    val feelsLikeC: Double,
    val isDay: Boolean,
    val humidity: Int,
    val condition: Condition,
    val windSpeedKph: Double,
    val pressureMb: Int,
    val visibilityKm: Int,
    val uvIndex: Double,
    val airQuality: AirQuality?, // Optional
    val lastUpdated: String
)
