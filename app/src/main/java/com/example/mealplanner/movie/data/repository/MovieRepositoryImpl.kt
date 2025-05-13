package com.example.mealplanner.movie.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.data.local.dao.FavoriteMovieDao
import com.example.mealplanner.movie.data.mapper.toDomain
import com.example.mealplanner.movie.data.mapper.toFavoriteMovieEntity
import com.example.mealplanner.movie.data.mapper.toMetadata
import com.example.mealplanner.movie.data.mapper.toMovie
import com.example.mealplanner.movie.data.mapper.toMovieList
import com.example.mealplanner.movie.data.remote.MovieApi
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "MovieRepositoryImpl"

class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApi,
    private val favoriteMovieDao: FavoriteMovieDao,
) : MovieRepository {
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

    // Triển khai phương thức toggleFavoriteStatus trong Repository
    override suspend fun toggleFavoriteStatus(movie: Movie) {
        // Chuẩn bị Entity để chèn. Repository luôn chuẩn bị nó.
        // DAO sẽ quyết định có dùng hay không dựa vào trạng thái hiện tại.
        val movieEntityToInsert = movie.toFavoriteMovieEntity()

        // Gọi phương thức transactional trong DAO
        // Pass ID và Entity cần chèn
        favoriteMovieDao.toggleFavoriteTransaction(movie.metadata.id, movieEntityToInsert)

        // Note: Nếu UseCase/ViewModel cần biết trạng thái cuối cùng,
        // bạn có thể làm cho Repository method này trả về Boolean mà DAO method trả về.
        // override suspend fun toggleFavoriteStatus(movie: Movie): Boolean {
        //    val movieEntityToInsert = movie.toFavoriteMovieEntity()
        //    return favoriteMovieDao.toggleFavoriteTransaction(movie.metadata.id, movieEntityToInsert)
        // }
    }

    override fun isFavorite(movieId: String): Flow<Boolean> {
        return favoriteMovieDao.isFavorite(movieId)
    }

    override fun getFavoritedMovies(): Flow<List<Movie>> {
        return favoriteMovieDao.getAllFavorites()
            // Áp dụng map operator trên Flow. Mỗi khi DAO phát ra List<FavoriteMovieEntity> mới,
            // lambda này sẽ được gọi với list đó, và chúng ta map từng Entity sang Movie.
            .map { favoriteEntities ->
                // map từng FavoriteMovieEntity trong list sang Movie Domain Model
                favoriteEntities.map { it.toMovie() }
            }
    }
    // Note: Nếu bạn quyết định dùng Paging 3 cho danh sách yêu thích offline (getAllFavoritesPaged trong DAO):
    // override fun getFavoritedMovies(): Flow<PagingData<Movie>> { // Chữ ký hàm thay đổi kiểu trả về
    //     return Pager(
    //         config = PagingConfig(pageSize = ...), // Cấu hình Paging
    //         pagingSourceFactory = { favoriteMovieDao.getAllFavoritesPaged() } // DAO trả về PagingSource
    //     ).flow // Flow<PagingData<FavoriteMovieEntity>>
    //      .map { pagingData -> pagingData.map { it.toMovie() } } // Map PagingData<Entity> sang PagingData<Domain Model>
    // }
}