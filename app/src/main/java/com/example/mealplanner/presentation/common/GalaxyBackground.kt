package com.example.mealplanner.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


@Composable
fun GalaxyBackground() {
    val darkBlue = Color(0xFF0D1B2A)
    val deepPurple = Color(0xFF1B003B)
    val teal = Color(0xFF00FFFF)
    val violet = Color(0xFF8A2BE2)
    val atmosphereColors = listOf(
        Color(0xFF00FFFF).copy(alpha = 0.1f),
        Color(0xFFBA68C8).copy(alpha = 0.1f),
        Color(0xFF82B1FF).copy(alpha = 0.08f)
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Nền chính
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(darkBlue, deepPurple),
                startY = 0f,
                endY = size.height
            ),
            size = size
        )

        // Dải ngân hà chéo
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, violet.copy(alpha = 0.3f), teal.copy(alpha = 0.3f), Color.Transparent),
                start = Offset(x = size.width * -0.2f, y = size.height * 0.2f),
                end = Offset(x = size.width * 1.2f, y = size.height * 0.8f)
            ),
            size = size
        )

        // Vẽ các dải khí quyển (đường cong nhiều lớp)
        atmosphereColors.forEachIndexed { index, color ->
            val path = Path().apply {
                val centerX = size.width / 2
                val centerY = size.height * 0.4f
                val radius = size.minDimension * (0.3f + index * 0.07f)
                moveTo(centerX + radius, centerY)
                for (i in 0..360 step 2) {
                    val angle = i * (PI / 180f)
                    val x = centerX + radius * cos(angle).toFloat()
                    val y = centerY + radius * sin(angle).toFloat() * 0.5f // squash vertically
                    lineTo(x, y)
                }
                close()
            }

            drawPath(
                path = path,
                brush = Brush.radialGradient(
                    colors = listOf(color, Color.Transparent),
                    center = Offset(size.width / 2, size.height * 0.4f),
                    radius = size.minDimension * 0.5f
                ),
                style = Stroke(width = 1.5f)
            )
        }

        // Luồng sáng nghiêng qua màn hình
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.07f), Color.Transparent),
                start = Offset(0f, size.height * 0.2f),
                end = Offset(size.width, size.height * 0.8f)
            ),
            size = size,
            blendMode = BlendMode.Softlight
        )

        // Sao nhỏ
        repeat(200) {
            val x = Random.nextFloat() * size.width
            val y = Random.nextFloat() * size.height
            val radius = Random.nextFloat() * 1.5f + 0.3f
            drawCircle(
                color = Color.White.copy(alpha = Random.nextFloat() * 0.5f + 0.3f),
                radius = radius,
                center = Offset(x, y)
            )
        }

        // Sao màu trung bình
        val colors = listOf(Color.Cyan, Color.Magenta, Color(0xFFFFF9C4))
        repeat(50) {
            val x = Random.nextFloat() * size.width
            val y = Random.nextFloat() * size.height
            val radius = Random.nextFloat() * 2.5f + 1.5f
            drawCircle(
                color = colors.random().copy(alpha = 0.5f),
                radius = radius,
                center = Offset(x, y)
            )
        }

        // Sao lớn
        repeat(10) {
            val x = Random.nextFloat() * size.width
            val y = Random.nextFloat() * size.height
            val radius = Random.nextFloat() * 6f + 4f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color.Transparent),
                    radius = radius * 2,
                    center = Offset(x, y)
                ),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}