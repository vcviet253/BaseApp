package com.example.mealplanner.domain.audioplayback

import com.example.mealplanner.core.audio.SimpleAudioPlayer
import javax.inject.Inject

class PlayAudioUseCase @Inject constructor(
    private val audioPlayer: SimpleAudioPlayer
){
    operator fun invoke() {
        audioPlayer.play()
    }
}