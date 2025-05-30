package com.example.mealplanner.movie.presentation.movie

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Episode
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.usecase.CheckFavoriteStatusUseCase
import com.example.mealplanner.movie.domain.usecase.GetMovieUseCase
import com.example.mealplanner.movie.domain.usecase.ToggleFavoriteStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieUseCase: GetMovieUseCase,
    private val toggleFavoriteStatusUseCase: ToggleFavoriteStatusUseCase,
    private val checkFavoriteStatusUseCase: CheckFavoriteStatusUseCase
) : ViewModel() {
    val slug: String = checkNotNull(savedStateHandle["slug"])

    // StateFlow để quản lý trạng thái tải và dữ liệu chi tiết phim
    private val _movieDetailState = MutableStateFlow<Resource<Movie>>(Resource.Loading())
    val movieDetailState = _movieDetailState.asStateFlow()

    // StateFlow cho danh sách các nhóm server (Episode)
    val episodeServerGroups: StateFlow<List<Episode>> =
        movieDetailState.map<Resource<Movie>, List<Episode>> { movieResource ->
            when (movieResource) {
                is Resource.Success -> {
                    movieResource.data.episodes ?: emptyList()
                }
                is Resource.Loading, is Resource.Error -> {
                    emptyList()
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // StateFlow cho index của server đang được chọn để hiển thị danh sách tập tương ứng
    private val _currentServerIndex = MutableStateFlow(0) // Mặc định chọn server đầu tiên (index 0)
    val currentServerIndex: StateFlow<Int> = _currentServerIndex.asStateFlow()

    // --- SỬA LỖI: isFavorite FLOW phụ thuộc vào movieDetailState ---
    // isFavorite Flow bây giờ lắng nghe movieDetailState.
    // Khi movieDetailState chuyển sang Success, nó sẽ lấy ID và kích hoạt checkFavoriteStatusUseCase.
    @OptIn(ExperimentalCoroutinesApi::class)
    val isFavorite: StateFlow<Boolean> =
        movieDetailState // <-- Bắt đầu từ Flow của movieDetailState
            .flatMapLatest { resource -> // <-- Khi movieDetailState thay đổi (Loading, Success, Error)...
                when (resource) {
                    is Resource.Success -> {
                        // Nếu dữ liệu chi tiết phim đã load thành công, lấy ID phim
                        val movieId = resource.data.metadata.id
                        // <-- Quan trọng: Chuyển sang lắng nghe Flow trạng thái yêu thích cho ID này
                        checkFavoriteStatusUseCase(movieId)
                    }
                    is Resource.Loading, is Resource.Error -> {
                        // Nếu vẫn đang tải hoặc bị lỗi, phát ra Flow với giá trị false (trạng thái ban đầu/lỗi)
                        flowOf(false) // <-- Sử dụng flowOf(false) để tạo Flow chỉ phát ra false một lần
                    }
                }
            }
            .distinctUntilChanged() // Optional: Chỉ phát ra trạng thái khi nó thực sự thay đổi
            .stateIn( // <-- Chuyển kết quả Flow<Boolean> thành StateFlow<Boolean>
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false // <-- Giá trị ban đầu của StateFlow isFavorite
            )
    // --- KẾT THÚC isFavorite FLOW ĐÃ SỬA ---

    /**
     * Tải chi tiết phim dựa trên slug.
     */
    fun fetchMovieDetail(slug: String) {
        _movieDetailState.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            val result = getMovieUseCase(slug)
            _movieDetailState.value = result
            if (result is Resource.Success) {
                // Khi fetch phim mới thành công, mặc định chọn server đầu tiên nếu có
                _currentServerIndex.value = result.data.episodes?.firstOrNull()?.let { 0 } ?: -1 // Chọn 0 nếu có server, -1 nếu không
                // _currentEpisodeIndex.value = -1 // Reset index tập
                Log.d("MovieViewModel", "Movie detail fetched successfully for slug: $slug. Selected server index: ${_currentServerIndex.value}")
            } else if (result is Resource.Error) {
                Log.e("MovieViewModel", "Failed to fetch movie detail for slug: $slug. Error: ${result.message}")
            }
        }
    }


    init {
        fetchMovieDetail(slug)
        println(slug)
    }

    /**
     * Cập nhật index của server đang được chọn để hiển thị danh sách tập tương ứng.
     * Hàm này KHÔNG bắt đầu phát video.
     */
    fun selectServer(serverIndex: Int) {
        val servers = episodeServerGroups.value
        if (serverIndex >= 0 && serverIndex < servers.size) {
            _currentServerIndex.value = serverIndex
            // Khi đổi server, không cần reset index tập đang phát vì không quản lý phát ở đây.
            // _currentEpisodeIndex.value = -1 // Loại bỏ dòng này
            Log.d("MovieViewModel", "Server selected: ${servers[serverIndex].serverName} (Index: $serverIndex)")
        } else {
            Log.w("MovieViewModel", "selectServer called with invalid serverIndex: $serverIndex")
        }
    }

    // --- COMPLETE onFavoriteClick FUNCTION ---
    // Hàm được gọi từ UI (e.g., Movie Detail Screen Composable)
    // khi nút yêu thích được click.
    // UI sẽ truyền đối tượng Movie đầy đủ cho ViewModel.
    fun onFavoriteClick(movie: Movie) {
        // Launch một coroutine để thực thi suspend Use Case
        viewModelScope.launch(Dispatchers.IO) { // Sử dụng IO dispatcher vì liên quan DB/Network
            Log.d("MovieViewModel", "onFavoriteClick called for movie: ${movie.metadata.name}")
            try {
                // Gọi Use Case để Bật/Tắt trạng thái yêu thích.
                // Use Case/Repository/DAO cần được sửa để TRẢ VỀ BOOLEAN (trạng thái cuối cùng sau khi toggle).
                val isNowFavorited = toggleFavoriteStatusUseCase(movie) // <-- Use Case trả về Boolean

                // Nếu Use Case hoàn thành mà không ném exception, coi là thành công.
                // Gửi sự kiện tới UI để hiển thị Bottom Sheet xác nhận.
                Log.d("MovieViewModel", "Toggle successful. Is now favorited: $isNowFavorited")
                _uiEvent.send(UiEvent.ShowFavoriteToggleSuccess(movie.metadata.name))

            } catch (e: Exception) {
                // Xử lý lỗi có thể xảy ra trong quá trình toggle (ví dụ: lỗi DB, lỗi mapping)
                Log.e("MovieViewModel", "Error toggling favorite status for movie: ${movie.metadata.name}", e)
                // Gửi sự kiện lỗi tới UI để hiển thị thông báo lỗi
                _uiEvent.send(UiEvent.ShowError(e.localizedMessage ?: "Unknown error toggling favorite"))
            }
        }
    }
    // --- END onFavoriteClick FUNCTION --

    // --- Channel để gửi các sự kiện một lần tới UI ---
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Định nghĩa các loại sự kiện UI có thể gửi
    sealed class UiEvent {
        // Sự kiện báo hiệu thao tác toggle favorite đã thành công
        // Bao gồm tên phim và trạng thái cuối cùng (true: đã thêm, false: đã xóa)
        data class ShowFavoriteToggleSuccess(val movieName: String) : UiEvent()
        // TODO: Thêm các loại sự kiện khác nếu cần (ví dụ: ShowError, NavigateTo...)
        data class ShowError(val message: String) : UiEvent() // <-- Ví dụ sự kiện lỗi
    }
    // --- Kết thúc Channel và sealed class ---

}