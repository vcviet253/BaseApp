package com.example.mealplanner.movie.presentation.movie

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.mealplanner.movie.presentation.navigation.MovieAppDestinations
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder


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

                Text(
                    text = movieDetail.metadata.name,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Mô tả: ${movieDetail.metadata.content}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis,
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

                LazyVerticalGrid(
                    modifier = Modifier.weight(1f),
                    columns = GridCells.Fixed(5),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                    itemsIndexed(firstServerEpisodes) { index, episode ->
                        val boxShape = RoundedCornerShape(8.dp) // Định nghĩa hình dạng bo góc

                        Box(
                            modifier = Modifier
                                // .fillMaxWidth() // Bỏ fillMaxWidth() để box nhỏ hơn, hoặc giữ nếu muốn ô lấp đầy cột
                                // .size(60.dp) // Đặt kích thước cố định nếu muốn box vuông nhỏ
                                .weight(1f) // Cho phép box lấp đầy chiều rộng còn lại trong cột
                                .clip(boxShape) // Cắt hiệu ứng ripple theo hình dạng bo góc
                                .background(MaterialTheme.colorScheme.primaryContainer) // Màu nền của box
                                .clickable { // Làm cho Box có thể click được
                                    val route = MovieAppDestinations.createMoviePlayerRoute(episode.link_m3u8)
                                    navController.navigate(route)
                                    android.util.Log.d(
                                        "MovieScreen",
                                        "Episode box clicked: ${episode.name} (Index: $index)"
                                    )
                                }
                                .border( // Thêm viền
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)),
                                    shape = boxShape
                                )
                                .padding(vertical = 8.dp, horizontal = 4.dp), // Padding bên trong box
                            contentAlignment = Alignment.Center // Căn giữa nội dung bên trong Box
                        ) {
                            Text(
                                text = episode.name,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 12.sp, // Điều chỉnh cỡ chữ cho box nhỏ
                                maxLines = 1 // Đảm bảo chữ không xuống dòng quá nhiều
                            )
                        }
                    }
                }
            }
        }
    }
}