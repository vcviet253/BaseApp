package com.example.mealplanner.presentation.maplabeling

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.times
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mealplanner.core.audio.AudioPlayerState
import com.example.mealplanner.domain.maplabeling.model.Question
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val TAG = "MapLabelingScreen"

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
            AnswerOptionsGrid(
                questionList = state.questionList,   //Truyen danh sach cac cau hoi cua bai tap hien tai
                // 1. Truyền danh sách đáp án từ state
                answerPool = state.answerPool,
                // 2. Truyền hàm callback khi đáp án được click
                // Dùng tham chiếu hàm viewModel::onAnswerSelected
                // Vì hàm onAnswerSelected(label: String) trong ViewModel
                // có signature khớp với (String) -> Unit mà onAnswerClick yêu cầu.
                onAnswerClick = viewModel::onAnswerSelected

                // modifier = Modifier... // Có thể thêm Modifier nếu cần
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding) // Padding từ scaffold (quan trọng)
                .padding(horizontal = 16.dp, vertical = 8.dp) // Padding riêng
        ) {
            // --- Tiêu đề Test ---
            Text(
                text = state.testTitle,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // --- Khu vực hiển thị Ảnh Map ---
            MapImageView(
                imageUrl = "https://th.bing.com/th/id/R.201f06fa3bf4d8e07d8f07951ec841a8?rik=DDq25UQfStmz1g&riu=http%3a%2f%2fst.ebomb.edu.vn%2fsrc%2fielts-fighter%2f2019%2f05%2fbai-hoc-listening%2fbai-tap-map-labelling-1.jpeg&ehk=JkgA5Km53F2Rm61DmORj8rpIOQBSvcJ%2bJXO8iN4DpsE%3d&risl=&pid=ImgRaw&r=0",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Cho phép ảnh chiếm không gian còn lại theo chiều dọc
                    .background(Color.Gray.copy(alpha = 0.1f)) // Nền tạm thời
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Hiển thị câu hỏi hiện tại ---
            Text(
                text = "Câu ${state.currentQuestionNumber} / ${state.totalQuestions}: ${state.selectedAnswerForCurrentQuestion ?: "___"}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // --- Điều khiển Audio Player ---
            AudioPlayerControls(
                audioState = state.audioState,
                progress = state.audioProgress,
                onPlayPauseClick = viewModel::togglePlayPause,
                onSeek = viewModel::seekAudio, // Có thể cần onValueChangeFinished
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp)) // Khoảng trống trước khi chạm đáy

            // (Tùy chọn) Nút để mở rộng sheet thủ công nếu cần
            // Button(
            //    onClick = { scope.launch { scaffoldState.bottomSheetState.expand() } },
            //    modifier = Modifier.align(Alignment.CenterHorizontally)
            // ) { Text("Hiển thị đáp án") }
        }
    }
}

