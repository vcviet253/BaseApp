package com.example.mealplanner.movie.presentation.playerscreen

//import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.SideEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleEventObserver
//import androidx.lifecycle.compose.LocalLifecycleOwner
//import androidx.media3.ui.PlayerView
//import androidx.navigation.NavController
//import com.google.accompanist.systemuicontroller.rememberSystemUiController // Thêm dependency nếu chưa có
//
//
//@Composable
//fun MoviePlayerScreen(
//    navController: NavController,
//    viewModel: MoviePlayerViewModel = hiltViewModel()
//) {
//    val context = LocalContext.current
//    val playerInstance = viewModel.playerInstance // Lấy player từ ViewModel
//    val isPlayerReady by viewModel.isPlayerReady.collectAsState()
//    val movieUrl by viewModel.movieUrl.collectAsState() // Có thể dùng để hiển thị title hoặc debug
//
//    // QUAN TRỌNG: State để kiểm soát việc hiển thị AndroidView
//    var showPlayerView by remember { mutableStateOf(true) } // Ban đầu hiển thị
//
//    // --- Fullscreen Handling ---
//    val systemUiController = rememberSystemUiController()
//    val originalSystemBarsVisible = remember { systemUiController.isSystemBarsVisible }
//
//    BackHandler {
//        showPlayerView = false // Ẩn player ngay lập tức
//        navController.popBackStack()
//    }
//
//    SideEffect { // Sử dụng SideEffect để thay đổi System UI một cách an toàn
//        systemUiController.isSystemBarsVisible = false // Ẩn status bar và navigation bar
//    }
//
//    // --- Player Lifecycle Management with Composable Lifecycle ---
//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(lifecycleOwner, playerInstance) {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_PAUSE -> {
//                    playerInstance?.pause()
//                }
//
//                Lifecycle.Event.ON_RESUME -> {
//                    // playerInstance?.play() // Cân nhắc việc tự động play khi resume
//                }
//                // ViewModel's onCleared sẽ lo việc release
//                else -> {}
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//            // Khôi phục lại System UI khi màn hình bị dispose
//            systemUiController.isSystemBarsVisible = originalSystemBarsVisible
//            // QUAN TRỌNG: Đặt trạng thái này thành false ngay khi Composable bắt đầu dispose
//            // để AndroidView biến mất khỏi Composition ngay lập tức về mặt logic.
//            showPlayerView = false
//            // QUAN TRỌNG: Dừng phát và xóa nội dung của player ngay lập tức
//            // Điều này giúp loại bỏ frame cuối cùng của video khi màn hình được loại bỏ.
//            playerInstance?.let {
//                it.stop() // Dừng phát lại
//                it.clearMediaItems() // Xóa các item media khỏi player
//                // Không gọi it.release() ở đây vì ViewModel sẽ lo việc đó trong onCleared()
//                // và việc release một player có thể gây ra lỗi nếu nó vẫn đang được sử dụng bởi PlayerView
//                // hoặc nếu playerInstance được giữ lại một cách không mong muốn.
//            }
//            // Player sẽ được release một cách an toàn trong onCleared của ViewModel
//        }
//    }
//
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black) // Nền đen cho player
//    ) {
//        if (showPlayerView && playerInstance != null && movieUrl.isNotBlank()) {
//            AndroidView(
//                factory = { ctx ->
//                    PlayerView(ctx).apply {
//                        player = playerInstance
//                        useController = true // Sử dụng controller mặc định của ExoPlayer
//                        // Các tùy chỉnh khác cho PlayerView nếu cần
//                    }
//                },
//                modifier = Modifier.fillMaxSize(),
//                update = { view -> // Được gọi khi playerInstance thay đổi (dù ViewModel nên giữ nó ổn định)
//                    view.player = playerInstance
//                }
//            )
//        } else if (movieUrl.isBlank()) {
//            Text(
//                text = "Không có URL phim hợp lệ.",
//                color = Color.White,
//                modifier = Modifier.align(Alignment.Center)
//            )
//        } else {
//            // Hiển thị loading hoặc thông báo lỗi nếu player chưa sẵn sàng hoặc có vấn đề
//            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//        }
//
//        // Nút Back (tùy chọn, vì người dùng có thể dùng nút back hệ thống)
//        IconButton(
//            onClick = {
//                showPlayerView = false // Ẩn PlayerView trước khi quay lại
//                navController.popBackStack()
//            },
//            modifier = Modifier
//                .align(Alignment.TopStart)
//                .padding(all = 8.dp)
//        ) {
//            Icon(
//                Icons.AutoMirrored.Filled.ArrowBack,
//                contentDescription = "Quay lại",
//                tint = Color.White
//            )
//        }
//    }
//}


