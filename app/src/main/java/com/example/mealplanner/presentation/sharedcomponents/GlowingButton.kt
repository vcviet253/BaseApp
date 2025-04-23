package com.example.mealplanner.presentation.sharedcomponents

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlowingEffectButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Tạo chuyển động cho hiệu ứng ánh sáng
    val infiniteTransition = rememberInfiniteTransition(label = "GlowTransition")
    val glowOffsetX by infiniteTransition.animateFloat(
        initialValue = -500f,  // Bắt đầu ở ngoài màn hình bên trái
        targetValue = 500f,    // Kết thúc ở ngoài màn hình bên phải
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing), // Di chuyển mượt mà
            repeatMode = RepeatMode.Restart
        ),
        label = "GlowMovement"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)) // Bo góc cho đẹp
            .background(Color(0xFF121212)) // Nền tối của button
            .clickable { onClick() }
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .drawBehind {
                // Vẽ gradient với tia sáng loang
                val gradient =  Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFFFA500), // Ánh sáng cam
                        Color.Transparent
                    ),
                    start = Offset(glowOffsetX, 0f), // Vị trí bắt đầu (di chuyển từ trái sang phải)
                    end = Offset(glowOffsetX + 300f, size.height) // Kết thúc gradient
                )
                drawRect(
                    brush = gradient,
                    size = size
                )
            }
    ) {
        Text(
            text = "Glowing Button",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ShinyMovingLightButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LightTransition")

    // Animate vị trí tia sáng từ trái -> phải
    val glowOffsetX by infiniteTransition.animateFloat(
        initialValue = -400f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GlowOffsetX"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp)) // Góc bo tròn đẹp
            .background(Color(0xFF1F1F1F)) // Nền button
            .clickable { onClick() }
            .drawBehind {
                // Tạo hiệu ứng ánh sáng bóng loang
                val gradient = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    start = Offset(glowOffsetX, 0f),
                    end = Offset(glowOffsetX + 300f, size.height)
                )
                drawRect(
                    brush = gradient,
                    size = size
                )
            }
            .padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}