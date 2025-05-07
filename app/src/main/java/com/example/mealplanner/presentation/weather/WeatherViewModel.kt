package com.example.mealplanner.presentation.weather

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.domain.model.weather.WeatherInfo
import com.example.mealplanner.domain.usecase.weather.GetCurrentWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
): ViewModel() {
    private val _weatherInfoState = MutableStateFlow<Resource<WeatherInfo>>(Resource.Loading())
    val weatherInfoState: StateFlow<Resource<WeatherInfo>> = _weatherInfoState

    // StateFlow to expose the state to the UI using stateIn
//    val weatherState: StateFlow<Resource<WeatherInfo>> = getCurrentWeatherUseCase("your_api_key", "your_location")
//        .stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    // Function to fetch weather data
//    fun fetchWeather(apiKey: String, location: String) {
//        // Collecting the flow from the use case in the ViewModel
//        viewModelScope.launch {
//            // Collecting the flow from the use case
//            getCurrentWeatherUseCase(apiKey, location)
//                .collect { resource ->
//                    // Updating the state based on the result
//                    _weatherState.value = resource
//                }
//        }
//    }

    fun fetchCurrentWeather(location: String, aqi: String = "no") {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherInfoState.value = Resource.Loading()
            val result = getCurrentWeatherUseCase(location, aqi)
            _weatherInfoState.value = when (result) {
                is Resource.Success -> Resource.Success(result.data)
                is Resource.Error -> Resource.Error(result.message)
                else -> Resource.Loading()
            }
        }
    }

    init {
        fetchCurrentWeather("London")
    }
}