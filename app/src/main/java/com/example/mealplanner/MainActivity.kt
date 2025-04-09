package com.example.mealplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.example.mealplanner.presentation.diaryscreen.DiaryScreen
import com.example.mealplanner.ui.theme.MealPlannerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealPlannerTheme {
                DiaryScreen()
            }
        }
    }
}
