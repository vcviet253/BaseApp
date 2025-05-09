package com.example.mealplanner.data.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkHelper {
    fun createRetrofit(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}