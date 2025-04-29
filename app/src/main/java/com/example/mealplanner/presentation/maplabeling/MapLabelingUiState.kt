package com.example.mealplanner.presentation.maplabeling

import com.example.mealplanner.core.audio.AudioPlayerState
import com.example.mealplanner.domain.maplabeling.model.Question


data class MapLabelingUiState(
    val isLoading: Boolean = false,
    val testTitle: String = "", // Tiêu đề bài test
    val imageUrl: String? = null, // URL/URI của ảnh bản đồ
    val currentQuestionNumber: Int = 1, // Câu hỏi đang hiển thị
    val totalQuestions: Int = 0, // Tổng số câu hỏi
    val questionList: List<Question> =  emptyList(), // Danh sách các câu hỏi
    val selectedAnswerForCurrentQuestion: String? = null, // Đáp án người dùng chọn cho câu hiện tại (A, B, C...)
    val answerPool: List<String> = emptyList(), // Danh sách các đáp án có thể chọn (A, B, C...)
    val audioState: AudioPlayerState = AudioPlayerState.IDLE, // Trạng thái của audio player
    val audioProgress: Float = 0f, // Tiến trình audio (0.0f - 1.0f)
    val canGoPrevious: Boolean = false, // Có thể quay lại câu trước?
    val canGoNext: Boolean = false, // Có thể đi tới câu sau?
    val errorMessage: String? = null // Thông báo lỗi (nếu có)
) {
}