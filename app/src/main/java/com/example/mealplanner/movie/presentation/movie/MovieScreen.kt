package com.example.mealplanner.movie.presentation.movie

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.mealplanner.core.common.Resource
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.media3.common.Player
import androidx.media3.ui.AspectRatioFrameLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(UnstableApi::class)
@Composable
fun MovieScreen(navController: NavController, viewModel: MovieViewModel = hiltViewModel()) {
    val movieResource by viewModel.movieDetailState.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val firstServerEpisodes by viewModel.firstServerEpisodes.collectAsState()
    val currentPlayingEpisodeIndex by viewModel.currentEpisodeIndexInFirstServer.collectAsState()
    val currentSpeed by viewModel.playbackSpeed.collectAsState()

    var playerView: PlayerView? by remember { mutableStateOf(null) }

    val lifecycleOwner = LocalLifecycleOwner.current

    var controlsVisible by remember { mutableStateOf(true) }
    var hideControlsJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(playerState) {
        Log.d("MovieScreen", "Player State changed to: $playerState")
    }

    // Hàm để ẩn control sau một khoảng thời gian
    fun scheduleHideControls() {
        hideControlsJob?.cancel()
        hideControlsJob = coroutineScope.launch {
            delay(3500) // Thời gian ẩn controls
            controlsVisible = false
        }
    }

    LaunchedEffect(controlsVisible, playerState) {
        val isEffectivelyPlaying =
            playerState == PlayerState.Playing || playerState == PlayerState.Buffering
        if (controlsVisible || isEffectivelyPlaying) {
            scheduleHideControls()
        } else {
            hideControlsJob?.cancel()
        }
    }

    // Quản lý vòng đời của ExoPlayer
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    // Xử lý việc khởi tạo/khôi phục player khi màn hình quay lại từ background
                    // ViewModel sẽ quản lý việc không khởi tạo lại nếu player đã tồn tại và hợp lệ.
                    if (viewModel.exoPlayer == null && movieResource is Resource.Success) {
                        val episodesForFirstServer =
                            viewModel.firstServerEpisodes.value // Lấy danh sách tập đã được map
                        // Ưu tiên phát tập đang chọn (nếu có và hợp lệ), nếu không thì phát tập đầu tiên
                        val indexToPlay =
                            if (currentPlayingEpisodeIndex != -1 && currentPlayingEpisodeIndex < episodesForFirstServer.size) {
                                currentPlayingEpisodeIndex
                            } else {
                                0 // Mặc định là tập đầu tiên nếu không có index hợp lệ
                            }
                        val episodeToPlay = episodesForFirstServer.getOrNull(indexToPlay)

                        if (episodeToPlay != null) {
                            Log.d(
                                "MovieScreen",
                                "ON_START: Ensuring player is initialized for episode index $indexToPlay: ${episodeToPlay.name}"
                            )
                            viewModel.playEpisodeFromFirstServer(episodeToPlay, indexToPlay)
                        } else if (episodesForFirstServer.isNotEmpty()) {
                            // Fallback nếu indexToPlay không hợp lệ nhưng vẫn có tập
                            Log.d(
                                "MovieScreen",
                                "ON_START: Fallback to first episode as index $indexToPlay was invalid."
                            )
                            viewModel.playEpisodeFromFirstServer(episodesForFirstServer[0], 0)
                        }
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    Log.d(
                        "MovieScreen",
                        "ON_RESUME: ViewModel will handle play if playWhenReady is true."
                    )

                    val player = viewModel.exoPlayer
                    if (player != null) {
                        val isReadyToPlay = player.playbackState == Player.STATE_READY
                        val isPlaying = player.playWhenReady && isReadyToPlay
                        val isPaused = !player.playWhenReady && isReadyToPlay

                        if (isPlaying || isPaused) {
                            viewModel.play()
                        }
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("MovieScreen", "ON_PAUSE: Pausing player via ViewModel.")
                    viewModel.pause()
                }

                Lifecycle.Event.ON_STOP -> {
                    Log.d(
                        "MovieScreen",
                        "ON_STOP: Preparing player for release (saving state) via ViewModel."
                    )
                    viewModel.prepareForRelease()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // ViewModel sẽ tự giải phóng trong onCleared.
        }
    }

    when (val currentMovieResource = movieResource) {
        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Resource.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = currentMovieResource.message ?: "Lỗi không xác định",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        is Resource.Success -> {
            val movieDetail = currentMovieResource.data

            // LaunchedEffect để khởi tạo player với tập đầu tiên khi firstServerEpisodes có dữ liệu
            // và player chưa được tạo. Đây là điểm khởi tạo chính cho lần đầu.
            LaunchedEffect(firstServerEpisodes) {
                // Chỉ khởi tạo nếu player chưa có và có danh sách tập
                if (viewModel.exoPlayer == null && firstServerEpisodes.isNotEmpty()) {
                    // Ưu tiên index hiện tại nếu nó hợp lệ (ví dụ sau khi xoay màn hình và ViewModel được khôi phục)
                    // Nếu không, mặc định là tập đầu tiên.
                    val indexToPlay =
                        if (currentPlayingEpisodeIndex != -1 && currentPlayingEpisodeIndex < firstServerEpisodes.size) {
                            currentPlayingEpisodeIndex
                        } else {
                            0
                        }
                    val episodeToPlay = firstServerEpisodes.getOrNull(indexToPlay)

                    if (episodeToPlay != null) {
                        Log.d(
                            "MovieScreen",
                            "LaunchedEffect(firstServerEpisodes): Initializing player for episode index $indexToPlay: ${episodeToPlay.name}"
                        )
                        viewModel.playEpisodeFromFirstServer(episodeToPlay, indexToPlay)
                    } else if (firstServerEpisodes.isNotEmpty()) {
                        // Fallback nếu indexToPlay không hợp lệ nhưng vẫn có tập
                        Log.d(
                            "MovieScreen",
                            "LaunchedEffect(firstServerEpisodes): Fallback to first episode as index $indexToPlay was invalid."
                        )
                        viewModel.playEpisodeFromFirstServer(firstServerEpisodes[0], 0)
                        playerView?.player =
                            viewModel.exoPlayer // Gán player cho PlayerView sau khi khởi tạo
                    } else {
                        Log.w(
                            "MovieScreen",
                            "LaunchedEffect(firstServerEpisodes): No episode found to play initially."
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = viewModel.exoPlayer // Gán player ban đầu
                            useController = true
                        }.also {
                            playerView = it
                        }
                    },
                    update = { view ->
                        // Cập nhật player nếu instance trong ViewModel thay đổi
                        if (view.player != viewModel.exoPlayer) {
                            view.player = viewModel.exoPlayer
                            Log.d(
                                "MovieScreen",
                                "AndroidView Update: PlayerView updated. Player is ${if (view.player != null) "NOT NULL" else "NULL"}"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp) // Thay aspectRatio bằng height cụ thể
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                Log.d("MovieScreen", "Box Tapped!") // Kiểm tra log khi click
                                controlsVisible = !controlsVisible
                                if (controlsVisible) scheduleHideControls() else hideControlsJob?.cancel()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {

//                    NetflixStylePlayerControls(
//                        // Điều kiện hiển thị: controlsVisible VÀ (player đã sẵn sàng HOẶC đang phát/buffering)
//                        // VÀ không phải đang ở trạng thái lỗi nghiêm trọng (PlayerState.Error).
//                        // Trạng thái Loading ban đầu (fetch URL) sẽ không hiển thị controls.
//                        isVisible = controlsVisible && (playerState == PlayerState.Ready || playerState == PlayerState.Playing || playerState == PlayerState.Paused || playerState == PlayerState.Buffering || playerState == PlayerState.Ended),
//                        playerState = playerState,
//                        currentSpeed = currentSpeed,
//                        onPlayPauseToggle = { viewModel.togglePlayPause() },
//                        onRewind = { viewModel.seekRewind() },
//                        onForward = { viewModel.seekForward() },
//                        onSpeedSelected = { speed -> viewModel.setPlaybackSpeed(speed) },
//                        onAudioSubtitlesClicked = {
//                            Log.d("MovieScreen", "Audio/Subtitles button clicked")
//                        },
//                        onNextEpisodeClicked = { viewModel.playNextEpisodeFromFirstServerIfAvailable() },
//                        hasNextEpisode = currentPlayingEpisodeIndex >= 0 && currentPlayingEpisodeIndex < firstServerEpisodes.size - 1
//                    )

                    // Lớp phủ cho trạng thái Loading (fetch URL) / Buffering / Error của Player
                    when (val currentPlayerState = playerState) {
                        is PlayerState.Loading -> { // Chỉ Loading khi fetch URL hoặc khởi tạo player ban đầu
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Log.d(
                                    "MovieScreen",
                                    "UI State: PlayerState.Loading (Initial URL/Player Load)"
                                )
                            }
                        }

                        is PlayerState.Buffering -> {
                            // Hiển thị buffering ngay cả khi controls hiện, vì nó quan trọng
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                                Log.d("MovieScreen", "UI State: PlayerState.Buffering")
                            }
                        }

                        is PlayerState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.8f))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.ErrorOutline,
                                        contentDescription = "Biểu tượng lỗi",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Text(
                                        text = "Lỗi Video",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(top = 12.dp)
                                    )
                                    Text(
                                        text = currentPlayerState.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                    Log.e(
                                        "MovieScreen",
                                        "UI State: PlayerState.Error - ${currentPlayerState.message}"
                                    )
                                }
                            }
                        }

                        else -> {
                            Log.d(
                                "MovieScreen",
                                "UI State: $currentPlayerState (No specific overlay)"
                            )
                        }
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Text(
                            text = movieDetail.metadata.name,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                        Text(
                            text = "Mô tả: ${movieDetail.metadata.content}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        movieDetail.episodes!!.firstOrNull()?.let { firstServer ->
                            Text(
                                text = "Server: ${firstServer.serverName}",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                            )
                        }
                        Text(
                            text = "Danh sách tập:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 12.dp,
                                bottom = 4.dp
                            )
                        )
                    }
                    itemsIndexed(firstServerEpisodes) { index, episode ->
                        val isCurrentlyPlayingThisEpisode = index == currentPlayingEpisodeIndex &&
                                (playerState == PlayerState.Playing ||
                                        playerState == PlayerState.Buffering ||
                                        playerState == PlayerState.Ready ||
                                        playerState == PlayerState.Paused)

                        Button(
                            onClick = {
                                Log.d(
                                    "MovieScreen",
                                    "Episode button clicked: ${episode.name} (Index: $index)"
                                )
                                // Chỉ phát lại nếu là tập khác, hoặc có lỗi, hoặc đã kết thúc/idle
                                if (index != currentPlayingEpisodeIndex || playerState is PlayerState.Error || playerState == PlayerState.Ended || playerState == PlayerState.Idle) {
                                    viewModel.playEpisodeFromFirstServer(episode, index)
                                }
                                controlsVisible = true // Hiển thị controls khi người dùng tương tác
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCurrentlyPlayingThisEpisode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                episode.name,
                                color = if (isCurrentlyPlayingThisEpisode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NetflixStylePlayerControls(
    isVisible: Boolean,
    playerState: PlayerState,
    currentSpeed: Float,
    onPlayPauseToggle: () -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onAudioSubtitlesClicked: () -> Unit,
    onNextEpisodeClicked: () -> Unit,
    hasNextEpisode: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(200)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onRewind,
                    modifier = Modifier.size(60.dp),
                    enabled = playerState != PlayerState.Idle && playerState !is PlayerState.Loading && playerState !is PlayerState.Error
                ) {
                    Icon(
                        Icons.Filled.Replay10,
                        contentDescription = "Tua lại 10 giây",
                        tint = if (playerState != PlayerState.Idle && playerState !is PlayerState.Loading && playerState !is PlayerState.Error) Color.White else Color.Gray,
                        modifier = Modifier.fillMaxSize(0.85f)
                    )
                }
                IconButton(
                    onClick = onPlayPauseToggle,
                    modifier = Modifier.size(80.dp),
                    enabled = playerState !is PlayerState.Loading && playerState !is PlayerState.Error
                ) {
                    val icon = when (playerState) {
                        PlayerState.Playing, PlayerState.Buffering -> Icons.Filled.PauseCircleFilled // Coi Buffering như đang chuẩn bị phát
                        is PlayerState.Error -> Icons.Filled.ReportProblem
                        else -> Icons.Filled.PlayCircleFilled
                    }
                    val contentDesc = when (playerState) {
                        PlayerState.Playing, PlayerState.Buffering -> "Tạm dừng"
                        is PlayerState.Error -> "Lỗi"
                        else -> "Phát"
                    }
                    Icon(
                        icon,
                        contentDescription = contentDesc,
                        tint = if (playerState !is PlayerState.Loading && playerState !is PlayerState.Error) Color.White else Color.Gray,
                        modifier = Modifier.fillMaxSize(0.9f)
                    )
                }
                IconButton(
                    onClick = onForward,
                    modifier = Modifier.size(60.dp),
                    enabled = playerState != PlayerState.Idle && playerState !is PlayerState.Loading && playerState !is PlayerState.Error
                ) {
                    Icon(
                        Icons.Filled.Forward10,
                        contentDescription = "Tua tới 10 giây",
                        tint = if (playerState != PlayerState.Idle && playerState !is PlayerState.Loading && playerState !is PlayerState.Error) Color.White else Color.Gray,
                        modifier = Modifier.fillMaxSize(0.85f)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var showSpeedMenu by remember { mutableStateOf(false) }
                val speedButtonEnabled =
                    playerState != PlayerState.Idle && playerState !is PlayerState.Loading && playerState !is PlayerState.Error
                Box {
                    TextButton(
                        onClick = { if (speedButtonEnabled) showSpeedMenu = true },
                        enabled = speedButtonEnabled
                    ) {
                        Icon(
                            Icons.Filled.Speed,
                            contentDescription = "Tốc độ",
                            tint = if (speedButtonEnabled) Color.White else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (currentSpeed == 1.0f) "Tốc độ" else "${currentSpeed}x",
                            color = if (speedButtonEnabled) Color.White else Color.Gray,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    DropdownMenu(
                        expanded = showSpeedMenu,
                        onDismissRequest = { showSpeedMenu = false }
                    ) {
                        listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                            DropdownMenuItem(
                                text = { Text("${speed}x") },
                                onClick = {
                                    onSpeedSelected(speed)
                                    showSpeedMenu = false
                                }
                            )
                        }
                    }
                }

                val settingsButtonEnabled =
                    playerState != PlayerState.Idle && playerState !is PlayerState.Loading && playerState !is PlayerState.Error
                IconButton(
                    onClick = onAudioSubtitlesClicked,
                    modifier = Modifier.size(48.dp),
                    enabled = settingsButtonEnabled
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Âm thanh/Phụ đề",
                        tint = if (settingsButtonEnabled) Color.White else Color.Gray
                    )
                }

                val nextEpisodeButtonEnabled =
                    hasNextEpisode && playerState != PlayerState.Idle && playerState !is PlayerState.Loading && playerState !is PlayerState.Error
                if (hasNextEpisode) { // Luôn giữ cấu trúc, chỉ thay đổi enabled state
                    IconButton(
                        onClick = onNextEpisodeClicked,
                        modifier = Modifier.size(48.dp),
                        enabled = nextEpisodeButtonEnabled
                    ) {
                        Icon(
                            Icons.Filled.SkipNext,
                            contentDescription = "Tập tiếp theo",
                            tint = if (nextEpisodeButtonEnabled) Color.White else Color.Gray
                        )
                    }
                } else {
                    Spacer(Modifier.width(48.dp)) // Giữ chỗ để layout không bị xô lệch
                }
            }
        }
    }
}
