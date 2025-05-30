package com.example.mealplanner.presentation.diary

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlin.math.abs


@Composable
fun DiaryScreen(viewModel: DiaryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Giả lập ảnh base64 đã chọn
        ImagePickerButton(viewModel)

        Spacer(Modifier.height(12.dp))

        // Hiển thị các ảnh đã chọn
        if (state.images.isNotEmpty()) {
            Text("Ảnh đã chọn:", style = MaterialTheme.typography.titleMedium)

//            state.images.forEach { base64 ->
//                val imageBitmap = remember(base64) {
//                    try {
//                        val imageBytes = Base64.decode(base64, Base64.DEFAULT)
//                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//                            ?.asImageBitmap()
//                    } catch (e: Exception) {
//                        null
//                    }
//                }
//
//                imageBitmap?.let {
//                    Image(
//                        bitmap = it,
//                        contentDescription = null,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(200.dp)
//                            .padding(vertical = 8.dp)
//                    )
//                }
//            }

            HorizontalPagerWithDepthTransition(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                viewModel = viewModel
            )
        } else {
            Text("Chưa có ảnh nào được chọn.")
        }

        Spacer(Modifier.height(12.dp))

        // Nút tạo nhật ký
        Button(
            onClick = { viewModel.generateDiary() },
            enabled = !state.isLoading && state.images.isNotEmpty()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("📝 Tạo Nhật Ký")
        }

        Spacer(Modifier.height(16.dp))

        // Kết quả
        if (!state.diaryText.isNullOrBlank()) {
            Text("📖 Nhật ký:", style = MaterialTheme.typography.titleMedium)
            Text(state.diaryText ?: "", modifier = Modifier.padding(top = 8.dp))
        }

        // Lỗi
        state.error?.let {
            Text("❗ Lỗi: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun ImagePickerButton(viewModel: DiaryViewModel) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val base64List = uris.mapNotNull { uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                // Encode base64 chuẩn dạng bytes (no wrap, no line breaks)
                bytes?.let { Base64.encodeToString(it, Base64.NO_WRAP) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        viewModel.onImagesSelected(base64List)
    }

    Button(onClick = {
        launcher.launch("image/*")
    }) {
        Text("Chọn ảnh")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerWithDepthTransition(modifier: Modifier = Modifier, viewModel: DiaryViewModel) {
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { state.images.size })
    HorizontalPager(
        modifier = modifier.fillMaxSize(),
        state = pagerState,
        pageSpacing = 0.dp
    ) { page ->
        Box(
            Modifier
                .pagerDepthTransition(page, pagerState)
                .fillMaxSize()
        ) {
            val base64 = state.images[page]

            val imageBitmap = remember(base64) {
                base64?.let {
                    try {
                        val imageBytes = Base64.decode(it, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            ?.asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            imageBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerDepthTransition(page: Int, pagerState: PagerState): Modifier = this.then(
    graphicsLayer {
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val scale = 1f - (0.25f * abs(pageOffset))
        scaleX = scale
        scaleY = scale
        alpha = 1f - abs(pageOffset)
    }
)