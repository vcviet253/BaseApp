package com.example.mealplanner.domain.audio_playback

import com.example.mealplanner.core.audio.SimpleAudioPlayer
import javax.inject.Inject

// --- Use Case Tạm dừng Audio ---
class PauseAudioUseCase @Inject constructor(
    private val audioPlayer: SimpleAudioPlayer
) {
    operator fun invoke() {
        audioPlayer.pause()
    }
}