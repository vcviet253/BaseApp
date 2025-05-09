package com.example.mealplanner.data.recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import dagger.hilt.android.UnstableApi
import java.io.File
import androidx.media3.session.MediaSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject

private const val TAG = "AudioRecorderManager"

@UnstableApi
class AudioRecorderManager @Inject constructor(@ApplicationContext private val context: Context) {
    private var recorder: MediaRecorder? = null

    private val outputFile: File = File(context.filesDir, "audio_recording.m4a")

    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun startRecording() {
        withContext(Dispatchers.IO) {
            try {
                recorder = MediaRecorder(context).apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(outputFile.absolutePath)
                    prepare()
                    Log.d(TAG, "Recorder prepared.")
                    start()
                    Log.d(TAG, "Recorder started.")
                    Log.d(TAG, "Output file path: ${outputFile.absolutePath}")
                }
            } catch (e: Exception) {
                Log.d(TAG, e.localizedMessage)
            }
        }
    }

    suspend fun stopRecording() {
        withContext(Dispatchers.IO) {
            try {
                recorder?.stop()
            } catch (e: Exception) {
                e.printStackTrace() // or log the error
            } finally {
                recorder?.release()
                recorder = null
            }
        }

    }
}