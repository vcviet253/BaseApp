package com.example.mealplanner.data.remote

import com.example.mealplanner.data.remote.dto.weather.currentweather.CurrentWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("aqi") airQualityIncluded: String = "no"
    ) : Response<CurrentWeatherResponse>
}