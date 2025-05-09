package com.example.mealplanner.movie.presentation.playerscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController // Thêm dependency nếu chưa có


@Composable
fun MoviePlayerScreen(
    navController: NavController,
    viewModel: MoviePlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val playerInstance = viewModel.playerInstance // Lấy player từ ViewModel
    val isPlayerReady by viewModel.isPlayerReady.collectAsState()
    val movieUrl by viewModel.movieUrl.collectAsState() // Có thể dùng để hiển thị title hoặc debug

    // QUAN TRỌNG: State để kiểm soát việc hiển thị AndroidView
    var showPlayerView by remember { mutableStateOf(true) } // Ban đầu hiển thị

    // --- Fullscreen Handling ---
    val systemUiController = rememberSystemUiController()
    val originalSystemBarsVisible = remember { systemUiController.isSystemBarsVisible }

    BackHandler {
        showPlayerView = false // Ẩn player ngay lập tức
        navController.popBackStack()
    }

    SideEffect { // Sử dụng SideEffect để thay đổi System UI một cách an toàn
        systemUiController.isSystemBarsVisible = false // Ẩn status bar và navigation bar
    }

    // --- Player Lifecycle Management with Composable Lifecycle ---
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, playerInstance) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    playerInstance?.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    // playerInstance?.play() // Cân nhắc việc tự động play khi resume
                }
                // ViewModel's onCleared sẽ lo việc release
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // Khôi phục lại System UI khi màn hình bị dispose
            systemUiController.isSystemBarsVisible = originalSystemBarsVisible
            // QUAN TRỌNG: Đặt trạng thái này thành false ngay khi Composable bắt đầu dispose
            // để AndroidView biến mất khỏi Composition ngay lập tức về mặt logic.
            showPlayerView = false
            // QUAN TRỌNG: Dừng phát và xóa nội dung của player ngay lập tức
            // Điều này giúp loại bỏ frame cuối cùng của video khi màn hình được loại bỏ.
            playerInstance?.let {
                it.stop() // Dừng phát lại
                it.clearMediaItems() // Xóa các item media khỏi player
                // Không gọi it.release() ở đây vì ViewModel sẽ lo việc đó trong onCleared()
                // và việc release một player có thể gây ra lỗi nếu nó vẫn đang được sử dụng bởi PlayerView
                // hoặc nếu playerInstance được giữ lại một cách không mong muốn.
            }
            // Player sẽ được release một cách an toàn trong onCleared của ViewModel
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Nền đen cho player
    ) {
        if (showPlayerView && playerInstance != null && movieUrl.isNotBlank()) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = playerInstance
                        useController = true // Sử dụng controller mặc định của ExoPlayer
                        // Các tùy chỉnh khác cho PlayerView nếu cần
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view -> // Được gọi khi playerInstance thay đổi (dù ViewModel nên giữ nó ổn định)
                    view.player = playerInstance
                }
            )
        } else if (movieUrl.isBlank()) {
            Text(
                text = "Không có URL phim hợp lệ.",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // Hiển thị loading hoặc thông báo lỗi nếu player chưa sẵn sàng hoặc có vấn đề
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // Nút Back (tùy chọn, vì người dùng có thể dùng nút back hệ thống)
        IconButton(
            onClick = {
                showPlayerView = false // Ẩn PlayerView trước khi quay lại
                navController.popBackStack()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(all = 8.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = Color.White
            )
        }
    }
}