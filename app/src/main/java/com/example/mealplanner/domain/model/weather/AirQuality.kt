package com.example.mealplanner.domain.model.weather

data class AirQuality(
    val co: Double,
    val no2: Double,
    val o3: Double,
    val pm10: Double,
    val pm2_5: Double,
    val so2: Double,
    val gbDefraIndex: Int,
    val usEpaIndex: Int
)
