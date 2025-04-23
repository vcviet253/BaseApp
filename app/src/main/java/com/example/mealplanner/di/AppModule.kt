package com.example.mealplanner.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.mealplanner.common.Constants
import com.example.mealplanner.data.preferences.UserPreferences
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
import java.util.concurrent.TimeUnit
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
    fun provideUserPreferences(sharedPreferences: SharedPreferences): UserPreferences
    {
        return UserPreferences(sharedPreferences)
    }
}