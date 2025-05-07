package com.example.mealplanner.movie.presentation.movie

import android.util.Log
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.mealplanner.core.common.Resource

@Composable
fun MovieScreen(navController: NavController, viewModel: MovieViewModel = hiltViewModel()) {
    val movie by viewModel.movieDetailState.collectAsState()
    val playerState by viewModel.playerState.collectAsState() // Có thể dùng để hiển thị UI loading/error

    val lifecycleOwner = LocalLifecycleOwner.current
    var playerView: PlayerView? by remember { mutableStateOf(null) }

    // Khởi tạo player khi có URL và player chưa được khởi tạo
    LaunchedEffect(movie) {
        movie.let { url ->

        }
    }

    when (movie) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text((movie as Resource.Error).message)
        is Resource.Success -> {
            val movieDetail = (movie as Resource.Success).data
            if (viewModel.exoPlayer == null) {
                viewModel.initializePlayer(movieDetail.episodes?.first()?.serverData?.first()?.link_m3u8!!)
                playerView?.player =
                    viewModel.exoPlayer // Gán player cho PlayerView sau khi khởi tạo
            }

            // Quản lý vòng đời của ExoPlayer cùng với vòng đời của Composable/LifecycleOwner
            DisposableEffect(Unit) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_PAUSE -> {
                            Log.d("VideoPlayerScreen", "Lifecycle ON_PAUSE: Pausing player.")
                            viewModel.pause() // Hoặc bạn có thể gọi viewModel.savePlayerState() và release
                        }

                        Lifecycle.Event.ON_RESUME -> {
                            Log.d(
                                "VideoPlayerScreen",
                                "Lifecycle ON_RESUME: Resuming player if URL available."
                            )
                            // Player sẽ tự động play nếu playWhenReady là true và đã được prepare
                            // Nếu bạn release player ở ON_STOP/ON_PAUSE, bạn cần khởi tạo lại ở đây

                            if (viewModel.exoPlayer == null) {
                                viewModel.initializePlayer(movieDetail.episodes?.first()?.serverData?.first()?.link_m3u8!!)
                                playerView?.player =
                                    viewModel.exoPlayer // Gán player cho PlayerView sau khi khởi tạo
                            }
                            viewModel.play() // Yêu cầu phát lại

                        }
                        Lifecycle.Event.ON_DESTROY -> { // Hoặc ON_STOP tùy theo logic bạn muốn
                            // ViewModel sẽ tự giải phóng trong onCleared, nhưng nếu bạn muốn
                            // giải phóng sớm hơn khi màn hình không còn hiển thị (ví dụ trong NavHost)
                            // thì có thể cân nhắc. Tuy nhiên, với @HiltViewModel, onCleared là đủ.
                            Log.d("VideoPlayerScreen", "Lifecycle ON_DESTROY.")
                        }

                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    Log.d("VideoPlayerScreen", "DisposableEffect onDispose: Removing observer.")
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    // Không release player ở đây nếu ViewModel vẫn còn sống (ví dụ khi xoay màn hình)
                    // ViewModel sẽ xử lý việc release trong onCleared()
                    // Tuy nhiên, nếu bạn muốn player dừng ngay khi Composable bị dispose (ví dụ navigate đi chỗ khác)
                    // và ViewModel không bị clear ngay (ví dụ ViewModel của Activity), thì có thể cân nhắc:
                    // viewModel.pause() // Hoặc viewModel.releasePlayer() nếu muốn giải phóng hoàn toàn
                }
            }

            // Sử dụng AndroidView để nhúng PlayerView của ExoPlayer vào Composable
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = viewModel.exoPlayer // Gán player ban đầu (có thể là null)
                        // Bạn có thể tùy chỉnh PlayerView ở đây
                        // useController = true // Hiển thị các nút điều khiển mặc định
                    }.also {
                        playerView = it // Lưu tham chiếu đến PlayerView
                    }
                },
                update = { view ->
                    // Cập nhật player cho PlayerView nếu nó thay đổi (ví dụ sau khi khởi tạo)
                    if (view.player != viewModel.exoPlayer) {
                        view.player = viewModel.exoPlayer
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f) // Giữ tỷ lệ khung hình video
            )
        }
    }


}

@Composable
fun MovieThumbnailCard(title: String) {
    Text(title)
}