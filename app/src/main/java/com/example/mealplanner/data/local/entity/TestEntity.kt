package com.example.mealplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.mealplanner.data.local.database.Converters
import com.example.mealplanner.data.local.model.TestType

@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,

    @ColumnInfo(index = true) val testType: TestType,
    val audioUrl: String, //URL or Path for main audio
    val imageUrl: String?, //URL or path to map image (nullable, not all tests have images

    /**
     * Stores List<String> as JSON, e.g., ["A", "B", "C"].
     * Contains ALL possible answer labels visible on the map.
     */
    val commonAnswerPoolJson: String?
    ) {

}