package com.example.mealplanner.movie.presentation.playerscreen

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
import kotlinx.coroutines.Dispatchers // Import này là cần thiết
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch // Import này là cần thiết
import kotlinx.coroutines.withContext // Import này là cần thiết
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class MoviePlayerViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    savedStateHandle: SavedStateHandle // SavedStateHandle sẽ được inject bởi Hilt
) : ViewModel() {

    // movieUrl StateFlow này vẫn có thể dùng để quan sát trong Composable
    val movieUrl: StateFlow<String> =
        savedStateHandle.getStateFlow(MovieAppDestinations.MOVIE_PLAYER_ARG_URL, "")
            .map { encodedUrl ->
                URLDecoder.decode(encodedUrl, "UTF-8")
            }.stateIn(
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

    init {
        val initialEncodedUrl: String? = savedStateHandle[MovieAppDestinations.MOVIE_PLAYER_ARG_URL]

        val decodedUrl: String = if (!initialEncodedUrl.isNullOrBlank()) {
            try {
                URLDecoder.decode(initialEncodedUrl, "UTF-8")
            } catch (e: Exception) {
                Log.e("MoviePlayerVM", "Error decoding URL: ${e.message}", e)
                ""
            }
        } else {
            ""
        }

        if (decodedUrl.isNotBlank()) {
            Log.d("MoviePlayerVM", "Initializing player with URL: $decodedUrl on Main Thread.")
            // Gọi initializePlayer TRỰC TIẾP trên Main Thread.
            // KHÔNG BAO BỌC NÓ TRONG launch(Dispatchers.Default)
            initializePlayer(decodedUrl)
        } else {
            Log.e("MoviePlayerVM", "Movie URL is blank or invalid. Player not initialized.")
            _isPlayerReady.value = false
        }
    }

    // Bỏ từ khóa 'suspend' và 'withContext'
    private fun initializePlayer(url: String) {
        try {
            _player = ExoPlayer.Builder(application).build().apply {
                setMediaItem(MediaItem.fromUri(url))
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        // Các callback này thường được gọi trên Main Thread bởi ExoPlayer
                        // và việc cập nhật StateFlow trên Main Thread là an toàn.
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        _playbackState.value = playbackState
                        if (playbackState == Player.STATE_READY) {
                            _isPlayerReady.value = true
                            playWhenReady = true // Tự động phát khi sẵn sàng
                            Log.d("MoviePlayerVM", "Player is ready and playing.")
                        } else if (playbackState == Player.STATE_ENDED) {
                            Log.d("MoviePlayerVM", "Playback ended.")
                        }
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        super.onPlayerError(error)
                        Log.e("MoviePlayerVM", "Player Error: ${error.message}", error)
                        _isPlayerReady.value = false
                        _player?.release()
                        _player = null
                    }
                })
                prepare() // prepare() của ExoPlayer sẽ tự động thực hiện I/O ở background
            }
        } catch (e: Exception) {
            Log.e("MoviePlayerVM", "Error initializing player: ${e.message}", e)
            _isPlayerReady.value = false
            _player?.release()
            _player = null
        }
    }

    fun play() {
        _player?.playWhenReady = true
    }

    fun pause() {
        _player?.playWhenReady = false
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MoviePlayerVM", "onCleared called, releasing player.")
        _player?.release()
        _player = null
    }
}