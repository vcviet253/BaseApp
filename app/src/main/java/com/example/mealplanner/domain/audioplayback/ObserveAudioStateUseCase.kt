package com.example.mealplanner.domain.audioplayback

import com.example.mealplanner.core.audio.AudioPlayerState
import com.example.mealplanner.core.audio.SimpleAudioPlayer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// --- Use Case Lắng nghe Trạng thái Audio ---
class ObserveAudioStateUseCase @Inject constructor(
    private val audioPlayer: SimpleAudioPlayer
) {
    operator fun invoke(): Flow<AudioPlayerState> {
        return audioPlayer.playerStateFlow
    }
}