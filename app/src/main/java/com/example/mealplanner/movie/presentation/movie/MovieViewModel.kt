package com.example.mealplanner.movie.presentation.movie

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Episode
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.usecase.GetMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieUseCase: GetMovieUseCase,
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

    // StateFlow cho index của tập phim đang được phát trong server đang chọn
    // State này KHÔNG CẦN THIẾT ở MovieScreen ViewModel vì không quản lý phát lại ở đây.
    // Có thể xóa hoặc giữ lại nếu có mục đích khác (ví dụ: đánh dấu tập đã xem).
    // Tạm thời xóa đi để làm rõ chức năng.
    // private val _currentEpisodeIndex = MutableStateFlow(-1)
    // val currentEpisodeIndex: StateFlow<Int> = _currentEpisodeIndex.asStateFlow()

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

    // Toàn bộ các hàm và state liên quan đến ExoPlayer và phát lại đã được loại bỏ khỏi ViewModel này.
    // Chúng nên được quản lý bởi ViewModel của MoviePlayerScreen.

    override fun onCleared() {
        super.onCleared()
        Log.d("MovieViewModel", "onCleared called.")
        // Không cần release player ở đây vì player không còn thuộc ViewModel này.
    }
}