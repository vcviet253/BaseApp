package com.example.mealplanner.movie.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mealplanner.core.common.Resource

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val recentlyUpdatedMovies by viewModel.recentlyUpdatedMovies.collectAsState()

    when (recentlyUpdatedMovies) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text((recentlyUpdatedMovies as Resource.Error).message, color = MaterialTheme.colorScheme.error)
        is Resource.Success -> {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                (recentlyUpdatedMovies as Resource.Success).data.forEach { movie ->
                    Text(movie.metadata.name)
                }
            }
        }
    }
}