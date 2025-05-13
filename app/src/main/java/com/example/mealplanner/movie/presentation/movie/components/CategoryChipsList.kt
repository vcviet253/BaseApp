package com.example.mealplanner.movie.presentation.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
            CustomChip(
                onClick = { onCategoryClick(category) },
                text = category.name
            )
        }
    }
}

@Composable
fun CustomChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onClick
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 4.dp), // Điều chỉnh sát mép ở đây
            style = MaterialTheme.typography.labelMedium
        )
    }
}