import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.mealplanner.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import java.util.Formatter
import java.util.Locale

@Composable
fun MoviePlayerScreen(
    navController: NavController,
    viewModel: MoviePlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val playerInstance = viewModel.playerInstance
    val isPlayerReady by viewModel.isPlayerReady.collectAsState()
    val movieUrl by viewModel.movieUrl.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val bufferedPosition by viewModel.bufferedPosition.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()

    // --- MỚI: Trạng thái tốc độ phát ---
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val availableSpeeds =
        remember { viewModel.availableSpeeds } // Lấy danh sách tốc độ từ ViewModel

    // --- MỚI: Trạng thái âm lượng ---
    val volume by viewModel.volume.collectAsState()

    var showPlayerView by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }

    // --- MỚI: Biến để kích hoạt reset hẹn giờ tự động ẩn ---
    var userInteractionKey by remember { mutableStateOf(0) } // Thay đổi giá trị để reset hẹn giờ

    val systemUiController = rememberSystemUiController()
    val originalSystemBarsVisible = remember { systemUiController.isSystemBarsVisible }

    // --- SỬA ĐỔI: LaunchedEffect để reset hẹn giờ khi có tương tác ---
    LaunchedEffect(showControls, userInteractionKey) { // Thêm userInteractionKey vào key list
        if (showControls) {
            delay(3000L) // Thời gian trễ
            showControls = false
        }
    }

    BackHandler {
        if (isFullscreen) {
            isFullscreen = false
            systemUiController.isSystemBarsVisible = true
        } else {
            showPlayerView = false
            navController.popBackStack()
        }
    }

    SideEffect {
        systemUiController.isSystemBarsVisible = !isFullscreen
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, playerInstance) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    playerInstance?.pause()
                }

                Lifecycle.Event.ON_RESUME -> { /* playerInstance?.play() */
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            systemUiController.isSystemBarsVisible = originalSystemBarsVisible
            showPlayerView = false
            playerInstance?.let {
                it.stop()
                it.clearMediaItems()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            // SỬA ĐỔI: Mỗi lần chạm, không chỉ toggle showControls mà còn reset hẹn giờ
            .clickable {
                showControls = !showControls
                userInteractionKey++ // Tăng giá trị để kích hoạt LaunchedEffect
            }
    ) {
        if (showPlayerView && playerInstance != null && movieUrl.isNotBlank()) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = playerInstance
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    view.player = playerInstance
                }
            )

            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                PlayerController(
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    duration = duration,
                    bufferedPosition = bufferedPosition,
                    onPlayPauseClick = {
                        if (isPlaying) viewModel.pause() else viewModel.play()
                        userInteractionKey++ // Reset hẹn giờ khi play/pause
                    },
                    onSeek = { position ->
                        viewModel.seekTo(position)
                        userInteractionKey++ // Reset hẹn giờ khi tua

                    },
                    onSeekForward = { viewModel.seekForward()
                        userInteractionKey++ // Reset hẹn giờ khi tua
                    },
                    onSeekRewind = { viewModel.seekRewind()
                        userInteractionKey++
                    },
                    onBackClick = {
                        showPlayerView = false
                        navController.popBackStack()
                        userInteractionKey++},
                    onFullscreenClick = {
                        isFullscreen = !isFullscreen
                        systemUiController.isSystemBarsVisible = !isFullscreen
                        userInteractionKey++
                        // TODO: Thêm logic xoay màn hình nếu cần
                    },
                    isFullscreen = isFullscreen,
                    playbackState = playbackState,
                    // --- MỚI: Truyền các tham số tốc độ ---
                    playbackSpeed = playbackSpeed,
                    availableSpeeds = availableSpeeds,
                    onSpeedChange = { speed -> viewModel.setPlaybackSpeed(speed)
                        userInteractionKey++},
                    // --- MỚI: Truyền các tham số âm lượng ---
                    volume = volume,
                    onVolumeChange = { vol -> viewModel.setVolume(vol)
                        userInteractionKey++},
                    onIncreaseVolume = { viewModel.increaseVolume()
                        userInteractionKey++},
                    onDecreaseVolume = { viewModel.decreaseVolume()
                        userInteractionKey++}

                )
            }
        } else if (movieUrl.isBlank()) {
            Text(
                text = "Không có URL phim hợp lệ.",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            if (playbackState == Player.STATE_BUFFERING) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Text(
                    text = "Đang tải...",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// --- Composable cho Custom Controller ---
@Composable
fun PlayerController(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    bufferedPosition: Long,
    onPlayPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onSeekForward: () -> Unit,
    onSeekRewind: () -> Unit,
    onBackClick: () -> Unit,
    onFullscreenClick: () -> Unit,
    isFullscreen: Boolean,
    playbackState: Int,
    // --- MỚI: Tham số tốc độ phát ---
    playbackSpeed: Float,
    availableSpeeds: List<Float>,
    onSpeedChange: (Float) -> Unit,
    // --- MỚI: Tham số âm lượng ---
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    onIncreaseVolume: () -> Unit,
    onDecreaseVolume: () -> Unit
) {
    var showSpeedMenu by remember { mutableStateOf(false) } // State để hiển thị menu tốc độ
    var showVolumeControls by remember { mutableStateOf(false) } // State để hiển thị/ẩn thanh âm lượng


    Box(modifier = Modifier.fillMaxSize()) {
        // --- Top Bar (Back Button, Title, Speed Button) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White
                )
            }

            // --- Nút & thanh điều chỉnh âm lượng (ví dụ đơn giản) ---
            IconButton(onClick = { showVolumeControls = !showVolumeControls }) {
                Icon(
                    imageVector = if (volume == 0f) Icons.Default.VolumeMute else if (volume < 0.5f) Icons.Default.VolumeDown else Icons.Default.VolumeUp,
                    contentDescription = "Âm lượng",
                    tint = Color.White
                )
            }

            AnimatedVisibility(
                visible = showVolumeControls,
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                Slider(
                    value = volume,
                    onValueChange = { onVolumeChange(it) },
                    valueRange = 0f..1.0f,
                    modifier = Modifier.width(120.dp), // Kích thước slider nhỏ hơn
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.Gray
                    )
                )
            }
            // --- KẾT THÚC phần điều chỉnh âm lượng ---

            Spacer(modifier = Modifier.weight(1f)) // Đẩy các nút sang hai bên

            // --- Nút điều chỉnh tốc độ ---
            Box { // Box để neo DropdownMenu
                IconButton(onClick = { showSpeedMenu = true }) {
                    Icon(
                        Icons.Default.Speed, // Icon tốc độ
                        contentDescription = "Tốc độ phát",
                        tint = Color.White
                    )
                }
                DropdownMenu(
                    expanded = showSpeedMenu,
                    onDismissRequest = { showSpeedMenu = false }
                ) {
                    availableSpeeds.forEach { speed ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "${speed}x",
                                    color = Color.White
                                )
                            }, // Text hiển thị tốc độ
                            onClick = {
                                onSpeedChange(speed)
                                showSpeedMenu = false // Đóng menu sau khi chọn
                            },
                            modifier = Modifier.background(
                                if (speed == playbackSpeed) MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.3f
                                ) else Color.Transparent
                            )
                        )
                    }
                }
            }
        }

        // --- Loading/Buffering Indicator (ở giữa màn hình) ---
        if (playbackState == Player.STATE_BUFFERING) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // --- Middle Controls (Tua nhanh/chậm, Play/Pause) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSeekRewind) {
                Icon(
                    painter = painterResource(R.drawable.backward_10_seconds),
                    contentDescription = "Tua lùi 10 giây",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Tạm dừng" else "Phát",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
            IconButton(onClick = onSeekForward) {
                Icon(
                    painter = painterResource(R.drawable.forward_10_seconds),
                    contentDescription = "Tua tiến 10 giây",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // --- Bottom Bar (Tiến trình, Thời gian, Fullscreen) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Slider(
                value = currentPosition.toFloat(),
                onValueChange = { newValue -> onSeek(newValue.toLong()) },
                valueRange = 0f..duration.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = Color.Gray,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                )
            )

            LinearProgressIndicator(
                progress = if (duration > 0) bufferedPosition.toFloat() / duration else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .offset(y = (-12).dp),
                color = Color.White.copy(alpha = 0.5f),
                trackColor = Color.Transparent
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
                    color = Color.White,
                    fontSize = 12.sp
                )
                IconButton(onClick = onFullscreenClick) {
                    Icon(
                        if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = if (isFullscreen) "Thoát toàn màn hình" else "Toàn màn hình",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// PlayerController.kt (hoặc file chứa hàm này)
@Composable
private fun formatTime(milliseconds: Long): String {
    val safeMilliseconds = milliseconds.coerceAtLeast(0L)

    val totalSeconds = safeMilliseconds / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    // Sử dụng remember để tái sử dụng Formatter và StringBuilder
    val stringBuilder = remember { StringBuilder() }
    val formatter = remember { Formatter(stringBuilder, Locale.getDefault()) }

    // --- MẤU CHỐT: DỌN DẸP StringBuilder NGAY LẬP TỨC TRƯỚC KHI ĐỊNH DẠNG MỚI ---
    stringBuilder.setLength(0) // Reset StringBuilder về rỗng

    return if (hours > 0) {
        formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
    } else {
        formatter.format("%02d:%02d", minutes, seconds).toString()
    }
}