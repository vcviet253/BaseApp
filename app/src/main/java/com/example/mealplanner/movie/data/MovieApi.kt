package com.example.mealplanner.movie.data

import com.example.mealplanner.movie.data.dto.moviesbycategory.ResultDto
import com.example.mealplanner.movie.data.dto.recentlyupdated.RecentlyUpdatedMovies
import com.example.mealplanner.movie.data.dto.singlemoviedetail.SingleMovieDetailDto
import com.example.mealplanner.movie.domain.model.Movie
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("danh-sach/phim-moi-cap-nhat-v3")
    suspend fun getRecentlyUpdatedMovies(
        @Query("page") currentPage: Int,
    ): RecentlyUpdatedMovies

    @GET("phim/{slug}")
    suspend fun getSingleMovie(
        @Path("slug") slug: String
    ): SingleMovieDetailDto

    /**
     * Thông số kỹ thuật:
     * - type_list = Thể loại phim cần lấy, sử dụng API phimapi.com/the-loai để lấy chi tiết slug.
     * - page = Số trang cần truy xuất, sử dụng [totalPages] để biết tổng trang khả dụng.
     * - sort_field = modified.time > tính theo thời gian cập nhật, _id > lấy theo ID của phim, year > lấy theo số năm phát hành của phim.
     * - sort_type = desc hoặc asc.
     * - sort_lang = vietsub > phim có Vietsub, thuyet-minh > phim có Thuyết Minh, long-tieng > phim có Lồng Tiếng.
     * - country = Quốc gia phim cần lấy, sử dụng API phimapi.com/quoc-gia để lấy chi tiết slug.
     * - year = Năm phát hành của phim (1970 - hiện tại).
     * - limit = Giới hạn kết quả (tối đa 64).
     */
    @GET("v1/api/the-loai/{type_list}")
    suspend fun getMoviesByCategory(
        @Path("type_list") type: String,
        @Query("page") page: Int? = null,
        @Query("sort_field") sortField: String? = null,
        @Query("sort_type") sortType: String? = null,
        @Query("sort_lang") sortLang: String? = null,
        @Query("country") country: String? = null,
        @Query("year") year: String? = null,
        @Query("limit") limit: Int? = null
    ): ResultDto
}