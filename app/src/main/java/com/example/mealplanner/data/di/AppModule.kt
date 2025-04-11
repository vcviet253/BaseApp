package com.example.mealplanner.data.di

import com.example.mealplanner.common.Constants
import com.example.mealplanner.data.remote.GeminiApi
import com.example.mealplanner.data.remote.WebSocketChatClient
import com.example.mealplanner.data.repository.GeminiRepositoryImpl
import com.example.mealplanner.domain.repository.GeminiRepository
import com.google.gson.Gson
import com.google.gson.internal.GsonBuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Provides
    @Singleton
    fun provideGeminiApi(): GeminiApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()

        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiRepository(
        api: GeminiApi,
        @Named("gemini_api_key") apiKey: String
    ): GeminiRepository {
        return GeminiRepositoryImpl(api, apiKey)
    }

    @Provides
    @Named("gemini_api_key")
    fun provideGeminiApiKey(): String {
        return "AIzaSyAXvM9mVbxXlSWijfNbJCl6-VoeeUXorw0" // 👈 Thay bằng API key thật (hoặc đọc từ BuildConfig)
    }

    @Provides
    fun provideOkHttpClient() : OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    fun provideWebSocketChatClient(
        okHttpClient: OkHttpClient
    ): WebSocketChatClient {
        return WebSocketChatClient(okHttpClient)
    }

    @Provides
    @Named("UserId")
    fun provideUserId(): String = "user123"
}