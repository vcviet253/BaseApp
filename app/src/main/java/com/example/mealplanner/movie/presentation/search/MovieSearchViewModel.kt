package com.example.mealplanner.movie.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.usecase.GetMoviesByKeywordPagedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "MovieSearchViewModel"

@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    private val getMoviesByKeywordPagedUseCase: GetMoviesByKeywordPagedUseCase
) : ViewModel() {
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


//    // Khai báo Flow<PagingData<Movie>> để cung cấp dữ liệu cho UI
    //Bạn đang gọi getMoviesByKeywordPagedUseCase một lần duy nhất khi ViewModel được khởi tạo,
    // và bạn truyền vào giá trị hiện tại của _searchQuery.value tại thời điểm đó (lúc đầu là chuỗi rỗng "").
    //Luồng searchResults này được tạo ra và cache.
    // Khi người dùng gõ vào thanh tìm kiếm, updateSearchQuery sẽ cập nhật _searchQuery.value,
    //nhưng luồng searchResults đã được tạo ra trước đó không tự động phản ứng với sự thay đổi này. Nó vẫn giữ nguyên kết quả tìm kiếm cho từ khóa ban đầu ("").
    //Để khắc phục, bạn cần làm cho luồng searchResults lắng nghe sự thay đổi của luồng searchQuery và tạo ra một luồng Paging 3 mới mỗi khi từ khóa thay đổi.
    // Operator flatMapLatest (hoặc switchMap nếu bạn dùng LiveData) sinh ra cho mục đích này.
//    val searchResults: Flow<PagingData<Movie>> =
//        // Gọi Use Case, truyền categorySlug vừa lấy được
//        getMoviesByKeywordPagedUseCase(keyword = _searchQuery.value)
//            // Sử dụng cachedIn để cache PagingData trong ViewModel scope.
//            // Điều này giúp giữ lại dữ liệu khi cấu hình thay đổi (ví dụ: xoay màn hình).
//            .cachedIn(viewModelScope)

    // --- PHẦN searchResults ĐÃ ĐƯỢC SỬA ---
    // searchResults bây giờ là một Flow được tạo ra bằng cách biến đổi Flow của searchQuery

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<PagingData<Movie>> =
        searchQuery // <-- Flow bắt đầu từ StateFlow (giá trị ban đầu "")
            .onEach { query -> Log.d(TAG, "searchQuery emitted: '$query'") } // Log giá trị phát ra từ StateFlow

            // Optional: Lọc các query rỗng
            .filter { it.isNotBlank() } // <-- Filter chỉ cho non-blank qua
            .onEach { filteredQuery -> Log.d(TAG, "filter passed: '$filteredQuery'") } // Log sau filter

            // Optional: debounce
            .debounce(300) // <-- Chờ 300ms ngừng gõ
            .onEach { debouncedQuery -> Log.d(TAG, "debounce passed: '$debouncedQuery'") } // Log sau debounce

            .flatMapLatest { query -> // <-- Khi Flow đến đây...
                Log.d(TAG, "flatMapLatest triggered for query: '$query'") // Log trước khi gọi UseCase
                getMoviesByKeywordPagedUseCase(keyword = query) // <-- Gọi UseCase (trả về Flow<PagingData<Movie>>)
            }
            .onEach { pagingData -> Log.d(TAG, "flatMapLatest emitted PagingData") } // Log khi UseCase/Repository phát ra PagingData

            .cachedIn(viewModelScope) // Cache PagingData

            .onEach { cachedPagingData -> Log.d(TAG, "searchResults emitted cached PagingData") } // Log khi Flow cuối cùng phát ra
    // .catch { e -> Log.e("SearchFlow", "Flow caught error: ${e.message}", e) } // Optional: catch lỗi nếu flow bị crash



    // TODO: Nếu bạn có các bộ lọc/sắp xếp khác từ UI (ví dụ: Sort by Year, Filter by Country),
    // chúng cũng nên là StateFlows và được kết hợp (combine) với searchQuery flow trước flatMapLatest.
    // Ví dụ: combine(searchQuery, sortFieldStateFlow) { query, sort -> Pair(query, sort) }.flatMapLatest { (query, sort) -> useCase(query, sort) }

}