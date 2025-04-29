package com.example.mealplanner.data.mapper

import com.example.mealplanner.data.local.relation.QuestionWithAnswerOption
import com.example.mealplanner.data.local.relation.TestAndQuestions
import com.example.mealplanner.domain.maplabeling.model.MapLabelingTestData
import com.example.mealplanner.domain.maplabeling.model.Question
import com.example.mealplanner.domain.maplabeling.model.Test
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

// Sử dụng extension functions cho tiện lợi

fun TestAndQuestions.toDomainModel(): MapLabelingTestData? {
    // Chỉ map nếu test và questions không null
    val testEntity = this.test
    val questionsWithOptions = this.questionsWithOptions

    // Parse commonAnswerPoolJson
    val answerPoolList: List<String> = parseJsonStringList(testEntity.commonAnswerPoolJson)

    // Map TestEntity sang TestInfo
    val testInfo = Test(
        id = testEntity.id,
        title = testEntity.title,
        // Quyết định nguồn audio chính: URL trực tiếp hay tạo URI từ tên resource
        // Ở đây ta giả định ViewModel sẽ xử lý tạo URI, nên chỉ cần audioUrl gốc
        audioSource = testEntity.audioUrl,
        imageUrl = testEntity.imageUrl,
        answerPool = answerPoolList
    )

    // Map List<QuestionWithAnswerOption> sang List<QuestionInfo>
    val questionsInfo = questionsWithOptions.mapNotNull { it.toDomainModel() }

    // Chỉ trả về TestData nếu có đủ thông tin cần thiết
    if (questionsInfo.isNotEmpty()) { // Cần ít nhất 1 câu hỏi
        return MapLabelingTestData(
            testInfo = testInfo,
            questions = questionsInfo
        )
    } else {
        // Hoặc throw exception / log lỗi nếu logic yêu cầu phải có question
        println("WARN: Test with id ${testEntity.id} has no valid questions after mapping.")
        return null // Trả về null nếu không map được question nào
    }
}

fun QuestionWithAnswerOption.toDomainModel(): Question? {
    val questionEntity = this.question
    // Lấy đáp án đúng duy nhất (vì là Map Labeling tối ưu)
    val correctAnswerEntity = this.answerOptions.firstOrNull()

    // Chỉ map nếu có đáp án đúng
    return correctAnswerEntity?.let { answer ->
        Question(
            number = questionEntity.questionNumber,
            prompt = questionEntity.prompt,
            audioStartTimeMs = questionEntity.audioStartTimeMs,
            audioEndTimeMs = questionEntity.audioEndTimeMs,
            correctLabel = answer.optionText, // Lấy đáp án đúng
            // Giả sử chưa có audio riêng cho từng câu hỏi trong Entity
            // Nếu có thì thêm trường tương ứng vào QuestionEntity và map ở đây
            audioResourceName = null
        )
    }
}

// Hàm helper để parse JSON list string (có thể đặt ở utils)
private fun parseJsonStringList(json: String?): List<String> {
    return json?.let {
        try {
            val listType: Type = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(it, listType) ?: emptyList()
        } catch (e: Exception) {
            println("ERROR: Failed to parse commonAnswerPoolJson: $it - ${e.message}")
            emptyList() // Trả về list rỗng nếu lỗi parse
        }
    } ?: emptyList() // Trả về list rỗng nếu json là null
}