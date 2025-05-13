package com.example.mealplanner.movie.presentation.movie.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons // Import Icons
import androidx.compose.material.icons.filled.Favorite // Import Favorite icon
import androidx.compose.material.icons.filled.FavoriteBorder // Import FavoriteBorder icon
import androidx.compose.material.icons.filled.PlayArrow // Import PlayArrow icon
import androidx.compose.material3.Icon // Import Icon Composable
import androidx.compose.material3.IconButton // Import IconButton Composable
import androidx.compose.material3.MaterialTheme // Import MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable // Composable annotation
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment // Import Alignment
import androidx.compose.ui.Modifier // Import Modifier
import androidx.compose.ui.draw.clip // Import clip modifier
import androidx.compose.ui.graphics.Color // Import Color (e.g., for icon tint)
import androidx.compose.ui.layout.ContentScale // Import ContentScale
import androidx.compose.ui.res.painterResource // Import painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp // Import dp units
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage // Import AsyncImage
import com.example.mealplanner.R
import com.example.mealplanner.movie.domain.model.Movie
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

// Import YouTube Player API components (SAU KHI THÊM DEPENDENCY)
// Đây là tên class ví dụ, có thể khác tùy phiên bản thư viện bạn dùng
// import com.google.android.youtube.player.YouTubePlayerView
// import com.google.android.youtube.player.YouTubePlayer
// import com.google.android.youtube.player.YouTubeInitializationResult
// import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener
// import com.google.android.youtube.player.YouTubePlayer.PlayerStyle

// Import API Key (ĐẢM BẢO BẢO MẬT API KEY!)
// import com.example.mealplanner.BuildConfig // Ví dụ lấy từ BuildConfig

