package com.example.mealplanner.movie.presentation.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mealplanner.movie.domain.model.Category

@Composable
fun CategoryChipsList(
    categories: List<Category>,
    modifier: Modifier = Modifier,
    onCategoryClick: (Category) -> Unit = {},
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp), // Khoảng cách ngang giữa các Chip
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        categories.forEach { category ->
            AssistChip(
                onClick = { onCategoryClick(category) },
                label = { Text(category.name) }
            )
        }
    }
}