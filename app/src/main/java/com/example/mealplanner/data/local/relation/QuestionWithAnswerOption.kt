package com.example.mealplanner.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.mealplanner.data.local.entity.AnswerOptionEntity
import com.example.mealplanner.data.local.entity.QuestionEntity

/**
 * POJO to combine a Question with its (single) correct AnswerOption for Map Labeling.
 */
data class QuestionWithAnswerOption(
    @Embedded val question: QuestionEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )

    //For Map Labeling, we only have one answer option per question ( list contains only 1 element)
    val answerOptions: List<AnswerOptionEntity>
) {
    // Convenience getter for the single correct option
    val correctOption: AnswerOptionEntity?
        get() = answerOptions.firstOrNull() // Since we only store the correct one
}