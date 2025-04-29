package com.example.mealplanner.presentation.testlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.mealplanner.presentation.navigation.AppDestinations

@Composable
fun TestListScreen(navController: NavController, testId: Long = 1L) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {
            navController.navigate("${AppDestinations.MAP_LABELING_ROUTE}/$testId")
            println("Navigating to map labeling with testId: $testId") // Debug log
        }) {
            Text("To test")
        }
    }
}