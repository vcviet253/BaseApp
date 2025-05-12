package com.example.mealplanner.movie.data.repository

import android.util.Log
import androidx.compose.runtime.key
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mealplanner.movie.data.remote.MovieApi
import com.example.mealplanner.movie.data.remote.dto.moviesbycategory.ItemDto
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "MoviesByKeywordPagingSource"

class MoviesByKeywordPagingSource(
    private val api: MovieApi,
    private val keyword: String,
    private val sortField: String? = null,
    private val sortType: String? = null,
    private val sortLang: String? = null,
    private val category: String? = null,
    private val country: String? = null,
    private val year: String? = null,
    private val limit: Int = 20 // Kích thước trang mặc định, nên khớp với pageSize trong PagingConfig
    ): PagingSource<Int, ItemDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemDto> {
        val pageNumber = params.key ?: 1 // Bắt đầu từ trang 1 nếu key null

        return try {
            val response = api.getMoviesByKeyword(
                keyword = keyword,
                page = pageNumber,
                sortField = sortField,
                sortType = sortType,
                sortLang = sortLang,
                category = category,
                country = country,
                year = year,
                limit = limit // Sử dụng limit, nên khớp với PagingConfig.pageSize
            )
            Log.d(TAG, response.status.toString())
            // --- Bước 2: KIỂM TRA response và response.data ---
            // Kiểm tra nếu response.data là null hoặc status API không phải "success"
            if (response.data == null || response.status != "success") {
                // API trả về lỗi hoặc dữ liệu bị thiếu. Xử lý nó như một lỗi Paging 3.
                val errorMessage = response.msg ?: "API returned null data or non-success status"
                // Trả về LoadResult.Error với thông báo lỗi
                return LoadResult.Error(RuntimeException(errorMessage))
            }
            // --- Kết thúc kiểm tra --

            // Lấy danh sách ItemDto và tổng số trang từ cấu trúc DTO lồng nhau
            val movieItems: List<ItemDto> = response.data.items
            val totalPages: Int = response.data.params.pagination.totalPages

            // Xác định nextKey dựa trên tổng số trang và trang hiện tại
            val nextKey = if (pageNumber < totalPages) {
                pageNumber + 1
            } else {
                null // Hết trang
            }

            LoadResult.Page(
                data = movieItems, // Trả về List<ItemDto>
                prevKey = null, // Thường là null cho API phân trang tiến
                nextKey = nextKey
            )
        } catch (e: IOException) {
            // Lỗi mạng
            LoadResult.Error(e)
        } catch (e: HttpException) {
            // Lỗi HTTP không thành công (ví dụ: 404, 500)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ItemDto>): Int? {
        // Logic xác định key để refresh
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}