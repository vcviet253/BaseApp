package com.example.mealplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.mealplanner.movie.presentation.navigation.MainAppScreen
import com.example.mealplanner.movie.presentation.navigation.MovieAppNavHost
import com.example.mealplanner.ui.theme.MealPlannerTheme
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("MainActivity: onCreate")

        setContent {
            val navController = rememberNavController()
            MainAppScreen()
        }
    }
}

@Preview
@Composable
fun PreviewBackground() {
    MealPlannerTheme {
        Box(modifier = Modifier.fillMaxSize()) {
        }
    }
}