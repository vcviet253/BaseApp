package com.example.mealplanner.data.player

import android.content.Context
import android.media.MediaPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AudioPlayer @Inject constructor(@ApplicationContext private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    suspend fun playRecording(file: File = File(context.filesDir, "audio_recording.m4a")) {
        withContext(Dispatchers.IO) {
            if (!file.exists()) {
                // Handle file not found
                return@withContext
            }

            try {
                mediaPlayer?.release() // Release previous if any
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare() // prepareAsync() nếu muốn chơi background
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Optional: log or handle playback errors here
            }
        }
    }

    suspend fun stopPlayingRecording() {
        withContext(Dispatchers.IO) {
            try {
                mediaPlayer?.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }
    }
}