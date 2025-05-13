package com.example.mealplanner.movie.data.model

enum class MovieCategory(val displayName: String, val slug: String) {
    HOC_DUONG("Học Đường", "hoc-duong"),
    GIA_DINH("Gia Đình", "gia-dinh"),
    TINH_CAM("Tình Cảm", "tinh-cam");

    companion object {
        fun fromSlug(slug: String): MovieCategory? {
            return values().find { it.slug == slug }
        }
    }
}
