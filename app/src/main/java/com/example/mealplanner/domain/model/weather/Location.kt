package com.example.mealplanner.domain.model.weather

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val localTime: String,
    val timezoneId: String
)