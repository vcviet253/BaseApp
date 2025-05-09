package com.example.mealplanner.data.remote

import com.example.mealplanner.data.remote.dto.login.LoginRequest
import com.example.mealplanner.data.remote.dto.login.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}