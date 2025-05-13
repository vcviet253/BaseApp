package com.example.mealplanner.movie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val id: String, // ID phim từ API (vd: _id từ ItemDto)
    val title: String,
    val slug: String,
    val posterUrl: String,
    val thumbUrl: String,
    val favoritedTimestamp: Long = System.currentTimeMillis() // Thời gian đánh dấu
    // Có thể thêm các trường khác nếu cần hiển thị offline (vd: year, quality)
)