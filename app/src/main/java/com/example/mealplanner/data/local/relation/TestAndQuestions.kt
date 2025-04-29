package com.example.mealplanner.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.mealplanner.data.local.entity.QuestionEntity
import com.example.mealplanner.data.local.entity.TestEntity

/**
 * POJO to combine a Test with all its associated Questions (each with its answer option).
 * This is typically what your DAO query would return.
 */
data class TestAndQuestions(
    @Embedded val test: TestEntity,

    @Relation(
        entity = QuestionEntity::class,  // Need to specify intermediate entity for nested relation
        parentColumn = "id",
        entityColumn = "testId",
    )

    // This list contains Questions, each bundled with its (single) correct AnswerOption
    val questionsWithOptions: List<QuestionWithAnswerOption>
) {
}