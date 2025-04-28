package com.example.mealplanner.domain.recorder.usecase

import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import com.example.mealplanner.data.recorder.AudioRecorderManager
import dagger.hilt.android.UnstableApi
import javax.inject.Inject

@OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.S)
class RecordingUseCaseImpl @Inject constructor(
    private val audioRecorderManager: AudioRecorderManager
): RecordingUseCase {
    override suspend fun startRecording() {
        audioRecorderManager.startRecording()
    }

    override suspend fun stopRecording() {
        audioRecorderManager.stopRecording()
    }
}