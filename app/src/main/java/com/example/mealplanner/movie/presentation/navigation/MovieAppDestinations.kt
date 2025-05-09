package com.example.mealplanner.movie.presentation.navigation

object MovieAppDestinations {

    const val MOVIE_DETAIL_ROUTE_BASE = "movie_detail" // Phần gốc của route
    const val MOVIE_DETAIL_ARG_SLUG = "slug"          // Tên argument
    const val MOVIE_DETAIL_ROUTE = "$MOVIE_DETAIL_ROUTE_BASE/{$MOVIE_DETAIL_ARG_SLUG}"

    // --- Movie Player ---
    const val MOVIE_PLAYER_BASE_ROUTE = "movie_player"
    const val MOVIE_PLAYER_ARG_URL = "videoUrl" // Key này phải khớp với key trong SavedStateHandle
    const val MOVIE_PLAYER_ROUTE = "$MOVIE_PLAYER_BASE_ROUTE/{$MOVIE_PLAYER_ARG_URL}" // Route đầy đủ với tham số

    const val HOME_ROUTE = "movie_home" // Phần gốc của route

    const val  SETTINGS_ROUTE = "settings" // Route for settings screen
    const val BOOKMARKS_ROUTE = "bookmarks" // Route for bookmarks screen
    const val ONBOARDING_ROUTE = "onboarding" // Route for onboarding screen

    fun createMoviePlayerRoute(videoUrl: String): String {
        // QUAN TRỌNG: Bạn phải ENCODE URL vì nó sẽ được nhúng vào URI.
        // Các ký tự như '/', '?', '&', '=' sẽ gây lỗi nếu không được encode.
        // Log lỗi bạn cung cấp: 'https%3A%2F%2Fs4.phim1280.tv%2F...' cho thấy hệ thống cũng đang encode.
        val encodedUrl = java.net.URLEncoder.encode(videoUrl, "UTF-8")
        return "$MOVIE_PLAYER_BASE_ROUTE/$encodedUrl"
    }
}