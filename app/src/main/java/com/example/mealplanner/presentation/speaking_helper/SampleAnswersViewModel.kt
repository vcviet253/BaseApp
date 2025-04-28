package com.example.mealplanner.presentation.speaking_helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.domain.usecase.GetModelAnswerForQuestionAndBandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SampleAnswersViewModel @Inject constructor(
    private val getModelAnswerForQuestionAndBandUseCase: GetModelAnswerForQuestionAndBandUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SampleAnswersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTopics()
    }

    private fun loadTopics() {
        _uiState.update { state ->
            state.copy(
                topics = listOf(
                    "Health",
                    "Technology",
                    "Environment",
                    "Education",
                    "Travel",
                    "Food",
                    "Sports",
                    "Fashion",
                    "Music",
                    "Art"
                )
            )
        }
    }

    fun selectTopic(topic: String) {
        _uiState.value = _uiState.value.copy(
            selectedTopic = topic
        )
        fetchRandomQuestion()
    }

    fun fetchRandomQuestion() {
        // Fetch a random question based on the selected topic
        _uiState.value = _uiState.value.copy(
            questionText = "Do you like your job?"
        )
    }

    fun selectBand(band: Float) {
        _uiState.value = _uiState.value.copy(
            selectedBand = band
        )

    }

    fun fetchModelAnswer(question: String, band: Float) {

        viewModelScope.launch(Dispatchers.IO) {
            val prompt = buildPrompt(question, band)
            // Fetch the model answer for the selected topic and target band

            _uiState.update { it.copy(isLoading = true) }
            try {
                val answer = getModelAnswerForQuestionAndBandUseCase(prompt) // Suspend function
                _uiState.value = _uiState.value.copy(
                    modelAnswer = answer
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }


    }

    // Helper function to build the prompt
    private fun buildPrompt(question: String, band: Float): String {
        return "You are an IELTS examiner. You will now ask the candidate a question from Part 1 of the IELTS Speaking test. The question is about the candidate's daily routine.\n" +
                "\n" +
                "Question: \"${question}\"\n" +
                "\n" +
                "Please answer the question as if you were a candidate in the IELTS Speaking test. Your response should be natural and in line with the following IELTS Band Descriptors:\n" +
                "\n" +
                "- Fluency and Coherence: The answer should be fluid with minimal hesitations and clear, logical progression.\n" +
                "- Lexical Resource: Use a range of vocabulary appropriate for the task, avoiding repetition.\n" +
                "- Grammatical Range and Accuracy: The answer should have a range of grammatical structures and be accurate in terms of syntax and tenses.\n" +
                "\n" +
                "Target Band: ${band}\n" +
                "\n" +
                "Please provide a model response that would meet the expectations for this band. In your answer, just keep the body part. Do not include anything like" +
                "\"I understand\" or \"I got it\" or any title/header. Just simply show the body of the answer."
    }

    fun updateTopicMenuExpanded() {
        _uiState.update { state ->
            state.copy(
                isTopicMenuExpanded = !state.isTopicMenuExpanded
            )
        }
    }

    fun updateBandMenuExpanded() {
        _uiState.update { state ->
            state.copy(
                isBandMenuExpanded = !state.isBandMenuExpanded
            )
        }    }
}

