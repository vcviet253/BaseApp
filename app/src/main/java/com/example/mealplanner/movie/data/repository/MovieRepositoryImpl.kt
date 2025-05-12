package com.example.mealplanner.movie.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.data.mapper.toDomain
import com.example.mealplanner.movie.data.mapper.toMetadata
import com.example.mealplanner.movie.data.mapper.toMovieList
import com.example.mealplanner.movie.data.remote.MovieApi
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "MovieRepositoryImpl"

class MovieRepositoryImpl @Inject constructor(private val api: MovieApi) : MovieRepository {
    override suspend fun getRecentlyUpdatedMovies(): Resource<List<Movie>> {
        return try {
            Resource.Success(api.getRecentlyUpdatedMovies(1).toDomain())
        } catch (e: Exception) {
            println(e.localizedMessage)
            Resource.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }

    override suspend fun getMovie(slug: String): Resource<Movie> {
        return try {
            Resource.Success(api.getSingleMovie(slug).toDomain())
        } catch (e: Exception) {
            println(e.localizedMessage)
            Resource.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }

    override suspend fun getMoviesByCategory(
        type: String,
        page: Int?,
        sort_field: String?,
        sort_type: String?,
        sort_lang: String?,
        country: String?,
        year: String?,
        limit: Int?
    ): Resource<List<Movie>> {
        return try {
            Resource.Success(
                api.getMoviesByCategory(
                    type,
                    page,
                    sort_field,
                    sort_type,
                    sort_lang,
                    country,
                    year,
                    limit
                ).toMovieList()
            )
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }

    override fun getMoviesByCategoryPaged(
        type: String,
        sortField: String?,
        sortType: String?,
        sortLang: String?,
        country: String?,
        year: String?
    ): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // Kích thước trang cho Paging 3, có thể khác limit Fixed
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                // Khởi tạo PagingSource, truyền các tham số cần thiết
                // PagingSource sẽ sử dụng 'limit' này khi gọi API
                MoviesByCategoryPagingSource(
                    apiService = api,
                    categoryType = type,
                    sortField = sortField,
                    sortType = sortType,
                    sortLang = sortLang,
                    country = country,
                    year = year,
                    limit = 20 // Đảm bảo khớp với PagingConfig.pageSize
                )
            }
        ).flow // Luồng PagingData<ItemDto>
            .map { pagingData ->// Map PagingData<ItemDto> sang PagingData<Movie>
                pagingData.map { itemDto -> // Map tung itemDto sang Movie chua Metadata
                    Movie(itemDto.toMetadata())
                }
            }
    }

    override fun getMoviesByKeywordPaged(
        keyword: String,
        sortField: String?,
        sortType: String?,
        sortLang: String?,
        category: String?,
        country: String?,
        year: String?
    ): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            )
        ) {
            MoviesByKeywordPagingSource(
                api,
                keyword,
                sortField,
                sortType,
                sortLang,
                category,
                country,
                year,
                limit = 20
            )
        }.flow
            .map { pagingData ->
                pagingData.map { itemDto ->
                    Log.d(TAG, "${itemDto.toMetadata().name}")
                    Movie(itemDto.toMetadata())
                }
            }
    }
}