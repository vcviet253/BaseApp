package com.example.mealplanner.data.repository

import android.content.SharedPreferences
import androidx.compose.ui.input.key.Key
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import com.example.mealplanner.domain.repository.TokenRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    @ApplicationContext context: ApplicationContext,
    private val sharedPreferences: SharedPreferences
): TokenRepository {

    companion object {
        private const val KEY_TOKEN = "auth_token"
    }

    override suspend fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }


    override fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }
}