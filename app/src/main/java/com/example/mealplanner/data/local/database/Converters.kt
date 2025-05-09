package com.example.mealplanner.data.local.database

import androidx.room.TypeConverter
import com.example.mealplanner.data.local.model.TestType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTestType(value: TestType?): String? = value?.name

    @TypeConverter
    fun toTestType(value: String?): TestType? = value?.let { enumValueOf<TestType>(it) }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? = value?.let { gson.toJson(it) }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val listType: Type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, listType)
        }
    }
}