package com.example.mealplanner.movie.presentation.search

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.presentation.home.MoviePosterCard
import androidx.compose.material3.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieSearchScreen(
    navController: NavController,
    viewModel: MovieSearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults: LazyPagingItems<Movie> = viewModel.searchResults.collectAsLazyPagingItems()

    // State for the SearchBar's active state (could be managed in ViewModel)
    // For a dedicated search screen, you might always want it active/expanded visually
    var active by remember { mutableStateOf(true) } // Start active

    // Optional: Focus requester to automatically focus the search field
    val focusRequester = remember { FocusRequester() }
    // Optional: Keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    // Màn hình Search này không sử dụng Scaffold riêng.
    // Layout gốc (Column) KHÔNG cần áp dụng padding từ Scaffold ngoài.
    // Nó sẽ tự động nằm trong Box đã được padding bởi NavHost.
    Column( // Column là layout gốc chứa SearchBar và Grid/List
        modifier = Modifier
            .fillMaxSize() // <-- Chiếm toàn bộ không gian của Box cha đã có padding
        // <-- BỎ .padding(outerPaddingValues)
    ) {
        // --- SearchBar Composable theo overload mới ---
        SearchBar(
            // --- REQUIRED PARAMETERS CHO CONTAINER ---
            // expanded và onExpandedChange quản lý trạng thái mở rộng/thu gọn của SearchBar container.
            expanded = active, // Tie to the state variable
            onExpandedChange = { // Callback khi trạng thái mở rộng/thu gọn thay đổi
                active = it // Cập nhật state
                if (!it) {
                    // Nếu chuyển sang thu gọn, ẩn bàn phím.
                    keyboardController?.hide()
                    // Optional: clear focus
                    // focusRequester.freeFocus()
                } else {
                    // Nếu chuyển sang mở rộng (hoặc ban đầu), yêu cầu focus và hiển thị bàn phím.
                    // focusRequester.requestFocus() // Yêu cầu focus vào input field (sẽ làm trong InputField)
                    keyboardController?.show()
                }
            },

            // trailingIcon cho SearchBar container (ví dụ: icon mic, share) - optional

            shape = SearchBarDefaults.fullScreenShape, // Sử dụng Shape mặc định cho màn hình full screen (Thay TODO)
            colors = SearchBarDefaults.colors(), // Sử dụng Màu sắc mặc định từ Material 3 (Thay TODO)
            tonalElevation = SearchBarDefaults.Elevation, // Sử dụng Elevation mặc định (Thay TODO)
            shadowElevation = SearchBarDefaults.Elevation, // Sử dụng Shadow Elevation mặc định (Thay TODO)
            windowInsets = SearchBarDefaults.windowInsets, // Sử dụng WindowInsets mặc định (Thay TODO)
            modifier = Modifier.fillMaxWidth(), // Modifier áp dụng cho SearchBar container

            // --- REQUIRED INPUT FIELD SLOT ---
            inputField = { // Cung cấp Composable cho phần nhập liệu thực tế
                // Sử dụng component nhập liệu chuẩn của SearchBar
                SearchBarDefaults.InputField(
                    query = searchQuery, // Pass query từ ViewModel
                    onQueryChange = { viewModel.updateSearchQuery(it) }, // Pass onQueryChange vào đây
                    onSearch = { // onSearch xử lý khi người dùng nhấn Enter/Search trên bàn phím
                        keyboardController?.hide() // Ẩn bàn phím
                        // Optional: thu gọn search bar khi submit nếu cần
                        // isSearchBarExpanded = false
                    },
                    placeholder = { Text("Tìm kiếm phim...") }, // Placeholder cho input field
                    leadingIcon = { // Icon ở đầu input field (ví dụ: icon Search)
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = { // Icon ở cuối input field (ví dụ: nút Xóa)
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) { // Logic xóa query
                                Icon(Icons.Default.Close, contentDescription = "Xóa")
                            }
                        }
                    },
                    // Modifier áp dụng cho input field (ví dụ: fillMaxWidth)
                    modifier = Modifier.focusRequester(focusRequester), // Áp dụng focusRequester vào input field

                    // --- REQUIRED PARAMETERS CHO INPUT FIELD (Thay thế các TODO từ snippet trước) ---
                    expanded = active, // Tie InputField's expanded state to SearchBar's (Thay TODO)
                    onExpandedChange = { isExpandedState ->
                        // Callback này từ InputField có thể không cần xử lý phức tạp
                        // nếu logic mở rộng chính nằm ở SearchBar container.
                        // Tuy nhiên, API yêu cầu, nên chúng ta có thể cập nhật state chính ở đây
                        active = isExpandedState
                    }, // Thay TODO
                    enabled = true, // Thường là true cho input (Thay TODO)
                    colors = SearchBarDefaults.inputFieldColors(), // Sử dụng colors mặc định (Thay TODO)
                    interactionSource = remember { MutableInteractionSource() }, // Sử dụng default (Thay TODO)
                    // --- END REQUIRED PARAMETERS CHO INPUT FIELD ---
                )
            },
            // --- END INPUT FIELD SLOT ---

        ) { // <-- Đây là content lambda chính của SearchBar (hiển thị khi expanded)
            // Nội dung hiển thị khi SearchBar container 'expanded' (thường dùng cho GỢI Ý tìm kiếm, lịch sử search...)
            // Trên màn hình search chuyên dụng, nó có thể dùng để hiển thị UI ban đầu hoặc gợi ý.
            // Chúng ta sẽ sử dụng nó để hiển thị lời nhắc khi query rỗng.

            if (searchQuery.isBlank() && searchResults.itemCount == 0 && searchResults.loadState.refresh is LoadState.NotLoading) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Nhập từ khóa để tìm kiếm phim.")
                }
            }
            // TODO: Thêm UI cho gợi ý tìm kiếm hoặc lịch sử search ở đây khi query không rỗng nhưng SearchBar vẫn expanded
            // Ví dụ: LazyColumn hiển thị danh sách gợi ý
            // else if (query is not blank && suggestions are available) { LazyColumn(...) { items(...) {... suggestions ...} } }
        }
        // --- END SearchBar Composable ---


        // Phần hiển thị kết quả tìm kiếm (LazyVerticalGrid) bên dưới SearchBar
        // Đây là nội dung chính của màn hình khi SearchBar KHÔNG chiếm toàn bộ màn hình
        Box( // Box để có thể overlay các trạng thái (loading, empty, error) lên khu vực kết quả
            modifier = Modifier
                .fillMaxSize() // <-- Chiếm hết không gian còn lại trong Column (bên dưới SearchBar)
            // Có thể thêm padding ngang nếu cần
            // .padding(horizontal = 8.dp)
        ) {
            // --- Xử lý các trạng thái UI cho kết quả PAGING ---
            // Các trạng thái này áp dụng cho LazyVerticalGrid

            // Trạng thái không tìm thấy kết quả sau khi search (query không rỗng, load xong, item count = 0)
            if (searchQuery.isNotBlank() && searchResults.itemCount == 0 && searchResults.loadState.refresh is LoadState.NotLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                ) {
                    Text(
                        "Không tìm thấy kết quả nào cho '${searchQuery}'",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            // Trạng thái hiển thị danh sách kết quả (hoặc đang tải append)
            else if (searchResults.itemCount > 0) { // Chỉ hiển thị Grid nếu có item
                LazyVerticalGrid( // Hoặc LazyColumn
                    columns = GridCells.Fixed(3), // Cấu hình grid (ví dụ 3 cột)
                    modifier = Modifier.fillMaxSize(), // <-- Chiếm hết không gian còn lại trong Box
                    contentPadding = PaddingValues(8.dp), // Padding nội dung list/grid
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        count = searchResults.itemCount, // Đối tượng LazyPagingItems
                    ) { index ->
                        val movie = searchResults[index]
                        if (movie != null) {
                            // Sử dụng lại Composable MovieItem để hiển thị một item kết quả tìm kiếm
                            MoviePosterCard(movie) { }
                        } else {
                            // Placeholder nếu cần
                            // LoadingItemPlaceholder()
                        }
                    }

                    // Xử lý trạng thái tải thêm ở cuối danh sách (append)
                    when (searchResults.loadState.append) {
                        is LoadState.Loading -> {
                            item(span = { GridItemSpan(maxLineSpan) }) { // Chiếm toàn bộ chiều ngang grid
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() } // Indicator loading nhỏ
                            }
                        }

                        is LoadState.Error -> {
                            // TODO: Hiển thị UI lỗi append (có thể nút thử lại)
                            // val error = searchResults.loadState.append as LoadState.Error
                            // item(span = { maxLineSpan }) { Text("Lỗi tải thêm: ${error.error.localizedMessage}", modifier = Modifier.fillMaxWidth().padding(16.dp).wrapContentWidth(Alignment.CenterHorizontally)) }
                        }

                        is LoadState.NotLoading -> {
                            // TODO: Tùy chọn: Hiển thị "Hết danh sách" nếu cuối cùng
                            // if (searchResults.loadState.append.endOfPaginationReached) { item(span = { maxLineSpan }) { Text("Đã hết kết quả.", ...) } }
                        }
                    }
                    // TODO: Xử lý prepend nếu API hỗ trợ
                }
            }


            // Xử lý trạng thái tải ban đầu (refresh) - overlay toàn màn hình kết quả
            when (searchResults.loadState.refresh) {
                is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is LoadState.Error -> {
                    // TODO: Hiển thị UI lỗi full màn hình refresh
                    // val error = searchResults.loadState.refresh as LoadState.Error
                    // Text("Lỗi tải kết quả: ${error.error.localizedMessage}", modifier = Modifier.align(Alignment.Center))
                }

                is LoadState.NotLoading -> {
                    // Trạng thái đã xử lý ở trên (hiển thị list, empty, no results)
                }
            }
        }

        // Tự động focus vào SearchBar và hiển thị bàn phím khi màn hình xuất hiện
        // Cần thêm .focusRequester(focusRequester) vào SearchBarDefaults.InputField
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
}