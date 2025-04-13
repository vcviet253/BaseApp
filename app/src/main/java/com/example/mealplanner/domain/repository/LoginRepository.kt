package com.example.mealplanner.domain.repository

import com.example.mealplanner.data.remote.dto.login.LoginResponse

interface LoginRepository {
    suspend fun login(username: String, password: String): LoginResponse
}