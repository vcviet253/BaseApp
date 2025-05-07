package com.example.mealplanner.data.remote.dto.weather.currentweather

data class CurrentWeatherDto(
    val air_quality: AirQualityDto?,
    val cloud: Int,
    val condition: ConditionDto,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val gust_kph: Double,
    val gust_mph: Double,
    val humidity: Int,
    val is_day: Int,
    val last_updated: String,
    val last_updated_epoch: Int,
    val precip_in: Int,
    val precip_mm: Double,
    val pressure_in: Double,
    val pressure_mb: Int,
    val temp_c: Double,
    val temp_f: Double,
    val uv: Double,
    val vis_km: Int,
    val vis_miles: Int,
    val wind_degree: Int,
    val wind_dir: String,
    val wind_kph: Double,
    val wind_mph: Double
)