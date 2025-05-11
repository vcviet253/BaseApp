package com.example.mealplanner.movie.data.mapper

import com.example.mealplanner.movie.domain.model.Category
import com.example.mealplanner.movie.domain.model.Country
import com.example.mealplanner.movie.domain.model.Episode
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.model.MovieMetadata
import com.example.mealplanner.movie.domain.model.ServerEpisodeData
import com.example.mealplanner.movie.domain.model.Tmdb

fun com.example.mealplanner.movie.data.remote.dto.common.CategoryDto.toDomain(): Category = Category(name, slug)
fun com.example.mealplanner.movie.data.remote.dto.common.CountryDto.toDomain(): Country = Country(name, slug)
fun com.example.mealplanner.movie.data.remote.dto.common.TmdbDto.toDomain(): Tmdb = Tmdb(season, type, vote_average, vote_count)
fun com.example.mealplanner.movie.data.remote.dto.singlemoviedetail.ServerDataDto.toDomain(): ServerEpisodeData =
    ServerEpisodeData(filename, link_embed, link_m3u8, name, slug)

fun com.example.mealplanner.movie.data.remote.dto.singlemoviedetail.EpisodeDto.toDomain(): Episode = Episode(server_data.map { it.toDomain() }, server_name)

fun com.example.mealplanner.movie.data.remote.dto.recentlyupdated.RecentlyUpdatedMovieDto.toDomain(): MovieMetadata {
    return MovieMetadata(
        id = _id,
        category = category.map { it.toDomain() },
        country = country.map { it.toDomain() },
        episode_current = episode_current,
        imdb = imdb.toString(),
        lang = lang,
        modified = modified.time,
        name = name,
        origin_name = origin_name,
        poster_url = poster_url,
        quality = quality,
        slug = slug,
        sub_docquyen = sub_docquyen,
        thumb_url = thumb_url,
        time = time,
        tmdb = tmdb.toDomain(),
        type = type,
        year = year,
    )
}

fun com.example.mealplanner.movie.data.remote.dto.singlemoviedetail.MovieMetadataDto.toDomain(): MovieMetadata {
    return MovieMetadata(
        id = _id,
        actor = actor,
        category = category.map { it.toDomain() },
        chieurap = chieurap,
        content = content,
        country = country.map { it.toDomain() },
        created = created.time,
        director = director,
        episode_current = episode_current,
        episode_total = episode_total,
        imdb = imdb.toString(),
        is_copyright = is_copyright,
        lang = lang,
        modified = modified.time,
        name = name,
        notify = notify,
        origin_name = origin_name,
        poster_url = poster_url,
        quality = quality,
        showtimes = showtimes,
        slug = slug,
        status = status,
        sub_docquyen = sub_docquyen,
        thumb_url = thumb_url,
        time = time,
        tmdb = tmdb.toDomain(),
        trailer_url = trailer_url,
        type = type,
        view = view,
        year = year
    )
}

fun com.example.mealplanner.movie.data.remote.dto.recentlyupdated.RecentlyUpdatedMovies.toDomain(): List<Movie> {
    return items.map { dto ->
        Movie(
            metadata = dto.toDomain(),
            episodes = null // No episodes in recently updated list
        )
    }
}

fun com.example.mealplanner.movie.data.remote.dto.singlemoviedetail.SingleMovieDetailDto.toDomain(): Movie {
    return Movie(movie.toDomain(), episodes.map { it.toDomain() })
}


fun com.example.mealplanner.movie.data.remote.dto.moviesbycategory.ItemDto.toMetadata(): MovieMetadata {
    return MovieMetadata(
        id = _id,
        category = category.map { it.toDomain() },
        chieurap = chieurap,
        country = country.map { it.toDomain() },
        episode_current = episode_current,
        lang = lang,
        modified = modified.time,
        name = name,
        origin_name = origin_name,
        poster_url = poster_url,
        quality = quality,
        slug = slug,
        sub_docquyen = sub_docquyen,
        thumb_url = thumb_url,
        time = time,
        type = type,
        year = year
    )
}

fun com.example.mealplanner.movie.data.remote.dto.moviesbycategory.ResultDto.toMovieList(): List<Movie> {
    return this.`data`.items.map {
        Movie(it.toMetadata())
    }
}