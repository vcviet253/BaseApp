@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.example.mealplanner.movie.presentation.movie

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.mealplanner.core.common.Resource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mealplanner.R
import com.example.mealplanner.movie.presentation.movie.components.CategoryChipsList
import com.example.mealplanner.movie.presentation.navigation.MovieAppDestinations
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@OptIn(UnstableApi::class)
@Composable
fun MovieScreen(navController: NavController, viewModel: MovieViewModel = hiltViewModel()) {
    val movieResource by viewModel.movieDetailState.collectAsState()
    val episodeServerGroups by viewModel.episodeServerGroups.collectAsState() // Lấy danh sách các nhóm server
    val currentServerIndex by viewModel.currentServerIndex.collectAsState() // Lấy index server đang chọn để hiển thị tập tương ứng
    val isFavorite by viewModel.isFavorite.collectAsState()

    // --- State để điều khiển hiển thị Bottom Sheet ---
    var showFavoriteToggleBottomSheet by remember { mutableStateOf(false) }
    // State để lưu nội dung hiển thị trong Bottom Sheet
    var bottomSheetMessage by remember { mutableStateOf("") }
    // State cho action của nút trong Bottom Sheet (ví dụ: đi đến danh sách yêu thích)
    var bottomSheetButtonAction by remember { mutableStateOf<(() -> Unit)?>(null) } // Lambda của nút
    var bottomSheetButtonText by remember { mutableStateOf<String?>(null) } // Text của nút

    // State và scope cho ModalBottomSheet của Material 3
    val sheetState = rememberModalBottomSheetState( // Quản lý trạng thái ẩn/hiện của sheet
        skipPartiallyExpanded = true // True nếu chỉ muốn full screen hoặc hidden
    )
    val scope = rememberCoroutineScope() // Scope để chạy suspend functions (ví dụ: sheetState.show/hide)


    // --- LaunchedEffect để thu thập các sự kiện UI từ ViewModel ---
    // Chỉ chạy một lần khi Composable được compose (Unit key) hoặc khi dependency thay đổi
    LaunchedEffect(Unit) {
        // Thu thập các sự kiện từ uiEvent Flow của ViewModel
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MovieViewModel.UiEvent.ShowFavoriteToggleSuccess -> {
                    // Khi nhận được sự kiện thành công
                    bottomSheetMessage = "Đã thêm '${event.movieName}' vào danh sách yêu thích."

                    // Chuẩn bị action cho nút trong Bottom Sheet (ví dụ: đi đến màn hình Yêu thích)
                    bottomSheetButtonText = "Xem Danh Sách Yêu Thích"
                    bottomSheetButtonAction = {
                        // Action thực sự: Điều hướng
                        navController.navigate(MovieAppDestinations.FAVORITE_ROUTE) {
                            // Tùy chọn navigation options (ví dụ: clear stack)
                            // popUpTo(...)
                        }
                    }

                    // Cập nhật state để hiển thị Bottom Sheet
                    showFavoriteToggleBottomSheet = true
                    // Chạy suspend fun sheetState.show() trong coroutine scope
                    scope.launch { sheetState.show() } // Hiển thị sheet
                }
                // TODO: Xử lý các loại sự kiện UI khác từ ViewModel (ví dụ: hiển thị Snackbar lỗi)
                 is MovieViewModel.UiEvent.ShowError -> { /* Hiển thị Snackbar hoặc Dialog lỗi */ }
            }
        }
    }
    // --- Kết thúc LaunchedEffect ---

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
            val servers = episodeServerGroups // Sử dụng StateFlow đã map từ ViewModel
            // Thông tin phim và danh sách server/tập

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(MaterialTheme.shapes.medium)
                        ) {
                            AsyncImage(
                                model = movieDetail.metadata.thumb_url,
                                contentDescription = movieDetail.metadata.name,
                                contentScale = ContentScale.Crop, // Shows the full image, letterboxed if needed
                                modifier = Modifier.fillMaxSize(), // It will fit within this Box
                                error = painterResource(id = R.drawable.ic_launcher_background),
                            )

                            IconButton(
                                onClick = {
                                    viewModel.onFavoriteClick(movieDetail)
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) Color.Red else Color.Gray // Ví dụ đổi màu
                                )
                            }
                        }

                        Text(
                            text = movieDetail.metadata.name,
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )

                        val slightRoundedCornerShape = RoundedCornerShape(4.dp)

                        //Download and add to my list
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(modifier = Modifier.weight(1f),
                                shape = slightRoundedCornerShape,
                                onClick = {}) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Download"
                                )
                                Text("Download")

                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(modifier = Modifier.weight(1f),
                                shape = slightRoundedCornerShape,
                                onClick = {}) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add"
                                )
                                Text("My list")
                            }
                        }

                        CategoryChipsList(
                            movieDetail.metadata.category
                        )

                        Text(
                            text = "Mô tả: ${movieDetail.metadata.content}",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 5, // Có thể tăng số dòng mô tả
                            overflow = TextOverflow.Ellipsis
                        )

                        // Danh sách Server (Horizontal Scroll)
                        Text(
                            text = "Chọn Server:",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(end = 16.dp) // Padding cuối danh sách
                        ) {
                            itemsIndexed(servers) { index, serverGroup ->
                                val isSelected = index == currentServerIndex
                                val serverShape = RoundedCornerShape(8.dp)

                                Box(
                                    modifier = Modifier
                                        .clip(serverShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable {
                                            // Khi chọn server, chỉ cập nhật index server trong ViewModel để hiển thị danh sách tập
                                            viewModel.selectServer(index)
                                            Log.d(
                                                "MovieScreen",
                                                "Server clicked: ${serverGroup.serverName} (Index: $index)"
                                            )
                                        }
                                        .border(
                                            BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.5f
                                                )
                                            ),
                                            shape = serverShape
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = serverGroup.serverName,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Danh sách Tập (Vertical Grid) - Chỉ hiển thị tập của server đang chọn
                val selectedServerEpisodes =
                    servers.getOrNull(currentServerIndex)?.serverData ?: emptyList()

                Text(
                    text = "Danh sách Tập:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                if (selectedServerEpisodes.isNotEmpty()) {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        columns = GridCells.Fixed(5), // 5 cột
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp) // Padding cuối danh sách
                    ) {
                        itemsIndexed(selectedServerEpisodes) { index, episode ->
                            // Không cần state isPlayingThisEpisode hay isSelectedEpisode ở đây nữa
                            // val isPlayingThisEpisode = index == currentEpisodeIndex && playerState is PlayerState.Playing
                            // val isSelectedEpisode = index == currentEpisodeIndex

                            val boxShape = RoundedCornerShape(8.dp)

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f) // Tỷ lệ 1:1 cho ô tập
                                    .clip(boxShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer) // Màu nền mặc định
                                    .clickable {
                                        // Khi click vào tập, điều hướng đến MoviePlayerScreen
                                        // Truyền URL của tập phim và có thể cả index server/tập nếu cần cho MoviePlayerScreen
                                        val encodedUrl = URLEncoder.encode(
                                            episode.link_m3u8,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        val route =
                                            MovieAppDestinations.createMoviePlayerRoute(
                                                encodedUrl
                                            )
                                        navController.navigate(route)

                                        Log.d(
                                            "MovieScreen",
                                            "Episode box clicked, navigating to player: ${episode.name} (Server Index: $currentServerIndex, Episode Index: $index)"
                                        )
                                    }
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                alpha = 0.5f
                                            )
                                        ),
                                        shape = boxShape
                                    )
                                    .padding(4.dp), // Padding bên trong box
                                contentAlignment = Alignment.Center // Căn giữa nội dung
                            ) {
                                Text(
                                    text = episode.name,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                } else if (currentServerIndex != -1) {
                    // Hiển thị thông báo nếu server được chọn nhưng không có tập
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Không có tập nào trên server này.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Hiển thị thông báo nếu chưa có server nào được chọn (trường hợp ban đầu)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chọn một server để xem danh sách tập.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
        }
    }

    // --- Composable Modal Bottom Sheet ---
    // Chỉ compose khi showFavoriteToggleBottomSheet là true
    if (showFavoriteToggleBottomSheet) {
        ModalBottomSheet(
            // Khi sheet bị dismiss (bấm ra ngoài, vuốt xuống)
            onDismissRequest = {
                // Cập nhật state để ẩn sheet
                showFavoriteToggleBottomSheet = false
                // Optional: Nếu cần xử lý logic sau khi dismiss bằng swipe, thêm LaunchedEffect với key là sheetState.currentValue
            },
            sheetState = sheetState // Gắn state quản lý của sheet
        ) {
            // Nội dung hiển thị BÊN TRONG Bottom Sheet
            Column(
                modifier = Modifier
                    .padding(16.dp) // Padding nội dung
                    .fillMaxWidth(), // Chiếm toàn bộ chiều rộng
                horizontalAlignment = Alignment.CenterHorizontally // Căn giữa theo chiều ngang
            ) {
                // Thông báo xác nhận
                Text(
                    bottomSheetMessage,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center // Căn giữa text
                )

                Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách

                // Nút tùy chọn cho hành động tiếp theo
                if (bottomSheetButtonText != null && bottomSheetButtonAction != null) {
                    Button(onClick = {
                        // Chạy suspend fun hide() trong scope
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            // Thực hiện action sau khi sheet đã ẩn hoàn toàn
                            bottomSheetButtonAction?.invoke()
                        }
                    }) {
                        Text(bottomSheetButtonText ?: "", fontWeight = FontWeight.Bold)
                    }
                }
                // Optional: Thêm khoảng cách cuối nếu cần
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    // --- Kết thúc Composable Bottom Sheet ---
}
