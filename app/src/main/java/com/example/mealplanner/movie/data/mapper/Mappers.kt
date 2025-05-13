package com.example.mealplanner.movie.data.mapper

import com.example.mealplanner.movie.data.local.entity.FavoriteMovieEntity
import com.example.mealplanner.movie.domain.model.Category
import com.example.mealplanner.movie.domain.model.Country
import com.example.mealplanner.movie.domain.model.Episode
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.model.MovieMetadata
import com.example.mealplanner.movie.domain.model.ServerEpisodeData
import com.example.mealplanner.movie.domain.model.Tmdb

fun com.example.mealplanner.movie.data.remote.dto.common.CategoryDto.toDomain(): Category =
    Category(name, slug)

fun com.example.mealplanner.movie.data.remote.dto.common.CountryDto.toDomain(): Country =
    Country(name, slug)

fun com.example.mealplanner.movie.data.remote.dto.common.TmdbDto.toDomain(): Tmdb =
    Tmdb(season, type, vote_average, vote_count)

fun com.example.mealplanner.movie.data.remote.dto.singlemoviedetail.ServerDataDto.toDomain(): ServerEpisodeData =
    ServerEpisodeData(filename, link_embed, link_m3u8, name, slug)

fun com.example.mealplanner.movie.data.remote.dto.singlemoviedetail.EpisodeDto.toDomain(): Episode =
    Episode(server_data.map { it.toDomain() }, server_name)

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
        poster_url = getPosterUrl(poster_url),
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

// Hàm mapper từ FavoriteMovieEntity sang Domain Model Movie
fun FavoriteMovieEntity.toMovie(): Movie {
    // Tạo MovieMetadata object từ thông tin có trong FavoriteMovieEntity
    val metadata = MovieMetadata(
        id = this.id, // Lấy ID từ Entity
        // Các trường không có trong FavoriteMovieEntity sẽ dùng giá trị mặc định hoặc null
        actor = null, // Không có trong Entity
        category = emptyList(), // Không có trong Entity, dùng rỗng cho List non-nullable
        chieurap = null, // Không có trong Entity
        content = null, // Không có trong Entity
        country = emptyList(), // Không có trong Entity, dùng rỗng cho List non-nullable
        created = null, // Không có trong Entity
        director = null, // Không có trong Entity
        episode_current = "", // Không có trong Entity, dùng chuỗi rỗng cho String non-nullable
        episode_total = null, // Không có trong Entity
        imdb = null, // Không có trong Entity
        is_copyright = null, // Không có trong Entity
        lang = "", // Không có trong Entity, dùng chuỗi rỗng cho String non-nullable
        modified = "", // Không có trong Entity, dùng chuỗi rỗng cho String non-nullable (hoặc chuyển từ timestamp)
        name = this.title, // Lấy Title từ Entity (map sang name)
        notify = null, // Không có trong Entity
        origin_name = this.title, // Có thể dùng lại Title hoặc chuỗi rỗng
        poster_url = this.posterUrl, // Lấy Poster URL từ Entity
        quality = "", // Không có trong Entity, dùng chuỗi rỗng
        showtimes = null, // Không có trong Entity
        slug = this.slug, // Thường ID cũng là slug, nếu không thì dùng chuỗi rỗng ""
        status = null, // Không có trong Entity
        sub_docquyen = false, // Không có trong Entity, dùng false cho Boolean non-nullable
        thumb_url = this.thumbUrl, // Có thể dùng lại Poster URL hoặc chuỗi rỗng
        time = "", // Không có trong Entity, dùng chuỗi rỗng (hoặc chuyển từ timestamp)
        tmdb = null, // Không có trong Entity
        trailer_url = null, // Không có trong Entity
        type = "", // Không có trong Entity, dùng chuỗi rỗng
        view = null, // Không có trong Entity
        year = 0 // Không có trong Entity, dùng 0 cho Int non-nullable
    )

    // Tạo Movie object chứa metadata và episodes = null
    return Movie(
        metadata = metadata, // Gán metadata vừa tạo
        episodes = null // FavoriteMovieEntity không có thông tin episodes
    )
}

fun Movie.toFavoriteMovieEntity(): FavoriteMovieEntity {
    return FavoriteMovieEntity(
        id = this.metadata.id,
        title = this.metadata.name, // Hoặc title
        slug = this.metadata.slug,
        posterUrl = this.metadata.poster_url,
        thumbUrl = this.metadata.thumb_url,
        // favoritedTimestamp sẽ tự gen khi insert nếu có giá trị default
    )
}

// Hàm utility chuyển path poster sang URL đầy đủ
fun getPosterUrl(posterPath: String): String {
    val cleanPath = posterPath.removePrefix("/")
    // Tốt nhất là lấy domain này từ một constant chung
    val baseImageUrl = "https://phimimg.com/" // Ví dụ, hoặc từ một ApiConstants object
    return "$baseImageUrl$cleanPath"
}