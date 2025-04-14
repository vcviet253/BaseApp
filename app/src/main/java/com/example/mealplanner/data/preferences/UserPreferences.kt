package com.example.mealplanner.data.preferences

import android.content.SharedPreferences
import javax.inject.Inject

class UserPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
)
{
    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
    }

    suspend fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    suspend fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    suspend fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}