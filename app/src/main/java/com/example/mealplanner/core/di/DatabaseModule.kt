package com.example.mealplanner.core.di

import android.content.Context
import androidx.room.Room
import com.example.mealplanner.data.local.dao.TestDao
import com.example.mealplanner.data.local.database.AppDatabase
import com.example.mealplanner.movie.data.local.MovieDatabase
import com.example.mealplanner.movie.data.local.dao.FavoriteMovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTestDao(database: AppDatabase): TestDao {
        return database.testDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteMovieDao(
        database: MovieDatabase
    ): FavoriteMovieDao {
        return database.favoriteMovieDao()
    }

    @Provides
    @Singleton
    fun provideMovieDatabase(
        @ApplicationContext context: Context
    ): MovieDatabase {
        return Room.databaseBuilder(
            context,
            MovieDatabase::class.java,
            "movie.db"
        ).fallbackToDestructiveMigration()
            .build()
    }
}
