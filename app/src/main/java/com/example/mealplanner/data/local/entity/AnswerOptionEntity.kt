package com.example.mealplanner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "answer_options",
    foreignKeys = [ForeignKey(
        entity = QuestionEntity::class,
        parentColumns = ["id"],
        childColumns = ["questionId"],
        onDelete = ForeignKey.CASCADE, // Delete answer options if parent question is deleted
    )],
    indices = [Index("questionId")]
)
data class AnswerOptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: Long,
    /**
     * The text of the correct answer label (e.g., "C").
     */
    val optionText: String,
    /**
     * Should always be TRUE for Map Labeling entries in this optimized schema.
     */
    val isCorrect: Boolean = true // Default to true, as we only store the correct one here
)