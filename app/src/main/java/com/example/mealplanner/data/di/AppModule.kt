package com.example.mealplanner.data.di

import com.example.mealplanner.common.Constants
import com.example.mealplanner.data.remote.MealDbApi
import com.example.mealplanner.data.repository.MealRepositoryImpl
import com.example.mealplanner.domain.model.Meal
import com.example.mealplanner.domain.repository.MealRepository
import com.google.gson.Gson
import com.google.gson.internal.GsonBuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMealDbApi(): MealDbApi {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MealDbApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMealRepository(api: MealDbApi): MealRepository {
        return MealRepositoryImpl(api)
    }
}