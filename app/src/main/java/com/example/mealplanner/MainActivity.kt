package com.example.mealplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.example.mealplanner.presentation.diaryscreen.DiaryScreen
import com.example.mealplanner.presentation.stickmantest.EpicStickmanAnimation
import com.example.mealplanner.presentation.stickmantest.GameScreen
import com.example.mealplanner.presentation.stickmantest.KamehamehaAnimation
import com.example.mealplanner.presentation.stickmantest.KamehamehaCanvas
import com.example.mealplanner.presentation.stickmantest.KamehamehaChargingBeam
import com.example.mealplanner.presentation.stickmantest.KamehamehaDemo
import com.example.mealplanner.presentation.stickmantest.KamehamehaEpic
import com.example.mealplanner.presentation.stickmantest.StickmanCanvas
import com.example.mealplanner.presentation.stickmantest.StickmanFightScene
import com.example.mealplanner.presentation.stickmantest.StickmanFightScreen
import com.example.mealplanner.presentation.stickmantest.StickmanWithSwordCanvas
import com.example.mealplanner.ui.theme.MealPlannerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealPlannerTheme {
                StickmanFightScreen()
            }
        }
    }
}
