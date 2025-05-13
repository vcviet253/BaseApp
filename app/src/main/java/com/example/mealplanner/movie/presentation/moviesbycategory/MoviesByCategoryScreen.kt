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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.presentation.home.MoviePosterCard
import com.example.mealplanner.movie.presentation.navigation.MovieAppDestinations

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
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // Số cột
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(count  = movies.itemCount, key = { index ->
                // Nếu có ID thì nên dùng movie?.id thay vì index
                movies[index]?.metadata?.id ?: index
            }) { index ->
                val movie = movies[index]
                if (movie != null) {
                   MoviePosterCard(movie, { slug ->
                       navController.navigate("${MovieAppDestinations.MOVIE_DETAIL_ROUTE_BASE}/$slug")
                   })
                } else {
                    // Placeholder UI nếu bật enablePlaceholders = true
                    // MoviePlaceholderItem()
                }
            }

            // LoadState.Append (loading page tiếp theo)
            when (val appendState = movies.loadState.append) {
                is LoadState.Loading -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is LoadState.Error -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            "Lỗi tải thêm: ${appendState.error.localizedMessage}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                is LoadState.NotLoading -> {
                    if (movies.loadState.append.endOfPaginationReached) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                "Đã hết danh sách",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
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