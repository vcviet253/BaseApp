package com.example.mealplanner.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.mealplanner.common.Constants
import com.example.mealplanner.data.remote.AuthApi
import com.example.mealplanner.data.remote.ChatApi
import com.example.mealplanner.data.remote.GeminiApi
import com.example.mealplanner.data.remote.WebSocketChatClient
import com.example.mealplanner.data.repository.GeminiRepositoryImpl
import com.example.mealplanner.data.utils.NetworkHelper
import com.example.mealplanner.domain.repository.GeminiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
//    @Provides
//    @Singleton
//    fun provideMealDbApi(): MealDbApi {
//        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(MealDbApi::class.java)
//    }

//    @Provides
//    @Singleton
//    fun provideMealRepository(api: MealDbApi): MealRepository {
//        return MealRepositoryImpl(api)
//    }

    //Gemini AI API
    @Provides
    @Singleton
    fun provideGeminiApi(): GeminiApi {
        return NetworkHelper.createRetrofit(Constants.GEMINI_API_BASE_URL)
            .create(GeminiApi::class.java)
    }

    //Authentication API (Login)
    @Provides
    @Singleton
    fun provideAuthApi(): AuthApi {
        return NetworkHelper.createRetrofit(Constants.SERVER_BASE_URL).create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApi(): ChatApi {
        return NetworkHelper.createRetrofit(Constants.SERVER_BASE_URL).create(ChatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiRepository(
        api: GeminiApi,
        @Named("gemini_api_key") apiKey: String
    ): GeminiRepository {
        return GeminiRepositoryImpl(api, apiKey)
    }

    //Gemini API Key
    @Provides
    @Named("gemini_api_key")
    fun provideGeminiApiKey(): String {
        return "AIzaSyAXvM9mVbxXlSWijfNbJCl6-VoeeUXorw0" // 👈 Thay bằng API key thật (hoặc đọc từ BuildConfig)
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    fun provideWebSocketChatClient(
        okHttpClient: OkHttpClient
    ): WebSocketChatClient {
        return WebSocketChatClient(okHttpClient)
    }

    //UserID
    @Provides
    @Named("UserId")
    fun provideUserId(): String = "user123"

    //Shared Preferences for user's token, configs
    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "user_prefs",         // Tên file
            masterKey,              // Khóa mã hóa
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}