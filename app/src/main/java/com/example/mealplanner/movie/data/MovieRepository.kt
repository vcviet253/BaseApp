package com.example.mealplanner.movie.data

import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.repository.MovieRepository
import com.example.mealplanner.movie.domain.model.Movie
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(private val api: MovieApi) : MovieRepository {
    override suspend fun getRecentlyUpdatedMovies(): Resource<List<Movie>> {
        return try {
            Resource.Success(api.getRecentlyUpdatedMovies(1).toDomain())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }

    override suspend fun getMovie(slug: String): Resource<Movie> {
        return try {
            Resource.Success(api.getSingleMovie(slug).toDomain())
        } catch (e:Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }
}