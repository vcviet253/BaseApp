package com.example.mealplanner.presentation.listening_map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanner.R
import com.example.mealplanner.core.audio.AudioPlayerState
import com.example.mealplanner.domain.audio_playback.ObserveAudioProgressUseCase
import com.example.mealplanner.domain.audio_playback.ObserveAudioStateUseCase
import com.example.mealplanner.domain.audio_playback.PauseAudioUseCase
import com.example.mealplanner.domain.audio_playback.PlayAudioUseCase
import com.example.mealplanner.domain.audio_playback.PrepareAudioUseCase
import com.example.mealplanner.domain.audio_playback.ReleaseAudioUseCase
import com.example.mealplanner.domain.audio_playback.SeekAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapLabelingViewModel @Inject constructor(
    private val observeAudioStateUseCase: ObserveAudioStateUseCase,
    private val observeAudioProgressUseCase: ObserveAudioProgressUseCase,
    private val prepareAudioUseCase: PrepareAudioUseCase,
    private val playAudioUseCase: PlayAudioUseCase,
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val seekAudioUseCase: SeekAudioUseCase,
    private val releaseAudioUseCase: ReleaseAudioUseCase,
    @ApplicationContext private val context: Context
): ViewModel() {
    private val _uiState = MutableStateFlow(MapLabelingUiState())
    val uiState = _uiState.asStateFlow()

    // Vẫn cần theo dõi state hiện tại để quyết định play hay pause
    private var currentPlayerState: AudioPlayerState = AudioPlayerState.IDLE

    init {
        // Lắng nghe trạng thái và tiến trình qua Use Cases
        viewModelScope.launch {
            observeAudioStateUseCase().collect { playerState ->
                currentPlayerState = playerState // Cập nhật trạng thái nội bộ
                _uiState.update { it.copy(audioState = playerState) }
            }
        }
        viewModelScope.launch {
            observeAudioProgressUseCase().collect { progress ->
                _uiState.update { it.copy(audioProgress = progress) }
            }
        }

        loadTestData(1L) // Ví dụ testId
    }

    fun loadTestData(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true)}
            delay(300)
            val audioUrl  = "android.resource://${context.packageName}/${R.raw.ielts1}"
            prepareAudioUseCase(audioUrl)

            _uiState.update { it.copy(isLoading = false)}
        }
    }

    // Các hàm điều khiển audio gọi Use Cases tương ứng
    fun togglePlayPause() {
        when (currentPlayerState) {
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
        if (currentPlayerState != AudioPlayerState.ERROR && currentPlayerState != AudioPlayerState.BUFFERING) {
            viewModelScope.launch { seekAudioUseCase(progress) }
        }
    }

    // Giải phóng tài nguyên khi ViewModel bị hủy qua Use Case
    override fun onCleared() {
        super.onCleared()
        // Không cần viewModelScope ở đây
        releaseAudioUseCase()
    }

}