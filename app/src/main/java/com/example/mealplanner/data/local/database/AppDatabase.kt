package com.example.mealplanner.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mealplanner.data.local.dao.TestDao
import com.example.mealplanner.data.local.entity.AnswerOptionEntity
import com.example.mealplanner.data.local.entity.QuestionEntity
import com.example.mealplanner.data.local.entity.TestEntity

@Database(
    entities = [TestEntity::class, QuestionEntity::class, AnswerOptionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun testDao(): TestDao
}