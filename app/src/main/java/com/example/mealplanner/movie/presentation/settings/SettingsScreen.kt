package com.example.mealplanner.movie.presentation.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@Composable
fun CustomVideoPlayerCompose(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // --- State cho UI Controls ---
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var totalDuration by remember { mutableLongStateOf(0L) }
    var bufferedPercentage by remember { mutableIntStateOf(0) }
    var showControls by remember { mutableStateOf(true) } // Hiển thị controls ban đầu

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // Thêm listener để cập nhật trạng thái UI
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                    isPlaying = isPlayingValue
                }

                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) ||
                        events.contains(Player.EVENT_IS_PLAYING_CHANGED) ||
                        events.contains(Player.EVENT_TIMELINE_CHANGED) ||
                        events.contains(Player.EVENT_POSITION_DISCONTINUITY) || // Quan trọng khi seek
                        events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) { // Quan trọng khi đổi media item
                        currentPosition = player.currentPosition
                        totalDuration = player.duration.coerceAtLeast(0L) // Đảm bảo không âm
                        bufferedPercentage = player.bufferedPercentage
                    }
                }
            })
        }
    }

    LaunchedEffect(videoUrl) {
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        // exoPlayer.playWhenReady = true // Để người dùng tự nhấn play ban đầu
    }

    // Cập nhật vị trí hiện tại một cách mượt mà hơn khi đang phát
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition
            delay(100L) // Cập nhật mỗi 100ms, có thể điều chỉnh
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = modifier.background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    // !!! Quan trọng: Ẩn controller mặc định !!!
                    useController = false
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    // Thêm sự kiện click vào PlayerView để ẩn/hiện controls
                    setOnClickListener {
                        showControls = !showControls
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // --- Custom Controls UI ---
        if (showControls) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom, // Đặt controls ở dưới
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hàng chứa thời gian và thanh seekbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(currentPosition),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Thanh SeekBar
                    Box(modifier = Modifier.weight(1f)) {
                        // Thanh buffer
                        LinearProgressIndicator(
                            progress = { bufferedPercentage / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.LightGray.copy(alpha = 0.5f),
                            trackColor = Color.Transparent // Ẩn track chính của buffer
                        )
                        // Thanh seek chính
                        Slider(
                            value = if (totalDuration > 0) currentPosition.toFloat() / totalDuration else 0f,
                            onValueChange = {
                                val newPosition = (it * totalDuration).toLong()
                                currentPosition = newPosition // Cập nhật UI ngay lập tức
                                exoPlayer.seekTo(newPosition)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color.Gray
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatDuration(totalDuration),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nút Play/Pause
                IconButton(
                    onClick = {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            // Nếu hết video thì seek về đầu rồi play
                            if (exoPlayer.playbackState == Player.STATE_ENDED) {
                                exoPlayer.seekTo(0)
                            }
                            exoPlayer.play()
                        }
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Bạn có thể thêm các nút khác ở đây (ví dụ: Tua tới/lui, Fullscreen, v.v.)
            }
        }
    }
}

// Hàm tiện ích để định dạng thời gian từ milliseconds sang MM:SS hoặc HH:MM:SS
fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    CustomVideoPlayerCompose("https://s4.phim1280.tv/20250325/15U0OSx5/index.m3u8")
}