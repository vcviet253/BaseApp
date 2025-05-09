package com.example.mealplanner.domain.audioplayback

import com.example.mealplanner.core.audio.SimpleAudioPlayer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// --- Use Case Lắng nghe Tiến trình Audio ---
class ObserveAudioProgressUseCase @Inject constructor(
    private val audioPlayer: SimpleAudioPlayer
) {
    operator fun invoke(): Flow<Float> {
        return audioPlayer.progressFlow
    }
}
