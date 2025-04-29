package com.example.mealplanner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [ForeignKey(
        entity = TestEntity::class,
        parentColumns = ["id"],
        childColumns = ["testId"],
        onDelete = ForeignKey.CASCADE, // Delete questions if parent test is deleted
    )],
    indices = [Index("testId")]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testId: Long,
    val questionNumber: Int, // The number associated with the question (e.g., 1, 2, 3)
    val prompt: String?,  // Optional text prompt (might be null for map labeling)
    // Optional: Specific audio timings for this question within the main audio
    val audioStartTimeMs: Long?,
    val audioEndTimeMs: Long?,
    )
