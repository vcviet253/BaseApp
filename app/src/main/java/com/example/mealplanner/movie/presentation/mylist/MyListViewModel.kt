package com.example.mealplanner.movie.presentation.mylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.usecase.GetFavoritedMoviesUseCase
import com.example.mealplanner.movie.domain.usecase.ToggleFavoriteStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val toggleFavoriteStatusUseCase: ToggleFavoriteStatusUseCase,
    private val getFavoritedMoviesUseCase: GetFavoritedMoviesUseCase,
): ViewModel() {
    val favoriteMovies: StateFlow<List<Movie>> =
        getFavoritedMoviesUseCase() // <-- Gọi Use Case để lấy Flow từ Repository/Room
            .stateIn( // Chuyển Flow thành StateFlow
                scope = viewModelScope, // Sử dụng ViewModel's scope
                started = SharingStarted.WhileSubscribed(5000), // Cấu hình khi nào Flow nên active
                initialValue = emptyList() // Giá trị ban đầu khi Flow chưa kịp emit giá trị đầu tiên
            )

    fun onRemoveFromMyList(movie: Movie) {
        // Chạy suspend fun của Use Case trong ViewModel's CoroutineScope
        viewModelScope.launch(Dispatchers.IO) {
            // Gọi Use Case Bật/Tắt trạng thái yêu thích
            toggleFavoriteStatusUseCase(movie)
            // UI sẽ tự động cập nhật trạng thái do isFavorite Flow trong Item Composable thay đổi
        }
    }
}