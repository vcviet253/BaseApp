package com.example.mealplanner.domain.repository

interface TokenRepository {
    suspend fun saveToken(token: String)
    fun getToken(): String?
}