@Composable
fun MovieDetailHeader(
    movieDetail: Movie, // <-- Nhận đối tượng Movie
    isFavorite: Boolean, // <-- Nhận trạng thái yêu thích
    onFavoriteClick: (Movie) -> Unit, // <-- Callback cho favorite icon
    // Bỏ onPlayTrailerClick ở đây, logic phát sẽ nằm trong Header
    modifier: Modifier = Modifier
) {
    // State để điều khiển liệu có đang phát trailer (hiển thị player) hay không
    var isPlayingTrailer by remember { mutableStateOf(false) }

    // Lấy URL trailer từ metadata
    val trailerUrl = movieDetail.metadata.trailer_url
    // TODO: Cần hàm helper để phân tích URL YouTube và lấy ra Video ID
    // Ví dụ: "https://www.youtube.com/watch?v=dQw4w9WgXcQ" -> "dQw4w9WgXcQ"
    val videoId = parseYouTubeVideoId(trailerUrl) // <-- Cần implement hàm này

    // Sử dụng Box để xếp chồng ảnh nền/player và các icon/button lên nhau
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp) // Chiều cao cố định cho khu vực media
            .clip(MaterialTheme.shapes.medium) // Bo góc
    ) {
        // --- Hiển thị Ẩnh/Nút Play HOẶC YouTube Player ---
        // Sử dụng điều kiện để chuyển đổi giữa 2 trạng thái hiển thị
        if (isPlayingTrailer && videoId != null) {
            // --- TRẠNG THÁI: Đang phát Trailer (Hiển thị YouTube Player) ---
            // Sử dụng AndroidView để nhúng View truyền thống vào Compose
            val lifecycleOwner = LocalLifecycleOwner.current // Lấy LifecycleOwner

            AndroidView(
                factory = { context ->
                    // Tạo instance của YouTubePlayerView
                    // LƯU Ý: Việc khởi tạo và quản lý playerView phức tạp hơn nhiều
                    // nếu không dùng thư viện wrapper.
                    // API chuẩn yêu cầu Activity kế thừa YouTubeBaseActivity
                    // hoặc sử dụng Fragment.
                    // Code dưới đây là KHÁI NIỆM cho API chuẩn, cần điều chỉnh.

                    YouTubePlayerView(context).apply {
                        // Đặt các listener và khởi tạo player ở đây
                        // Việc initialize này thường là suspend hoặc async
                        // Nó yêu cầu API Key
                        lifecycleOwner.lifecycle.addObserver(this) // 'this' ở đây là YouTubePlayerView

                        // Thêm Listener để xử lý các sự kiện của player
                        addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                            // Phương thức được gọi khi player đã sẵn sàng
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                // Player đã sẵn sàng, tải video bằng Video ID
                                youTubePlayer.loadVideo(
                                    videoId,
                                    0f
                                ) // Tải video, bắt đầu từ giây thứ 0
                                // Optional: youTubePlayer.play() // Bắt đầu phát ngay lập tức
                            }

                            // Optional: Xử lý các sự kiện khác (ví dụ: khi video kết thúc, lỗi)
                            override fun onStateChange(
                                youTubePlayer: YouTubePlayer,
                                state: PlayerConstants.PlayerState
                            ) {
                                // Nếu video kết thúc, quay về hiển thị thumbnail
                                if (
                                    state == PlayerConstants.PlayerState.ENDED) {
                                    // Nếu player pause, end hoặc stop, quay về hiển thị thumbnail
                                    isPlayingTrailer = false // <-- Cập nhật state
                                }
                                // TODO: Xử lý lỗi phát video (ví dụ: state == PlayerState.PLAYBACK_ERROR)
                            }

                            override fun onError(
                                youTubePlayer: YouTubePlayer,
                                error: PlayerConstants.PlayerError
                            ) {
                                // Xử lý lỗi player
                                Log.e("YouTubePlayer", "Player error: $error")
                                isPlayingTrailer = false // Quay về hiển thị thumbnail
                                // TODO: Hiển thị thông báo lỗi cho người dùng
                            }
                        })
                    }
                },
                modifier = Modifier.fillMaxSize(), // Player View lấp đầy Box
                // Hàm 'update' được gọi khi Composable recompose
                // KHÔNG khởi tạo player ở đây. Dùng để cập nhật thuộc tính view nếu cần.
                // Ví dụ: playerView.visibility = View.VISIBLE
                update = { playerView ->
                    // Có thể làm gì đó với playerView ở đây nếu cần
                }
            )
        } else {
            // --- TRẠNG THÁI: Chưa phát (Hiển thị Thumbnail và Nút Play) ---
            AsyncImage(
                model = movieDetail.metadata.poster_url, // Hoặc thumb_url tùy bạn muốn hiển thị
                contentDescription = movieDetail.metadata.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(), // Lấp đầy Box
                placeholder = painterResource(id = R.drawable.ic_launcher_background), // Ảnh placeholder
                error = painterResource(id = R.drawable.ic_launcher_background) // Ảnh lỗi
            )

            // --- Nút Yêu thích Overlay (hiển thị ở góc trên bên phải) ---
            IconButton(
                onClick = { onFavoriteClick(movieDetail) }, // Gọi callback favorite
                modifier = Modifier
                    .align(Alignment.TopEnd) // Vị trí góc trên bên phải
                    .padding(8.dp) // Padding từ mép
            ) {
                Icon(
                    // Icon trái tim dựa trên trạng thái yêu thích
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else Color.White // Màu đỏ/trắng
                )
            }
            // --- Kết thúc Nút Yêu thích ---

            // Nút Play Overlay (ở giữa)
            // Chỉ hiển thị nút Play nếu trailerUrl hợp lệ và có Video ID
            if (videoId != null) {
                IconButton(
                    onClick = {
                        // Khi bấm nút Play:
                        // Set state để chuyển sang hiển thị player và bắt đầu tải video
                        isPlayingTrailer = true // <-- Set state này
                        // Logic tạo player và tải video sẽ chạy ở nhánh 'if (isPlayingTrailer && videoId != null)'
                    },
                    modifier = Modifier.align(Alignment.Center) // Căn giữa nút Play
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Trailer",
                        tint = Color.White, // Màu trắng để dễ thấy trên ảnh
                        modifier = Modifier.size(56.dp) // Kích thước lớn hơn cho nút Play
                    )
                }
            } else {
                // Optional: Hiển thị thông báo "Không có trailer" ở giữa nếu không tìm thấy Video ID
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No Trailer Available",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        // --- Kết thúc chuyển đổi hiển thị Thumbnail/Player ---
    }
}

// TODO: Implement hàm helper để phân tích URL YouTube và lấy Video ID
// Cần xử lý nhiều định dạng URL khác nhau (watch?v=, youtu.be/, embed/, etc.)
fun parseYouTubeVideoId(youtubeUrl: String?): String? {
    if (youtubeUrl == null) return null

    // Regex tìm Video ID trong các định dạng URL phổ biến
    val videoIdRegex =
        "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|youtu.be%2F)[^#&?]*".toRegex()

    return videoIdRegex.find(youtubeUrl)?.value
}
