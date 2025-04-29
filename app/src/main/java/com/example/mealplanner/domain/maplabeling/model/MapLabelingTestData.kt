package com.example.mealplanner.domain.maplabeling.model

data class MapLabelingTestData(
    val testInfo: Test,
    val questions: List<Question>
) {
}

data class Test(
    val id: Long,
    val title: String,
    val audioSource: String,
    val imageUrl: String?,
    val answerPool: List<String>
)

data class Question(
    val number: Int,
    val prompt: String?,
    val audioStartTimeMs:Long?,
    val audioEndTimeMs: Long?,
    val correctLabel: String, // Đáp án đúng (ví dụ: "C")
    val audioResourceName: String? = null // Tên file audio riêng cho câu hỏi (nếu có)
)
