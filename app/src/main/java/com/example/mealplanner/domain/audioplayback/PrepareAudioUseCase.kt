package com.example.mealplanner.domain.audioplayback

import com.example.mealplanner.core.audio.SimpleAudioPlayer
import javax.inject.Inject

class PrepareAudioUseCase @Inject constructor(
    private val audioPlayer: SimpleAudioPlayer
){
  operator fun invoke(source: String) {
    audioPlayer.prepare(source)
  }
}