package com.example.mealplanner.presentation.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun TestSreen(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Home Screen",
            modifier = Modifier
                .fillMaxSize().align(Alignment.Center),
        )
    }
}