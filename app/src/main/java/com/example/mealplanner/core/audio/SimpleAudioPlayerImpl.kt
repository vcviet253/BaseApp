package com.example.mealplanner.core.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.work.impl.utils.isDefaultProcess
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimpleAudioPlayerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SimpleAudioPlayer, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {
    private var mediaPlayer: MediaPlayer? = null
    private val _playerState = MutableStateFlow(AudioPlayerState.IDLE)
    override val playerStateFlow: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    override val progressFlow: StateFlow<Float> = _progress.asStateFlow()

    private var progressJob: Job? = null
    private val scope =
        CoroutineScope(Dispatchers.Main + SupervisorJob()) // Dùng Main để cập nhật UI state an toàn

    override fun prepare(source: String) {
        release()
        _playerState.value = AudioPlayerState.BUFFERING
        _progress.value = 0f
        try {
            mediaPlayer = MediaPlayer().apply {
                //mediaPlayer.setDataSource(source: String): Phương thức này của MediaPlayer được thiết kế để nhận một chuỗi là:
                //
                //Đường dẫn đầy đủ đến một file trên hệ thống (/sdcard/myfile.mp3).
                //Một URL HTTP/HTTPS (https://example.com/audio.mp3).
                //Để MediaPlayer (hoặc ExoPlayer) có thể đọc file từ res/raw, bạn phải dùng một phương thức setDataSource khác, cụ thể là setDataSource(context: Context, uri: Uri).
                //
                //Bạn cần có Context để truy cập tài nguyên ứng dụng.
                //Bạn cần chuyển đổi chuỗi "android.resource://..." (mà ViewModel tạo ra) thành đối tượng Uri bằng cách dùng Uri.parse(source).
                setDataSource(context, Uri.parse(source))
                setOnPreparedListener(this@SimpleAudioPlayerImpl)
                setOnErrorListener(this@SimpleAudioPlayerImpl)
                setOnCompletionListener(this@SimpleAudioPlayerImpl)
                prepareAsync()
            }
        } catch (e: IOException) {
            _playerState.value = AudioPlayerState.ERROR
        } catch (e: IllegalArgumentException) {
            _playerState.value = AudioPlayerState.ERROR
        }
    }

    override fun onPrepared(p0: MediaPlayer?) {
        _playerState.value = AudioPlayerState.IDLE // Sẵn sàng để play
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        _playerState.value = AudioPlayerState.ERROR
        stopProgressUpdates()
        release() // Giải phóng khi có lỗi
        return true //Da xu li loi
    }

    override fun onCompletion(p0: MediaPlayer?) {
        _playerState.value = AudioPlayerState.COMPLETED
        _progress.value = 1f
        stopProgressUpdates()
        // Có thể reset về IDLE và progress 0f nếu muốn tua lại từ đầu khi nhấn play
        // _playerState.value = AudioPlayerState.IDLE
        // _progress.value = 0f
        // mp?.seekTo(0)
    }

    override fun play() {
        if (_playerState.value == AudioPlayerState.IDLE || _playerState.value == AudioPlayerState.PAUSED || _playerState.value == AudioPlayerState.COMPLETED) {
            mediaPlayer?.start()
            _playerState.value = AudioPlayerState.PLAYING
            startProgressUpdates()
        }
    }

    override fun pause() {
        if (_playerState.value == AudioPlayerState.PLAYING) {
            mediaPlayer?.pause()
            _playerState.value = AudioPlayerState.PAUSED
            stopProgressUpdates()
        }
    }

    override fun seekTo(progress: Float) {
        if (mediaPlayer != null && (_playerState.value == AudioPlayerState.PLAYING || _playerState.value == AudioPlayerState.PAUSED || _playerState.value == AudioPlayerState.IDLE || _playerState.value == AudioPlayerState.COMPLETED)) {
            try {
                val duration = mediaPlayer?.duration ?: 0
                if (duration > 0) {
                    val newPosition = (duration * progress).toInt()
                    mediaPlayer?.seekTo(newPosition)
                    _progress.value =
                        progress // Cập nhật progress ngay lập tức (có thể cần đợi seek hoàn tất)
                }
            } catch (e: IllegalStateException) {
                // Handle error
            }
        }
    }

    override fun release() {
        stopProgressUpdates()
        scope.coroutineContext.cancelChildren() // Hủy coroutine chạy progress
        mediaPlayer?.release()
        mediaPlayer = null
        _playerState.value = AudioPlayerState.IDLE
        _progress.value = 0f
    }

    private fun startProgressUpdates() {
        stopProgressUpdates() // Đảm bảo job cũ đã dừng
        progressJob = scope.launch {
            while (isActive && mediaPlayer != null && mediaPlayer!!.isPlaying) {
                try {
                    val currentPosition = mediaPlayer?.currentPosition ?: 0
                    val duration = mediaPlayer?.duration ?: 1 // Tránh chia cho 0
                    if (duration > 0) {
                        _progress.value = currentPosition.toFloat() / duration.toFloat()
                    }
                } catch (e: IllegalStateException) {
                    // Player có thể đã bị release
                    break // Thoát vòng lặp
                }
                delay(250) // Cập nhật tiến trình mỗi 250ms
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }
}