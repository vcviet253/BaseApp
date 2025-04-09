package com.example.mealplanner.presentation.mealdetail

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.util.fastReduce
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.common.Constants
import com.example.mealplanner.common.Resource
import com.example.mealplanner.domain.model.Meal
import com.example.mealplanner.domain.usecase.getmeal.GetMealUseCase
import com.example.mealplanner.domain.usecase.getmeal.GetRandomMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MealDetailViewModel @Inject constructor(
    private val getMealUseCase: GetMealUseCase,
    private val getRandomMealUseCase: GetRandomMealUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(MealDetailState())
    val state = _state.asStateFlow()

    init {
//        savedStateHandle.get<String>(Constants.PARAM_MEAL_NAME)?.let { mealName ->
//            getMealUseCase(mealName = mealName)
//        }
        getMealByName("Arrabiata")
    }


    private fun getMealByName(mealName: String) {
        getMealUseCase(mealName).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = MealDetailState(isLoading = true)
                }

                is Resource.Success -> {
                    _state.value = MealDetailState(meal = result.data?.firstOrNull())
                }

                is Resource.Error -> {
                    _state.value = MealDetailState(
                        error = result.message ?: "An unexpected error has occurred"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getRandomMeal() {
        getRandomMealUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = MealDetailState(isLoading = true)
                }

                is Resource.Success -> {
                    _state.value = MealDetailState(meal = result.data?.firstOrNull())
                }

                is Resource.Error -> {
                    _state.value = MealDetailState(
                        error = result.message ?: "An unexpected error has occurred"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}