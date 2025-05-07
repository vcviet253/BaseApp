package com.example.mealplanner.movie.data

import com.example.mealplanner.movie.data.dto.recentlyupdated.RecentlyUpdatedMovies
import com.example.mealplanner.movie.data.dto.singlemoviedetail.SingleMovieDetailDto
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
}