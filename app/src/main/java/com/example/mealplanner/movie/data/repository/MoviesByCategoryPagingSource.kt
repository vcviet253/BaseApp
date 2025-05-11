package com.example.mealplanner.movie.data.repository

import android.graphics.pdf.LoadParams
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mealplanner.movie.data.remote.MovieApi
import com.example.mealplanner.movie.data.remote.dto.moviesbycategory.ItemDto // PagingSource làm việc với ItemDto
import retrofit2.HttpException
import java.io.IOException

class MoviesByCategoryPagingSource(
    private val apiService: MovieApi,
    private val categoryType: String,
    private val sortField: String? = null,
    private val sortType: String? = null,
    private val sortLang: String? = null,
    private val country: String? = null,
    private val year: String? = null,
    private val limit: Int = 20 // Kích thước trang mặc định, nên khớp với pageSize trong PagingConfig
) : PagingSource<Int, ItemDto>() { // Key = Int (page number), Value = ItemDto
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemDto> {
        val pageNumber = params.key ?: 1 // Bắt đầu từ trang 1 nếu key null

        return try {
            val response = apiService.getMoviesByCategory(
                type = categoryType,
                page = pageNumber,
                sortField = sortField,
                sortType = sortType,
                sortLang = sortLang,
                country = country,
                year = year,
                limit = limit // Sử dụng limit, nên khớp với PagingConfig.pageSize
            )

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