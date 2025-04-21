package com.example.mealplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mealplanner.common.UserSession
import com.example.mealplanner.data.preferences.UserPreferences
import com.example.mealplanner.presentation.CustomNavigation
import com.example.mealplanner.presentation.chat.ChatScreen
import com.example.mealplanner.presentation.common.GalaxyBackground
import com.example.mealplanner.presentation.home.HomeScreen
import com.example.mealplanner.presentation.login.LoginScreen

import com.example.mealplanner.ui.theme.MealPlannerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("MainActivity: onCreate")

        setContent {
            val navController = rememberNavController()
            CustomNavigation(navController)
        }
    }
}

@Preview
@Composable
fun PreviewBackground() {
    MealPlannerTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            GalaxyBackground()
        }
    }
}