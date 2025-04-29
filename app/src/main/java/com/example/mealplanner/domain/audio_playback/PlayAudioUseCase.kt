package com.example.mealplanner.domain.audio_playback

import com.example.mealplanner.core.audio.SimpleAudioPlayer
import javax.inject.Inject

class PlayAudioUseCase @Inject constructor(
    private val audioPlayer: SimpleAudioPlayer
){
    operator fun invoke() {
        audioPlayer.play()
    }
}