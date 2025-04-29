package com.example.mealplanner.presentation.maplabeling

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.R
import com.example.mealplanner.common.Resource
import com.example.mealplanner.core.audio.AudioPlayerState
import com.example.mealplanner.domain.audioplayback.ObserveAudioProgressUseCase
import com.example.mealplanner.domain.audioplayback.ObserveAudioStateUseCase
import com.example.mealplanner.domain.audioplayback.PauseAudioUseCase
import com.example.mealplanner.domain.audioplayback.PlayAudioUseCase
import com.example.mealplanner.domain.audioplayback.PrepareAudioUseCase
import com.example.mealplanner.domain.audioplayback.ReleaseAudioUseCase
import com.example.mealplanner.domain.audioplayback.SeekAudioUseCase
import com.example.mealplanner.domain.maplabeling.model.MapLabelingTestData
import com.example.mealplanner.domain.maplabeling.usecase.GetMapLabelingTestDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MapLabelingViewModel"

@HiltViewModel
class MapLabelingViewModel @Inject constructor(
    private val getMapLabelingTestDataUseCase: GetMapLabelingTestDataUseCase,
    private val observeAudioStateUseCase: ObserveAudioStateUseCase,
    private val observeAudioProgressUseCase: ObserveAudioProgressUseCase,
    private val prepareAudioUseCase: PrepareAudioUseCase,
    private val playAudioUseCase: PlayAudioUseCase,
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val seekAudioUseCase: SeekAudioUseCase,
    private val releaseAudioUseCase: ReleaseAudioUseCase,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapLabelingUiState())
    val uiState = _uiState.asStateFlow()

    // Store fetched data locally to access question details easily
    private var currentTestData: MapLabelingTestData? = null

    // Vẫn cần theo dõi state hiện tại để quyết định play hay pause
    private var currentAudioState: AudioPlayerState = AudioPlayerState.IDLE

    // Store user answers temporarily if needed (or handle via DB/UseCase)
    private val userAnswers =
        mutableMapOf<Int, String>() // Key: questionNumber, Value: selectedLabel

    init {
        observeAudioUpdates()
        // Assuming testId is passed via navigation arguments
        val testId: Long? = savedStateHandle.get<Long>("testId") // Match argument name
        if (testId != null) {
            loadTestData(testId)
        } else {
            // Handle error: testId not provided
            _uiState.update { it.copy(isLoading = false, errorMessage = "Test ID không hợp lệ.") }
        }
    }

    private fun observeAudioUpdates() {
        viewModelScope.launch {
            observeAudioStateUseCase().collect { state ->
                currentAudioState = state // Keep track locally for toggle logic
                _uiState.update { it.copy(audioState = state) }
            }
        }
        viewModelScope.launch {
            observeAudioProgressUseCase().collect { progress ->
                // Avoid updating progress if user is actively seeking (requires more state)
                _uiState.update { it.copy(audioProgress = progress) }
            }
        }
    }

    fun loadTestData(testId: Long) {
        // Prevent concurrent loading if already loading
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            getMapLabelingTestDataUseCase(testId)
                .catch { e ->
                    // Catch unexpected errors during flow collection
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Lỗi không xác định: ${e.message}"
                        )
                    }
                }
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val testData = result.data
                            currentTestData = testData // Store fetched data

                            // Determine initial audio source (e.g., main test audio)
                            prepareAudioBySource(
                                testData.testInfo.audioSource,
                                testData.testInfo.id
                            ) // Pass ID for resource lookup if needed
                            Log.d(TAG, testData.testInfo.audioSource)
                            Log.d(TAG, testData.testInfo.answerPool.toString())

                            _uiState.update { currentState ->
                                val totalQuestions = testData.questions.size
                                val currentQuestionNumber = 1 // Start at question 1
                                currentState.copy(
                                    isLoading = false,
                                    testTitle = testData.testInfo.title,
                                    imageUrl = testData.testInfo.imageUrl,
                                    totalQuestions = totalQuestions,
                                    answerPool = testData.testInfo.answerPool,
                                    currentQuestionNumber = currentQuestionNumber,
                                    selectedAnswerForCurrentQuestion = userAnswers[currentQuestionNumber], // Load saved answer if any
                                    canGoPrevious = false, // Can't go back from first question
                                    canGoNext = totalQuestions > 1, // Can go next if more than 1 question
                                    errorMessage = null
                                )
                            }
                        }

                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                        ?: "Không thể tải dữ liệu bài test."
                                )
                            }
                        }

                        is Resource.Loading -> _uiState.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        } // Reset error on new load
                    }
                }
        }
    }

    fun onAnswerSelected(answerLabel: String) {
        val currentQuestionNum = _uiState.value.currentQuestionNumber
        userAnswers[currentQuestionNum] = answerLabel // Store answer temporarily
        _uiState.update { it.copy(selectedAnswerForCurrentQuestion = answerLabel) }
        // Optionally call a use case to save progress persistently
        // viewModelScope.launch { saveAnswerUseCase(...) }
    }

    // Các hàm điều khiển audio gọi Use Cases tương ứng
    fun togglePlayPause() {
        when (currentAudioState) {
            AudioPlayerState.IDLE, AudioPlayerState.PAUSED, AudioPlayerState.COMPLETED -> {
                viewModelScope.launch { playAudioUseCase() }
            }

            AudioPlayerState.PLAYING -> {
                viewModelScope.launch { pauseAudioUseCase() }
            }

            else -> {}
        }
    }

    fun seekAudio(progress: Float) {
        if (currentAudioState != AudioPlayerState.ERROR && currentAudioState != AudioPlayerState.BUFFERING) {
            viewModelScope.launch { seekAudioUseCase(progress) }
        }
    }

    // Helper to handle preparing audio from different source types (URL or res/raw name)
    private fun prepareAudioBySource(
        sourceIdentifier: String? = "ielts1",
        testIdForResourceLookup: Long
    ) {
        if (sourceIdentifier.isNullOrBlank()) {
            println("WARN: No audio source identifier provided for test $testIdForResourceLookup.")
            // Maybe release player if no audio is expected?
            // viewModelScope.launch { releaseAudioUseCase() }
            _uiState.update { it.copy(audioState = AudioPlayerState.IDLE, audioProgress = 0f) }
            return
        }

        // Basic check: is it likely a URL or a resource name?
        if (sourceIdentifier.startsWith("http://") || sourceIdentifier.startsWith("https://") || sourceIdentifier.startsWith(
                "content://"
            )
        ) {
            // Assume it's a direct URL or content URI
            viewModelScope.launch { prepareAudioUseCase(sourceIdentifier) }
        } else {
            // Assume it's a resource name from res/raw
            try {
                val resourceId = context.resources.getIdentifier(
                    sourceIdentifier, // The resource name (e.g., "test_audio_1")
                    "raw",
                    context.packageName
                )
                Log.d(TAG, resourceId.toString())

                if (resourceId == 0) {
                    println("ERROR: Raw resource not found: $sourceIdentifier")
                    _uiState.update { it.copy(errorMessage = "Không tìm thấy file audio: $sourceIdentifier") }
                    return
                }

                val uriString = "android.resource://${context.packageName}/$resourceId"
                println("INFO: Preparing raw resource: $uriString")
                viewModelScope.launch { prepareAudioUseCase(uriString) }

            } catch (e: Resources.NotFoundException) {
                println("ERROR: Raw resource lookup failed: $sourceIdentifier - ${e.message}")
                _uiState.update { it.copy(errorMessage = "Lỗi tìm file audio: $sourceIdentifier") }
            } catch (e: Exception) {
                println("ERROR: Failed to prepare raw resource '$sourceIdentifier': ${e.message}")
                _uiState.update { it.copy(errorMessage = "Lỗi chuẩn bị audio resource: ${e.message}") }
            }
        }
    }

    // Giải phóng tài nguyên khi ViewModel bị hủy qua Use Case
    override fun onCleared() {
        super.onCleared()
        // Không cần viewModelScope ở đây
        releaseAudioUseCase()
    }

}