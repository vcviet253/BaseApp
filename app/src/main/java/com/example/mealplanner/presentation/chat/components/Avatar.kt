package com.example.mealplanner.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun Avatar(avatarUrl: String?) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
    ) {
        if (avatarUrl != null) {
            // Example using Coil
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder avatar
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
