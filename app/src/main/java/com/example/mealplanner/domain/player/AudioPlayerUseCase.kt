package com.example.mealplanner.domain.player

import com.example.mealplanner.data.player.AudioPlayer
import javax.inject.Inject

class AudioPlayerUseCase @Inject constructor(
    private val audioPlayer: AudioPlayer
) {
    suspend fun playRecording() {
        audioPlayer.playRecording()
    }

    suspend fun stopPlayingRecording() {
        audioPlayer.stopPlayingRecording()
    }
}