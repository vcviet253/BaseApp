package com.example.mealplanner.presentation.navigation

import android.app.Activity.MODE_PRIVATE
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mealplanner.common.Constants
import com.example.mealplanner.common.UserSession
import com.example.mealplanner.data.preferences.UserPreferences
import com.example.mealplanner.presentation.chat.ChatScreen
import com.example.mealplanner.presentation.home.HomeScreen
import com.example.mealplanner.presentation.login.LoginScreen
import com.example.mealplanner.presentation.maplabeling.MapLabelingScreen
import com.example.mealplanner.presentation.splash.SplashScreen
import com.example.mealplanner.presentation.testlist.TestListScreen
import kotlinx.coroutines.delay

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AppDestinations.TEST_LIST_ROUTE) {
        // Destination 1: Test List Screen
        composable(route = AppDestinations.TEST_LIST_ROUTE) {
            TestListScreen(navController = navController)
        }

        // Destination 2: Map Labeling Screen
        composable(
            route = AppDestinations.MAP_LABELING_FULL_ROUTE, // Use route with placeholder
            arguments = listOf(
                // Define the expected argument "testId" and its type
                navArgument(AppDestinations.MAP_LABELING_ARG_TEST_ID) {
                    type = NavType.LongType
                    // nullable = false // Default is false
                    // defaultValue = -1L // Optional default value
                }
            )
        ) {
            // No need to manually extract argument here for the screen composable itself,
            // as the ViewModel will get it via SavedStateHandle.
            MapLabelingScreen()
        }
    }


//
//    val context = LocalContext.current
//
//    val userPreferences = UserPreferences(context.getSharedPreferences("user_prefs", MODE_PRIVATE))
//    UserSession.userId = userPreferences.getUserId()
//
//    var userId by remember { mutableStateOf<String?>("no") }
//
//    LaunchedEffect(Unit) {
//        delay(5000)
//        userId = userPreferences.getUserId() // đọc từ SharedPreferences
//    }
//
//    LaunchedEffect(userId) {
//        when (userId) {
//            "no" -> {}
//            null -> navController.navigate(Constants.LOGIN_ROUTE) {
//                popUpTo(0) { inclusive = true } // Xóa toàn bộ back stack
//            }
//            else -> navController.navigate(Constants.CHAT_ROUTE) {
//                popUpTo(0) { inclusive = true } // Xóa toàn bộ back stack
//            }
//        }
//    }
//
//    NavHost(
//        navController = navController,
//        startDestination = Constants.SPLASH_ROUTE
//    )
//    {
//        composable(Constants.SPLASH_ROUTE) {
//            SplashScreen()
//        }
//        composable(Constants.LOGIN_ROUTE) { LoginScreen(navController) }
//        composable(Constants.CHAT_ROUTE) { ChatScreen(navController) }
//        composable(Constants.HOME_ROUTE) { HomeScreen(navController) }
//    }
}