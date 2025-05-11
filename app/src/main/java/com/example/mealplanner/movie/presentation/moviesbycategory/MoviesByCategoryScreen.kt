package com.example.mealplanner.movie.presentation.moviesbycategory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.mealplanner.movie.domain.model.Movie

@Composable
fun MoviesByCategoryScreen(
    navController: NavController,
    viewModel: MoviesByCategoryViewModel = hiltViewModel()
) {
    // Thu thập Flow<PagingData<Movie>> từ ViewModel thành đối tượng LazyPagingItems<Movie>
    // LazyPagingItems này tự động quản lý việc tải dữ liệu khi người dùng cuộn
    val movies: LazyPagingItems<Movie> = viewModel.movies.collectAsLazyPagingItems()

    // Sử dụng Box để có thể overlay các indicator loading hoặc trạng thái rỗng/lỗi
    Box(modifier = Modifier.fillMaxSize()) {
        // Lazy list để hiển thị dữ liệu phân trang
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp), // Padding quanh nội dung list
            verticalArrangement = Arrangement.spacedBy(8.dp) // Khoảng cách giữa các item
        ) {
            // Sử dụng extension function `items` từ Paging Compose cho LazyPagingItems
            // Hàm này tự động xử lý sự khác biệt dữ liệu (diffing), placeholders (nếu bật),
            // và cung cấp từng item Movie (hoặc null nếu placeholder được dùng)
            items(count = movies.itemCount, key = { it })
            { index ->
                val movie = movies[index]
                // 'movie' ở đây là một item thuộc kiểu Movie (Domain Model).
                // Nó có thể là null nếu bạn bật placeholder trong PagingConfig.
                // Tuy nhiên, mặc định enablePlaceholders=false, nên 'movie' thường không null khi hiển thị.
                if (movie != null) {
                    // Gọi Composable để hiển thị một item phim cụ thể
                    // Sử dụng Composable MovieItem mà chúng ta đã phác thảo trước đó
                    MovieItem(movie = movie, onMovieClick = {})
                } else {
                    // Nếu dùng placeholder và item là null, có thể hiển thị một placeholder UI
                    // Ví dụ: LoadingItemPlaceholder()
                }
            }

            // Xử lý trạng thái tải ở CUỐI danh sách (khi cuộn để tải trang TIẾP THEO)
            when (movies.loadState.append) {
                is LoadState.Loading -> {
                    item { // Thêm một item đặc biệt ở cuối list
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator() // Indicator loading nhỏ ở cuối
                        }
                    }
                }

                is LoadState.Error -> {
                    // TODO: Hiển thị thông báo lỗi hoặc nút thử lại ở cuối list
                    // val error = movies.loadState.append as LoadState.Error
                    // item { Text("Lỗi tải thêm: ${error.error.localizedMessage}") }
                }

                is LoadState.NotLoading -> {
                    // Không làm gì nếu không loading append
                    if (movies.loadState.append.endOfPaginationReached) {
                        // TODO: Tùy chọn: Hiển thị "Đã hết danh sách"
                        // item { Text("Đã hết danh sách", modifier = Modifier.fillMaxWidth().padding(16.dp).wrapContentWidth(Alignment.CenterHorizontally)) }
                    }
                }
            }
            // TODO: Xử lý trạng thái tải ở ĐẦU danh sách (prepend) nếu API hỗ trợ
        }

        // Xử lý trạng thái tải ban đầu (REFRESH) - thường hiển thị toàn màn hình
        when (movies.loadState.refresh) {
            is LoadState.Loading -> {
                // Hiển thị indicator loading full màn hình khi tải lần đầu
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is LoadState.Error -> {
                // TODO: Hiển thị thông báo lỗi hoặc nút thử lại full màn hình
                // val error = movies.loadState.refresh as LoadState.Error
                // Text("Lỗi tải dữ liệu: ${error.error.localizedMessage}", modifier = Modifier.align(Alignment.Center))
            }

            is LoadState.NotLoading -> {
                // TODO: Xử lý trường hợp danh sách rỗng sau khi tải xong
                // Nếu loadState.append.endOfPaginationReached && movies.itemCount == 0
                if (movies.loadState.append.endOfPaginationReached && movies.itemCount == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Không có phim nào trong thể loại này.")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieItem(
    movie: Movie, // Bây giờ Composable này nhận vào đối tượng Movie
    // Ví dụ thêm lambda xử lý click
    onMovieClick: (String) -> Unit // Slug hoặc ID để điều hướng
) {
    Card(
        // Khi click, gọi lambda onMovieClick, truyền slug hoặc ID từ metadata
        onClick = { onMovieClick(movie.metadata.slug) }, // Sử dụng slug từ metadata
        modifier = Modifier.fillMaxWidth() // Hoặc Modifier cho dạng lưới
    ) {
        Column {
            // Truy cập thông tin cần hiển thị thông qua thuộc tính 'metadata'
            AsyncImage(
                model = movie.metadata.poster_url, // Dùng poster_url từ metadata
                contentDescription = movie.metadata.name, // Dùng tên từ metadata
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Chiều cao tùy chỉnh
                contentScale = ContentScale.Crop // Cắt ảnh cho vừa khung
            )
            Text(
                text = movie.metadata.name, // Dùng tên từ metadata
                modifier = Modifier.padding(8.dp)
            )
            // TODO: Hiển thị các thông tin metadata khác nếu cần, ví dụ:
            // Text("Năm: ${movie.metadata.year}", style = MaterialTheme.typography.bodySmall)
            // Text("Chất lượng: ${movie.metadata.quality}", style = MaterialTheme.typography.bodySmall)


            // --- Quan trọng ---
            // Tại Composable này khi dùng cho màn hình Category, bạn biết movie.episodes sẽ luôn null.
            // Do đó, bạn sẽ không cố gắng hiển thị movie.episodes ở đây dựa trên dữ liệu từ API category.
            // Nếu MovieItem này là generic và dùng ở màn hình Chi tiết phim (nơi episodes có thể không null),
            // thì trong Composable này, bạn sẽ thêm logic kiểm tra:
            // if (movie.episodes != null && movie.episodes.isNotEmpty()) {
            //     // Hiển thị danh sách tập phim ở đây
            //     Text("Danh sách tập:", modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp))
            //     // ... Composable khác để hiển thị danh sách tập phim ...
            // }
        }
    }
}