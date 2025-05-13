package com.example.mealplanner.movie.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mealplanner.movie.data.local.dao.FavoriteMovieDao
import com.example.mealplanner.movie.data.local.entity.FavoriteMovieEntity

@Database(
    entities = [FavoriteMovieEntity::class],
    version = 1,
    exportSchema = false
)

abstract class MovieDatabase: RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
}