// --- Các Composable thành phần ---
@Composable
fun MapImageView(imageUrl: String?, modifier: Modifier = Modifier) {
    // Use Animatable for smooth transitions on scale and offset
    val scale = remember { Animatable(1f) }
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    // val rotation = remember { Animatable(0f) } // Optional rotation

    // Coroutine scope for launching animations
    val scope = rememberCoroutineScope()

    // To store the size of the Box composable
    var composableSize by remember { mutableStateOf(IntSize.Zero) }

    // Remember previous scale for comparison in double tap
    var previousScale by remember { mutableStateOf(1f) }

    //Anh co the zoom/pan
    Box(modifier = modifier
        .clipToBounds()
        .onSizeChanged { composableSize = it } // Get the actual size of the Box
        .pointerInput(Unit) { // Use a single pointerInput for multiple gestures
            detectTransformGestures(
                onGesture = { centroid, pan, zoom, gestureRotation ->
                    scope.launch { // Use scope for snapping which suspends
                        val oldScale = scale.value
                        val newScale = (scale.value * zoom).coerceIn(1f, 5f) // Clamp scale

                        // Calculate offset adjustment for zoom centering
                        val scaleChange = newScale / oldScale
                        val centroidOffset = offset.value + centroid * (oldScale - 1f)
                        val newCentroidOffset = centroidOffset * scaleChange
                        val panApplied = pan * newScale

                        var tempOffset = newCentroidOffset - centroid * (newScale - 1f) + panApplied

                        // Calculate scaled image dimensions for clamping
                        val layoutWidth = composableSize.width * newScale
                        val layoutHeight = composableSize.height * newScale

                        // Clamp the offset within boundaries
                        tempOffset = calculateClampedOffset(
                            tempOffset,
                            newScale,
                            composableSize,
                            layoutWidth,
                            layoutHeight
                        )


                        // Snap to new values during transform gesture for immediate feedback
                        scale.snapTo(newScale)
                        offset.snapTo(tempOffset)
                        // rotation.snapTo(rotation.value + gestureRotation) // Optional
                    }
                }
            )
        }
        .pointerInput(Unit) { // Separate pointerInput for taps (usually ok, can combine if needed)
            detectTapGestures(
                onDoubleTap = { tapOffset ->
                    scope.launch {
                        Log.d("MapDebug", "--- Double Tap START ---") // Log bắt đầu
                        Log.d("MapDebug", "Composable Size: $composableSize") // Log size

                        // Thêm kiểm tra composableSize
                        if (composableSize == IntSize.Zero) {
                            Log.w(
                                "MapDebug",
                                "!!! Double Tap RETURNED: Composable size is zero"
                            ) // Log nếu return sớm
                            return@launch
                        }

                        val currentScale = scale.value
                        val targetScale = if (abs(currentScale - 1f) < 0.1f) 3f else 1f
                        Log.d(
                            "MapDebug",
                            "Current Scale: $currentScale, Target Scale: $targetScale"
                        ) // Log scale

                        // Check for near-zero currentScale (prevent division by zero)
                        if (abs(currentScale) < 0.0001f) {
                            Log.e(
                                "MapDebug",
                                "!!! Double Tap RETURNED: Current scale is near zero"
                            )
                            return@launch
                        }
                        val scaleRatio = targetScale / currentScale
                        Log.d("MapDebug", "Scale Ratio: $scaleRatio") // Log ratio

                        val currentOffset = offset.value // Lấy offset hiện tại
                        Log.d(
                            "MapDebug",
                            "Tap Offset: $tapOffset, Current Offset: $currentOffset"
                        ) // Log offsets

                        // Dòng tính toán offset đã sửa
                        var targetOffset = tapOffset - (tapOffset - currentOffset) * scaleRatio
                        Log.d(
                            "MapDebug",
                            "Target Offset (Before Clamp): $targetOffset"
                        ) // Log offset trước clamp

                        val targetLayoutWidth = composableSize.width * targetScale
                        val targetLayoutHeight = composableSize.height * targetScale

                        // Clamp the target offset
                        targetOffset = calculateClampedOffset(
                            targetOffset,
                            targetScale,
                            composableSize,
                            targetLayoutWidth,
                            targetLayoutHeight
                        )
                        Log.d(
                            "MapDebug",
                            "Target Offset (After Clamp): $targetOffset"
                        ) // Log offset sau clamp

                        // Check if target is same as current (within tolerance)
                        val scaleDifference = abs(targetScale - currentScale)
                        val offsetDifferenceSquared =
                            (targetOffset - currentOffset).getDistanceSquared() // Use squared distance for efficiency
                        val scaleClose = scaleDifference < 0.01f
                        val offsetClose =
                            offsetDifferenceSquared < 0.1f // Adjust tolerance if needed

                        Log.d(
                            "MapDebug",
                            "Scale Diff: $scaleDifference (Close: $scaleClose), Offset Diff Sq: $offsetDifferenceSquared (Close: $offsetClose)"
                        )

                        if (scaleClose && offsetClose) {
                            Log.w(
                                "MapDebug",
                                "!!! Double Tap IGNORED: Target scale/offset are same as current."
                            )
                            return@launch // Thoát nếu không có gì thay đổi đáng kể
                        }


                        // Animate scale and offset...
                        Log.d(
                            "MapDebug",
                            ">>> Launching animations..."
                        ) // Log trước khi launch animation
                        launch {
                            scale.animateTo(targetScale, animationSpec = tween(300))
                        }
                        launch {
                            offset.animateTo(targetOffset, animationSpec = tween(300))
                        }
                        Log.d(
                            "MapDebug",
                            "<<< Animations launched."
                        ) // Log sau khi launch animation
                        Log.d("MapDebug", "--- Double Tap END ---") // Log kết thúc
                    }
                }
                // Add onTap, onLongPress here if needed
            )
        }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Map",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer( // Apply transformations
                    scaleX = scale.value,
                    scaleY = scale.value,
                    translationX = offset.value.x,
                    translationY = offset.value.y
                    // rotationZ = rotation.value // Optional
                ), // Để AsyncImage lấp đầy Box cha
            contentScale = ContentScale.Fit // Hiển thị toàn bộ ảnh
        )

        // Optional: Display current zoom/pan state for debugging
        Text(
            // Access the .value property of Animatable scale and offset
            text = "Zoom: ${"%.2f".format(scale.value)}x\nPan: (${"%.0f".format(offset.value.x)}, ${
                "%.0f".format(
                    offset.value.y
                )
            })",
            color = Color.White, // Changed color for better visibility on dark overlay
            modifier = Modifier
                .align(Alignment.BottomStart) // Aligned to bottom-start
                .padding(8.dp) // Padding outside the background
                .background(
                    Color.Black.copy(alpha = 0.6f), // Semi-transparent black background
                    shape = MaterialTheme.shapes.small // Optional: rounded corners
                )
                .padding(horizontal = 6.dp, vertical = 4.dp) // Padding inside the background
        )

        // Ghi chú tạm thời
//        Text(
//            text = "TODO: Implement Zoom/Pan",
//            color = MaterialTheme.colorScheme.error,
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(4.dp)
//                .background(Color.Black.copy(alpha = 0.5f))
//        )
    }
}

// --- Helper function to calculate and clamp offset ---
private fun calculateClampedOffset(
    currentOffset: Offset,
    newScale: Float,
    imageSize: IntSize, // The size of the composable Box
    layoutWidth: Float, // Actual image width after scaling
    layoutHeight: Float // Actual image height after scaling
): Offset {
    val maxX = (layoutWidth - imageSize.width).coerceAtLeast(0f) / 2f
    val maxY = (layoutHeight - imageSize.height).coerceAtLeast(0f) / 2f

    return Offset(
        x = currentOffset.x.coerceIn(-maxX, maxX),
        y = currentOffset.y.coerceIn(-maxY, maxY)
    )
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
    questionList: List<Question>,
    answerPool: List<String>,
    onAnswerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        questionList.forEach() { question ->
            // Hiển thị câu hỏi
            Text(
                text = question.prompt ?: "Câu hỏi không có nội dung",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Log.d(TAG, questionList.size.toString())
        }

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
