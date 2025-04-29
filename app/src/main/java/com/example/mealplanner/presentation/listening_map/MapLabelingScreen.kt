package com.example.mealplanner.presentation.listening_map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapLabelingScreen(viewModel: MapLabelingViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    // State cho BottomSheetScaffold
    val scaffoldState = rememberBottomSheetScaffoldState()

    // Coroutine scope để điều khiển bottom sheet
    val scope = rememberCoroutineScope()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return // Dừng vẽ phần còn lại nếu đang loading
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        // Chiều cao hé lộ của bottom sheet khi thu gọn
        sheetPeekHeight = 90.dp,
        // Nội dung bên trong Bottom Sheet
        sheetContent = {
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)  // Padding từ scaffold (quan trọng)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) // Padding riêng
        {

        }
    }
}

// --- Các Composable thành phần ---
@Composable
fun MapImageView(imageUrl: String?, modifier: Modifier = Modifier) {
    // !! Quan trọng: Đây chỉ là hiển thị ảnh cơ bản.
    // !! Cần thay thế bằng thư viện hoặc giải pháp hỗ trợ Zoom & Pan.
    // Ví dụ thư viện (cần kiểm tra): ZoomableComposeImage, compose-photo-view
    // Hoặc tự triển khai bằng Modifier.pointerInput + Modifier.graphicsLayer
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Map",
            modifier = Modifier.fillMaxSize(), // Để AsyncImage lấp đầy Box cha
            contentScale = ContentScale.Fit // Hiển thị toàn bộ ảnh
        )

        // Ghi chú tạm thời
        Text(
            text = "TODO: Implement Zoom/Pan",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(4.dp)
                .background(Color.Black.copy(alpha = 0.5f))
        )
    }
}

@Composable
fun AudioPlayerControls(
    audioState: AudioPlayerState,
    progress: Float,
    onPlayPauseClick: () -> Unit,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onPlayPauseClick) {
            Icon(
                imageVector = if (audioState == AudioPlayerState.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (audioState == AudioPlayerState.PLAYING) "Pause" else "Play"
            )
        }

        Slider(
            value = progress,
            onValueChange = onSeek, // Xem xét dùng onValueChangeFinished để chỉ seek khi người dùng thả tay
            modifier = Modifier.weight(1f)
            // Có thể thêm steps nếu audio được chia thành các đoạn rõ ràng
        )
    }
}

@Composable
fun AnswerOptionsGrid(
    answerPool: List<String>,
    onAnswerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Chọn đáp án:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp)) // Tăng khoảng cách
        LazyVerticalGrid(
            // Số cột tự điều chỉnh, chiều rộng tối thiểu cho mỗi ô
            columns = GridCells.Adaptive(minSize = 64.dp),
            // Khoảng cách giữa các ô
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            // Thêm padding để lưới không bị sát mép bottom sheet
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(answerPool, key = { it }) { label -> // Dùng label làm key
                Button(
                    onClick = { onAnswerClick(label) },
                    // Kích thước cố định hoặc dùng aspect ratio
                    modifier = Modifier.size(60.dp),
                    // Căn chỉnh text vào giữa nút
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = label, fontSize = 20.sp) // Tăng cỡ chữ
                }
            }
        }
    }
}