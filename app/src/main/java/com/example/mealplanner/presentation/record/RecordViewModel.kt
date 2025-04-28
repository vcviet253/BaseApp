package com.example.mealplanner.presentation.record

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.data.recorder.SpeechRecognitionResult
import com.example.mealplanner.domain.player.AudioPlayerUseCase
import com.example.mealplanner.domain.recorder.usecase.RecordingUseCase
import com.example.mealplanner.domain.recorder.usecase.SpeechRecognitionUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordingUseCase: RecordingUseCase,
    private val audioPlayerUseCase: AudioPlayerUseCase,
    private val speechRecognizerUseCase: SpeechRecognitionUseCase,
) : ViewModel() {
    private val _recordUiState = MutableStateFlow(RecordUiState())
    val recordUiState = _recordUiState.asStateFlow()

    private var listeningJob: Job? = null

    fun startListening() {
        if (listeningJob != null) return

        listeningJob = viewModelScope.launch {
            speechRecognizerUseCase.listen().collectLatest { result ->
                when (result) {
                    is SpeechRecognitionResult.Partial -> {
                        _recordUiState.value = _recordUiState.value.copy(
                            liveText = result.text,
                            error = null
                        )
                    }

                    is SpeechRecognitionResult.Final -> {
                        // Khi final text xong thì cộng dồn vào history
                        val updatedHistory = _recordUiState.value.historyText + result.text + " "
                        _recordUiState.value = _recordUiState.value.copy(
                            historyText = updatedHistory,
                            liveText = "",
                            error = null
                        )
                        restartListening()
                    }

                    is SpeechRecognitionResult.Error -> {
                        _recordUiState.value = _recordUiState.value.copy(
                            error = "Error: ${result.errorCode}"
                        )
                        restartListening()
                    }
                }
            }
        }
    }

    fun stopListening() {
        listeningJob?.cancel()
        listeningJob = null
    }

    private fun restartListening() {
        stopListening()
        startListening()
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }

    fun startRecording() {
        _recordUiState.update { state ->
            state.copy(
                isRecording = true,
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            recordingUseCase.startRecording()
        }
    }

    fun stopRecording() {
        viewModelScope.launch(Dispatchers.IO) {
            recordingUseCase.stopRecording()

            _recordUiState.update { state ->
                state.copy(
                    isRecording = false,
                )
            }
        }
    }

    fun playAudio(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            audioPlayerUseCase.playRecording()
        }
    }

    fun stopAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            audioPlayerUseCase.stopPlayingRecording()
        }
    }
}