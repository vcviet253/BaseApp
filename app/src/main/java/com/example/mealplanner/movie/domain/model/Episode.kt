package com.example.mealplanner.movie.domain.model

import com.example.mealplanner.movie.data.dto.singlemoviedetail.ServerDataDto

data class Episode(
    val serverData: List<ServerData>,
    val serverName: String
)