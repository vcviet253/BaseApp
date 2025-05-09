package com.example.mealplanner.presentation.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mealplanner.domain.model.Message
import com.example.mealplanner.domain.model.MessageStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean,
    avatarUrl: String? = null,
    isLastMessageFromCurrentUser: Boolean,
    isExpanded: Boolean = false,
    onRetry: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() },
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!isCurrentUser) {
            Avatar(avatarUrl)
            Spacer(modifier = Modifier.width(6.dp))
        }
        Box {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomEnd = if (isCurrentUser) 0.dp else 16.dp,
                    bottomStart = if (isCurrentUser) 16.dp else 0.dp
                ),
                color = if (isCurrentUser) Color(0xFFDCF8C6) else Color.White,
                shadowElevation = 2.dp, // nếu bạn dùng Material3
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 240.dp)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTimestamp(message.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }


        if (isCurrentUser) {
            Spacer(modifier = Modifier.width(6.dp))
            Avatar(avatarUrl)
        }
    }
}

fun formatTimestamp(timeSeconds: Long): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault() // <- set theo hệ thống
    return formatter.format(Date(timeSeconds * 1000))
}

@Composable
fun MessageStatusBox(
    status: MessageStatus,
    onRetry: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFE0E0E0), // màu xám nhạt
        modifier = Modifier
            .padding(end = 48.dp) // tránh đè avatar
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (status) {
                MessageStatus.SENDING -> {
                    Text(
                        text = "Sending",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Sending",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                MessageStatus.SENT -> {
                    Text(
                        text = "Sent",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Sent",
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                }

                MessageStatus.FAILED -> {
                    Text(
                        text = "Failed",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onRetry) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun MessageWithStatus(
    message: Message,
    isCurrentUser: Boolean,
    avatarUrl: String? = null,
    isLastMessageFromCurrentUser: Boolean,
    isExpanded: Boolean = false,
    onRetry: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        MessageBubble(
            message = message,
            isCurrentUser = isCurrentUser,
            avatarUrl = avatarUrl,
            isLastMessageFromCurrentUser = isLastMessageFromCurrentUser,
            isExpanded = isExpanded,
            onRetry = onRetry,
            onClick = onClick
        )

        AnimatedVisibility(
            visible = isExpanded, // Chỉ hiển thị khi mở rộng
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) { it / 2 },
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 50) // Almost instant exit
            ) { it / 2 }
        ) {
            // Trạng thái nằm bên ngoài bubble
            if (isCurrentUser && (isLastMessageFromCurrentUser || isExpanded)) {
                Spacer(modifier = Modifier.height(2.dp))
                MessageStatusBox(
                    status = message.status,
                    onRetry = onRetry
                )
            }
        }
    }
}