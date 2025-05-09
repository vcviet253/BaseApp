package com.example.mealplanner.core.audio

import kotlinx.coroutines.flow.StateFlow

interface SimpleAudioPlayer {
    // Flow để ViewModel lắng nghe trạng thái
    val playerStateFlow: StateFlow<AudioPlayerState>
    // Flow để ViewModel lắng nghe tiến trình (0.0f - 1.0f)
    val progressFlow: StateFlow<Float>

    // Chuẩn bị audio từ URL hoặc URI
    fun prepare(source: String)
    fun play()
    fun pause()
    fun seekTo(progress: Float) // Tiến trình từ 0.0f đến 1.0f
    fun release() // Giải phóng tài nguyên

}