package com.example.mealplanner.movie.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mealplanner.movie.presentation.bookmarks.BookmarksScreen
import com.example.mealplanner.movie.presentation.home.HomeScreen
import com.example.mealplanner.movie.presentation.movie.MovieScreen
import com.example.mealplanner.movie.presentation.moviesbycategory.MoviesByCategoryScreen
import com.example.mealplanner.movie.presentation.onboarding.OnboardingScreen
import com.example.mealplanner.movie.presentation.playerscreen.MoviePlayerScreen
import com.example.mealplanner.movie.presentation.settings.SettingsScreen

@Composable
fun MovieAppNavHost(navController: NavHostController) {
    NavHost(
        startDestination = MovieAppDestinations.HOME_ROUTE,
        navController = navController,
        // Bạn có thể đặt transition mặc định ở đây nếu muốn
        // enterTransition = { fadeIn(animationSpec = tween(300)) },
        // exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        // --- Màn hình dùng Scaffold chung (Nhóm 1) ---
        composable(
            route = MovieAppDestinations.HOME_ROUTE,
            // Nếu là điều hướng thông thường đến HOME (không phải pop)
            enterTransition = {
                // Giữ nguyên như bạn đã có, nếu bạn muốn HOME slide in từ trái khi được điều hướng tới
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },

            // --- Khi HOME biến mất (điều hướng sang màn hình Detail) ---
            // Home đứng yên khi Detail trượt vào
            exitTransition = {
                // HOME đứng yên, chỉ fade out (hoặc return null nếu không muốn fade)
                fadeOut(animationSpec = tween(400))
            },
            // Khi HOME biến mất vì một màn hình khác bị pop đi (không liên quan trực tiếp đến trường hợp Home -> Detail -> Home)
            popExitTransition = {
                // Giữ nguyên như bạn đã có
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            HomeScreen(navController)
        }

        composable(
            route = MovieAppDestinations.MOVIE_DETAIL_ROUTE,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth }, // Xuất hiện từ bên phải
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth }, // Trượt ra bên phải
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            arguments = listOf(
                navArgument(MovieAppDestinations.MOVIE_DETAIL_ARG_SLUG) {
                    type = NavType.StringType
                },

            )
        ) {
            MovieScreen(navController)
        }

        composable(MovieAppDestinations.SETTINGS_ROUTE) {
            // SettingsScreen KHÔNG cần định nghĩa Scaffold riêng
            SettingsScreen(navController = navController)
        }


        // --- Màn hình KHÔNG dùng Scaffold chung (Nhóm 2) ---
        composable(MovieAppDestinations.ONBOARDING_ROUTE) {
            // OnboardingScreen thường không dùng Scaffold hoặc dùng rất khác
            OnboardingScreen(navController = navController)
        }

        composable(MovieAppDestinations.BOOKMARKS_ROUTE) {
            // BookmarksScreen KHÔNG cần định nghĩa Scaffold riêng
            BookmarksScreen(navController = navController)
        }

        composable(
            route = MovieAppDestinations.MOVIE_PLAYER_ROUTE, // <-- Định nghĩa route với tham số
            arguments = listOf(
                navArgument(MovieAppDestinations.MOVIE_PLAYER_ARG_URL) {
                    type = NavType.StringType // Kiểu dữ liệu của tham số
                }
            )
        ) {
            MoviePlayerScreen(navController) // ViewModel của nó sẽ nhận SavedStateHandle đã có URL
        }

        composable(
            route = MovieAppDestinations.MOVIES_BY_CATEGORY_ROUTE,
            arguments = listOf(navArgument(MovieAppDestinations.MOVIES_BY_CATEGORY_ARG_URL) {
                type = NavType.StringType
            })
        ) {
            MoviesByCategoryScreen(navController)
        }

    }
}