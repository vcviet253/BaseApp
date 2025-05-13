package com.example.mealplanner.movie.presentation.mylist

import android.util.Log
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mealplanner.R
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.presentation.navigation.MovieAppDestinations

private const val TAG = "MyListScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListScreen(
    navController: NavController,
    viewModel: MyListViewModel = hiltViewModel()
) {
    // Thu thập StateFlow của danh sách phim yêu thích thành State của Compose
    val favoriteMovies: List<Movie> by viewModel.favoriteMovies.collectAsState()
    var isInEditMode by remember { mutableStateOf(false) }

    // Sử dụng Scaffold để cấu trúc màn hình (TopAppBar + nội dung chính)
    Scaffold(
        topBar = {
            // TopAppBar cho màn hình Danh sách yêu thích
            TopAppBar(
                title = { Text(text = if (!isInEditMode) "My List" else "Edit") }, // Tiêu đề màn hình
                navigationIcon = {
                    if (isInEditMode) {
                        IconButton(onClick = { isInEditMode = false }) { // Logic thoát chế độ xóa
                            Icon(Icons.Filled.Close, contentDescription = "Exit remove mode")
                        }
                    }
                    // Nút quay lại nếu màn hình này không phải là điểm bắt đầu của navigation stack
                    else if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.popBackStack() }) { // Logic quay lại
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
                        }
                    }
                },
                actions = {
                    if (!isInEditMode) {
                        IconButton(
                            onClick = { isInEditMode = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Enter remove mode"
                            )
                        }
                    }
                }
            )
        }
        // Không cần bottomBar ở đây nếu nó được quản lý bởi Scaffold ngoài cùng trong MainAppScreen
    ) { paddingValues -> // Padding từ Scaffold (TopAppBar)
        // Khu vực nội dung chính của màn hình, áp dụng padding từ Scaffold
        // Note: Nếu MainAppScreen đã áp dụng padding cho NavHost, Composable này cũng nhận padding đó.
        // Cần xử lý lồng ghép padding cẩn thận nếu cả Scaffold trong và ngoài đều áp dụng padding.
        // Cách đơn giản: Composable này chỉ áp dụng padding từ Scaffold này, và NavHost đã xử lý padding ngoài.
        Box(
            modifier = Modifier
                .fillMaxSize() // Chiếm hết không gian
                .padding(paddingValues) // <-- Áp dụng padding từ Scaffold này
            // Nếu NavHost đã có padding ngoài, Box này nằm bên trong vùng đã pad đó.
        ) {
            // --- Xử lý các trạng thái hiển thị (Rỗng, Có dữ liệu) ---

            if (favoriteMovies.isNullOrEmpty()) { // Kiểm tra nếu danh sách rỗng (bao gồm cả trạng thái ban đầu emptyList)
                //Set edit mode ve false khi nguoi dung xoa phim yeu thich cuoi cung
                isInEditMode = false

                // Trạng thái: Danh sách rỗng (chưa có phim yêu thích)
                Box( // Box này lấp đầy khu vực nội dung chính
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center // Căn giữa nội dung
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = "No favorites",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        ) // Icon mờ
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Chưa có phim yêu thích nào.",
                            style = MaterialTheme.typography.bodyLarge
                        ) // Thông báo
                    }
                }
            } else {
                // Trạng thái: Danh sách có dữ liệu
                // Hiển thị danh sách phim yêu thích bằng LazyColumn (hoặc LazyVerticalGrid)
                LazyColumn( // Hoặc LazyVerticalGrid với columns = GridCells.Fixed(...)
                    modifier = Modifier.fillMaxSize(), // Lấp đầy khu vực nội dung
                    contentPadding = PaddingValues(
                        horizontal = 8.dp,
                        vertical = 0.dp
                    ), // Padding quanh nội dung list
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Khoảng cách giữa các item
                ) {
                    // Sử dụng hàm items chuẩn cho List<Movie> (KHÔNG phải items extension của Paging)
                    items(
                        favoriteMovies,
                        key = { movie -> movie.metadata.id }// <-- Truyền List<Movie>
                    ) { movie -> // Lambda nhận đối tượng Movie
                        // Hiển thị từng item phim yêu thích sử dụng Composable item chung
                        FavoriteMovieListItem(movie,
                            isInEditMode,
                            onMovieClick = { slug ->
                                navController.navigate("${MovieAppDestinations.MOVIE_DETAIL_ROUTE_BASE}/${slug}")
                            },
                            onRemoveFromMyListClick = { slug ->
                                viewModel.onRemoveFromMyList(movie)
                            })
                    }
                }
            }

            // TODO: Xử lý trạng thái Loading (nếu có cơ chế hiển thị loading ban đầu, ví dụ check nếu list là null trước emptyList)
            // TODO: Xử lý trạng thái Error (nếu có cơ chế báo lỗi khi đọc từ DB, ví dụ catch error trong ViewModel và expose state lỗi)
        }
    }
}

@Composable
fun FavoriteMovieListItem(
    movie: Movie,
    isInEditMode: Boolean,
    onMovieClick: (String) -> Unit,
    onRemoveFromMyListClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = movie.metadata.thumb_url,
            contentDescription = movie.metadata.name,
            modifier = Modifier
                .width(100.dp)
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_launcher_background), // Ảnh placeholder
            error = painterResource(id = R.drawable.ic_launcher_background), // Replace with your error image
        )

        Text(
            movie.metadata.name,
            maxLines = 3, // Giới hạn số dòng
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )

        IconButton(
            onClick = {
                if (!isInEditMode) onMovieClick(movie.metadata.slug)
                else onRemoveFromMyListClick(movie.metadata.slug)
                Log.d(TAG, "Clicked on ${movie.metadata.slug}")
            },
        ) {
            Icon(
                imageVector = if (!isInEditMode) Icons.Default.PlayArrow else Icons.Default.DeleteForever,
                contentDescription = "Icons",
                tint = if (isInEditMode) Color.Red else Color.Black
            )
        }
    }
}
