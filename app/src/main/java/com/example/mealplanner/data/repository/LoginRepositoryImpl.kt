package com.example.mealplanner.data.repository

import com.example.mealplanner.data.remote.AuthApi
import com.example.mealplanner.data.remote.dto.login.LoginRequest
import com.example.mealplanner.data.remote.dto.login.LoginResponse
import com.example.mealplanner.domain.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val api: AuthApi) : LoginRepository {
    override suspend fun login(username: String, password: String): LoginResponse {
        return withContext(Dispatchers.IO) {
            api.login(LoginRequest(username,password))
        }
    }
}