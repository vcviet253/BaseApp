package com.example.mealplanner.data.recorder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "SpeechRecognizer"


class SpeechRecognizerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var speechRecognizer: SpeechRecognizer? = null

    fun listen(): Flow<SpeechRecognitionResult> = callbackFlow {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            close(Exception("Speech recognition is not available."))
            return@callbackFlow
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}

                override fun onPartialResults(partialResults: Bundle?) {
                    val text = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                    trySend(SpeechRecognitionResult.Partial(text))
                }

                override fun onResults(results: Bundle?) {
                    val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                    trySend(SpeechRecognitionResult.Final(text))
                }

                override fun onError(error: Int) {
                    trySend(SpeechRecognitionResult.Error(error))
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN") // Tiếng Việt
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000) // Cảm nhận ngắt câu nhanh hơn
        }

        speechRecognizer?.startListening(intent)

        awaitClose {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }
}

sealed class SpeechRecognitionResult {
    data class Partial(val text: String) : SpeechRecognitionResult()
    data class Final(val text: String) : SpeechRecognitionResult()
    data class Error(val errorCode: Int) : SpeechRecognitionResult()
}