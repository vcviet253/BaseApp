package com.example.mealplanner.movie.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val navController = rememberNavController()

    // Xác định route hiện tại để quyết định hiển thị các thành phần của Scaffold (Bottom Bar, Top Bar...)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // kiểm tra xem route hiện tại có cần Bottom Bar không
    val shouldShowBottomBar = when (currentRoute) {
        MovieAppDestinations.HOME_ROUTE -> true
        MovieAppDestinations.MOVIE_DETAIL_ROUTE -> true
        MovieAppDestinations.SETTINGS_ROUTE -> true
        // Thêm các route khác cần Bottom Bar vào đây
        else -> false // Các route khác không hiển thị Bottom Bar
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // NavHost chứa tất cả các màn hình (cả có và không có Scaffold chung)
            MovieAppNavHost(navController = navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, MovieAppDestinations.HOME_ROUTE),
        BottomNavItem("Bookmarks", Icons.Default.Bookmark, MovieAppDestinations.BOOKMARKS_ROUTE),
        BottomNavItem("Settings", Icons.Default.Settings, MovieAppDestinations.SETTINGS_ROUTE)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

