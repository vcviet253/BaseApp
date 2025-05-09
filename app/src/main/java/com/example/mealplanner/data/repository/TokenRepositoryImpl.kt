package com.example.mealplanner.data.repository

import android.content.SharedPreferences
import androidx.compose.ui.input.key.Key
import com.example.mealplanner.data.preferences.UserPreferences
import com.example.mealplanner.domain.repository.TokenRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences
): TokenRepository {

    companion object {
        private const val KEY_TOKEN = "auth_token"
    }

    override suspend fun saveToken(token: String) {
        userPreferences.saveToken(token)
    }

    override fun getToken(): String? {
        return userPreferences.getToken()
    }
}