package com.example.mealplanner.domain.usecase.weather

import android.adservices.ondevicepersonalization.RenderOutput
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.domain.model.weather.WeatherInfo
import com.example.mealplanner.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
)
{
    suspend operator fun invoke(location: String, aqi: String): Resource<WeatherInfo> {
        return weatherRepository.getCurrentWeather(location, aqi)
    }
}