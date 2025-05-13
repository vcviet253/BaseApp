package com.example.mealplanner.core.di

import android.content.Context
import android.content.SharedPreferences
import com.example.mealplanner.core.common.Constants
import com.example.mealplanner.data.preferences.UserPreferences
import com.example.mealplanner.data.remote.AuthApi
import com.example.mealplanner.data.remote.ChatApi
import com.example.mealplanner.data.remote.GeminiApi
import com.example.mealplanner.data.remote.WeatherApi
import com.example.mealplanner.data.utils.NetworkHelper
import com.example.mealplanner.movie.data.remote.MovieApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    //Gemini AI API
    @Provides
    @Singleton
    fun provideGeminiApi(okHttpClient: OkHttpClient): GeminiApi {
        return NetworkHelper.createRetrofit(Constants.GEMINI_API_BASE_URL, okHttpClient)
            .create(GeminiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherApi(okHttpClient: OkHttpClient): WeatherApi {
        return NetworkHelper.createRetrofit(Constants.WEATHER_API_BASE_URL,okHttpClient)
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieApi(okHttpClient: OkHttpClient): MovieApi {
        return NetworkHelper.createRetrofit(Constants.MOVIE_API_BASE_URL, okHttpClient)
            .create(MovieApi::class.java)
    }

    //Authentication API (Login)
    @Provides
    @Singleton
    fun provideAuthApi(okHttpClient: OkHttpClient): AuthApi {
        return NetworkHelper.createRetrofit(Constants.SERVER_BASE_URL,okHttpClient).create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApi(okHttpClient: OkHttpClient): ChatApi {
        return NetworkHelper.createRetrofit(Constants.SERVER_BASE_URL,okHttpClient).create(ChatApi::class.java)
    }

    //Gemini API Key
    @Provides
    @Named("gemini_api_key")
    fun provideGeminiApiKey(): String {
        return "AIzaSyAXvM9mVbxXlSWijfNbJCl6-VoeeUXorw0" // 👈 Thay bằng API key thật (hoặc đọc từ BuildConfig)
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(7, TimeUnit.SECONDS)   // Timeout kết nối
            .writeTimeout(7, TimeUnit.SECONDS)     // Timeout gửi data lên
            .readTimeout(7, TimeUnit.SECONDS)      // Timeout nhận data về
            .build()
    }

    //UserID
    @Provides
    @Named("UserId")
    fun provideUserId(): String = "user123"

    //Shared Preferences for user's token, configs
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(sharedPreferences: SharedPreferences): UserPreferences {
        return UserPreferences(sharedPreferences)
    }
}