package com.example.mealplanner.domain.model

import java.util.concurrent.TimeUnit

class Message(
    val from: String,
    val to: String,
    val text: String,
    val timestamp: Long,
) {
}