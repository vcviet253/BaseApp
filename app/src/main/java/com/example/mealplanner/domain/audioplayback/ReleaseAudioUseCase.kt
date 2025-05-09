package com.example.mealplanner.domain.audioplayback

import com.example.mealplanner.core.audio.SimpleAudioPlayer
import javax.inject.Inject

// --- Use Case Giải phóng Tài nguyên Audio ---
class ReleaseAudioUseCase @Inject constructor(
    private val audioPlayer: SimpleAudioPlayer
) {
    operator fun invoke() {
        // Có thể thêm logic dọn dẹp khác nếu cần
        audioPlayer.release()
    }
}