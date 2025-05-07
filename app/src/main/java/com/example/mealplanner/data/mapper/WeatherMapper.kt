package com.example.mealplanner.data.mapper

import com.example.mealplanner.data.remote.dto.weather.currentweather.AirQualityDto
import com.example.mealplanner.data.remote.dto.weather.currentweather.ConditionDto
import com.example.mealplanner.data.remote.dto.weather.currentweather.CurrentWeatherDto
import com.example.mealplanner.data.remote.dto.weather.currentweather.CurrentWeatherResponse
import com.example.mealplanner.data.remote.dto.weather.currentweather.LocationDto
import com.example.mealplanner.domain.model.weather.AirQuality
import com.example.mealplanner.domain.model.weather.Condition
import com.example.mealplanner.domain.model.weather.CurrentWeather
import com.example.mealplanner.domain.model.weather.Location
import com.example.mealplanner.domain.model.weather.WeatherInfo

fun CurrentWeatherDto.toDomain(): CurrentWeather = CurrentWeather(
    temperatureC = temp_c,
    feelsLikeC = feelslike_c,
    isDay = is_day == 1,
    humidity = humidity,
    condition = condition.toDomain(),
    windSpeedKph = wind_kph,
    pressureMb = pressure_mb,
    visibilityKm = vis_km,
    uvIndex = uv,
    airQuality = air_quality?.toDomain(),
    lastUpdated = last_updated
)

fun ConditionDto.toDomain(): Condition = Condition(
    text = text,
    iconUrl = icon
)

fun AirQualityDto.toDomain(): AirQuality = AirQuality(
    co = co,
    no2 = no2,
    o3 = o3,
    pm10 = pm10,
    pm2_5 = pm2_5,
    so2 = so2,
    gbDefraIndex = gbDefraIndex,
    usEpaIndex = usEpaIndex
)

fun LocationDto.toDomain(): Location = Location(
    name = name,
    region = region,
    country = country,
    localTime = localtime,
    timezoneId = tz_id
)

fun CurrentWeatherResponse.toDomain(): WeatherInfo {
    return WeatherInfo(
        currentWeather = this.current.toDomain(),
        location = this.location.toDomain(),
    )
}