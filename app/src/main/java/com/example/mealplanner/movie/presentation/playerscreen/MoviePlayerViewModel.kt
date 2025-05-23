package com.example.mealplanner.movie.presentation.playerscreen

//import android.content.Context
//import android.util.Log
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.media3.common.MediaItem
//import androidx.media3.common.Player
//import androidx.media3.exoplayer.ExoPlayer
//import com.example.mealplanner.movie.presentation.navigation.MovieAppDestinations
//import dagger.hilt.android.lifecycle.HiltViewModel
//import dagger.hilt.android.qualifiers.ApplicationContext
//import kotlinx.coroutines.Dispatchers // Import này là cần thiết
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch // Import này là cần thiết
//import kotlinx.coroutines.withContext // Import này là cần thiết
//import java.net.URLDecoder
//import javax.inject.Inject
//
//@HiltViewModel
//class MoviePlayerViewModel @Inject constructor(
//    @ApplicationContext private val application: Context,
//    savedStateHandle: SavedStateHandle // SavedStateHandle sẽ được inject bởi Hilt
//) : ViewModel() {
//
//    // movieUrl StateFlow này vẫn có thể dùng để quan sát trong Composable
//    val movieUrl: StateFlow<String> =
//        savedStateHandle.getStateFlow(MovieAppDestinations.MOVIE_PLAYER_ARG_URL, "")
//            .map { encodedUrl ->
//                URLDecoder.decode(encodedUrl, "UTF-8")
//            }.stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(5000),
//                initialValue = ""
//            )
//
//    private var _player: ExoPlayer? = null
//    val playerInstance: ExoPlayer? get() = _player
//
//    private val _isPlayerReady = MutableStateFlow(false)
//    val isPlayerReady: StateFlow<Boolean> = _isPlayerReady.asStateFlow()
//
//    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
//    val playbackState: StateFlow<Int> = _playbackState.asStateFlow()
//
//    init {
//        val initialEncodedUrl: String? = savedStateHandle[MovieAppDestinations.MOVIE_PLAYER_ARG_URL]
//
//        val decodedUrl: String = if (!initialEncodedUrl.isNullOrBlank()) {
//            try {
//                URLDecoder.decode(initialEncodedUrl, "UTF-8")
//            } catch (e: Exception) {
//                Log.e("MoviePlayerVM", "Error decoding URL: ${e.message}", e)
//                ""
//            }
//        } else {
//            ""
//        }
//
//        if (decodedUrl.isNotBlank()) {
//            Log.d("MoviePlayerVM", "Initializing player with URL: $decodedUrl on Main Thread.")
//            // Gọi initializePlayer TRỰC TIẾP trên Main Thread.
//            // KHÔNG BAO BỌC NÓ TRONG launch(Dispatchers.Default)
//            initializePlayer(decodedUrl)
//        } else {
//            Log.e("MoviePlayerVM", "Movie URL is blank or invalid. Player not initialized.")
//            _isPlayerReady.value = false
//        }
//    }
//
//    // Bỏ từ khóa 'suspend' và 'withContext'
//    private fun initializePlayer(url: String) {
//        try {
//            _player = ExoPlayer.Builder(application).build().apply {
//                setMediaItem(MediaItem.fromUri(url))
//                addListener(object : Player.Listener {
//                    override fun onIsPlayingChanged(isPlaying: Boolean) {
//                        // Các callback này thường được gọi trên Main Thread bởi ExoPlayer
//                        // và việc cập nhật StateFlow trên Main Thread là an toàn.
//                    }
//
//                    override fun onPlaybackStateChanged(playbackState: Int) {
//                        _playbackState.value = playbackState
//                        if (playbackState == Player.STATE_READY) {
//                            _isPlayerReady.value = true
//                            playWhenReady = true // Tự động phát khi sẵn sàng
//                            Log.d("MoviePlayerVM", "Player is ready and playing.")
//                        } else if (playbackState == Player.STATE_ENDED) {
//                            Log.d("MoviePlayerVM", "Playback ended.")
//                        }
//                    }
//
//                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
//                        super.onPlayerError(error)
//                        Log.e("MoviePlayerVM", "Player Error: ${error.message}", error)
//                        _isPlayerReady.value = false
//                        _player?.release()
//                        _player = null
//                    }
//                })
//                prepare() // prepare() của ExoPlayer sẽ tự động thực hiện I/O ở background
//            }
//        } catch (e: Exception) {
//            Log.e("MoviePlayerVM", "Error initializing player: ${e.message}", e)
//            _isPlayerReady.value = false
//            _player?.release()
//            _player = null
//        }
//    }
//
//    fun play() {
//        _player?.playWhenReady = true
//    }
//
//    fun pause() {
//        _player?.playWhenReady = false
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        Log.d("MoviePlayerVM", "onCleared called, releasing player.")
//        _player?.release()
//        _player = null
//    }
//}

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.mealplanner.movie.presentation.navigation.MovieAppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class MoviePlayerViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val movieUrl: StateFlow<String> =
        savedStateHandle.getStateFlow(MovieAppDestinations.MOVIE_PLAYER_ARG_URL, "")
            .map { encodedUrl -> URLDecoder.decode(encodedUrl, "UTF-8") }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ""
            )

    private var _player: ExoPlayer? = null
    val playerInstance: ExoPlayer? get() = _player

    private val _isPlayerReady = MutableStateFlow(false)
    val isPlayerReady: StateFlow<Boolean> = _isPlayerReady.asStateFlow()

    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    val playbackState: StateFlow<Int> = _playbackState.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _bufferedPosition = MutableStateFlow(0L)
    val bufferedPosition: StateFlow<Long> = _bufferedPosition.asStateFlow()

    // --- THÊM CÁC STATE VÀ HÀM CHO CHỨC NĂNG TỐC ĐỘ PHÁT ---
    private val _playbackSpeed = MutableStateFlow(1.0f) // Tốc độ mặc định là 1.0x
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    val availableSpeeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f) // Các tốc độ có sẵn

    fun setPlaybackSpeed(speed: Float) {
        if (speed <= 0) { // Không cho phép tốc độ âm hoặc bằng 0
            Log.w("MoviePlayerVM", "Attempted to set invalid playback speed: $speed")
            return
        }
        _player?.setPlaybackSpeed(speed)
        _playbackSpeed.value = speed
        Log.d("MoviePlayerVM", "Playback speed set to: ${speed}x")
    }
    // --- KẾT THÚC PHẦN THÊM TỐC ĐỘ PHÁT ---

    // --- THÊM CÁC STATE VÀ HÀM CHO CHỨC NĂNG ÂM LƯỢNG ---
    private val _volume = MutableStateFlow(1.0f) // Âm lượng mặc định là 1.0 (max)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    fun setVolume(vol: Float) {
        val newVolume = vol.coerceIn(0f, 1.0f) // Đảm bảo âm lượng trong khoảng 0.0f đến 1.0f
        _player?.volume = newVolume
        _volume.value = newVolume
        Log.d("MoviePlayerVM", "Volume set to: $newVolume")
    }

    fun increaseVolume(step: Float = 0.1f) {
        setVolume(_volume.value + step)
    }

    fun decreaseVolume(step: Float = 0.1f) {
        setVolume(_volume.value - step)
    }
    // --- KẾT THÚC PHẦN THÊM ÂM LƯỢNG ---


    private var positionUpdateJob: Job? = null

    init {
        val initialEncodedUrl: String? = savedStateHandle[MovieAppDestinations.MOVIE_PLAYER_ARG_URL]
        val decodedUrl: String = if (!initialEncodedUrl.isNullOrBlank()) {
            try { URLDecoder.decode(initialEncodedUrl, "UTF-8") } catch (e: Exception) { Log.e("MoviePlayerVM", "Error decoding URL: ${e.message}", e); "" }
        } else { "" }

        if (decodedUrl.isNotBlank()) {
            Log.d("MoviePlayerVM", "Initializing player with URL: $decodedUrl on Main Thread.")
            initializePlayer(decodedUrl)
        } else {
            Log.e("MoviePlayerVM", "Movie URL is blank or invalid. Player not initialized.")
            _isPlayerReady.value = false
        }
    }

    private fun initializePlayer(url: String) {
        try {
            _player = ExoPlayer.Builder(application).build().apply {
                setMediaItem(MediaItem.fromUri(url))
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                        if (isPlaying) {
                            startPositionUpdates()
                        } else {
                            stopPositionUpdates()
                        }
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        _playbackState.value = playbackState
                        if (playbackState == Player.STATE_READY) {
                            _isPlayerReady.value = true
                            _duration.value = duration
                            playWhenReady = true
                            Log.d("MoviePlayerVM", "Player is ready and playing.")
                            // Đảm bảo tốc độ được đặt ban đầu nếu player đã sẵn sàng
                            setPlaybackSpeed(_playbackSpeed.value)
                        } else if (playbackState == Player.STATE_ENDED) {
                            Log.d("MoviePlayerVM", "Playback ended.")
                            _isPlaying.value = false
                            _currentPosition.value = duration
                            stopPositionUpdates()
                        }
                    }

                    override fun onPositionDiscontinuity(
                        oldPosition: Player.PositionInfo,
                        newPosition: Player.PositionInfo,
                        reason: Int
                    ) {
                        _currentPosition.value = newPosition.positionMs
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        super.onPlayerError(error)
                        Log.e("MoviePlayerVM", "Player Error: ${error.message}", error)
                        _isPlayerReady.value = false
                        _player?.release()
                        _player = null
                        _isPlaying.value = false
                        stopPositionUpdates()
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        _duration.value = _player?.duration ?: 0L
                    }

                    override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
                        _duration.value = _player?.duration ?: 0L
                    }

                     fun onBufferedPositionChanged(bufferedPositionMs: Long) {
                        _bufferedPosition.value = bufferedPositionMs
                    }
                })
                prepare()
            }
        } catch (e: Exception) {
            Log.e("MoviePlayerVM", "Error initializing player: ${e.message}", e)
            _isPlayerReady.value = false
            _player?.release()
            _player = null
            _isPlaying.value = false
            stopPositionUpdates()
        }
    }

    fun play() { _player?.play() }
    fun pause() { _player?.pause() }
    fun seekTo(positionMs: Long) { _player?.seekTo(positionMs) }
    fun seekForward(milliseconds: Long = 10000L) { _player?.let { val newPosition = (it.currentPosition + milliseconds).coerceAtMost(it.duration); it.seekTo(newPosition) } }
    fun seekRewind(milliseconds: Long = 10000L) { _player?.let { val newPosition = (it.currentPosition - milliseconds).coerceAtLeast(0L); it.seekTo(newPosition) } }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch {
            while (true) {
                _currentPosition.value = _player?.currentPosition ?: 0L
                _bufferedPosition.value = _player?.bufferedPosition ?: 0L
                kotlinx.coroutines.delay(1000L)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MoviePlayerVM", "onCleared called, releasing player.")
        stopPositionUpdates()
        _player?.release()
        _player = null
    }
}