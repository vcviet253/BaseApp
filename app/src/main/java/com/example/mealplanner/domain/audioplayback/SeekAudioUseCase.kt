package com.example.mealplanner.domain.audioplayback

import com.example.mealplanner.core.audio.SimpleAudioPlayer
import javax.inject.Inject

// --- Use Case Tua Audio ---
class SeekAudioUseCase @Inject constructor(
    private val audioPlayer: SimpleAudioPlayer
) {
    operator fun invoke(progress: Float) {
        // Có thể kiểm tra giá trị progress hợp lệ (0.0f - 1.0f)
        audioPlayer.seekTo(progress)
    }
}