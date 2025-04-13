package com.example.mealplanner.presentation.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.domain.usecase.GenerateDiaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val generateDiaryUseCase: GenerateDiaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState = _uiState.asStateFlow()

    fun onImagesSelected(base64Images: List<String>) {
        _uiState.update { state -> state.copy(images = base64Images) }
    }

    fun onPromptChanged(prompt: String) {
        _uiState.update { it.copy(prompt = prompt) }
    }

    fun generateDiary() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val result = generateDiaryUseCase(
                    images = _uiState.value.images,
                    prompt = _uiState.value.prompt
                )

                _uiState.update {
                    it.copy(
                        diaryText = result,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Lỗi không xác định",
                        isLoading = false
                    )
                }
            }
        }
    }
}