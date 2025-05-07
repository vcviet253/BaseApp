package com.example.mealplanner.movie.presentation.movie

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.usecase.GetMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Các trạng thái có thể có của Player
sealed class PlayerState {
    object Idle : PlayerState()
    object Loading : PlayerState() // Thêm trạng thái loading URL
    object Buffering : PlayerState()
    object Ready : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Ended : PlayerState()
    data class Error(val message: String) : PlayerState()
}

@HiltViewModel
class MovieViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val getMovieUseCase: GetMovieUseCase,
) : ViewModel() {
    val slug: String = checkNotNull(savedStateHandle["slug"])

    private val _movieDetailState = MutableStateFlow<Resource<Movie>>(Resource.Loading())
    val movieDetailState = _movieDetailState.asStateFlow()

    private var _exoPlayer: ExoPlayer? = null
    val exoPlayer: ExoPlayer? get() = _exoPlayer

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState

    // Biến lưu vị trí phát hiện tại khi activity bị pause
    private var currentPlaybackPositionMs: Long = 0L
    private var currentMediaItemIndex: Int = 0
    private var playWhenReady: Boolean = true

    fun fetchMovie(slug: String) {
        _movieDetailState.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _movieDetailState.value = getMovieUseCase(slug)
        }
    }

    init {
        fetchMovie(slug)
        println(slug)
    }

    fun initializePlayer(url: String) {
        if (_exoPlayer == null && url.isNotEmpty()) {
            Log.d("VideoPlayerViewModel", "Initializing ExoPlayer with URL: $url")
            _exoPlayer = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(url))
                // Khôi phục trạng thái
                this.playWhenReady = this@MovieViewModel.playWhenReady
                seekTo(currentMediaItemIndex, currentPlaybackPositionMs)
                addListener(playerListener)
                prepare() // Chuẩn bị player
                // play() // Bắt đầu phát nếu playWhenReady là true
            }
            _playerState.value = PlayerState.Ready
        } else {
            Log.d("VideoPlayerViewModel", "ExoPlayer already initialized or URL is empty.")
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString = when (playbackState) {
                Player.STATE_IDLE -> "Idle"
                Player.STATE_BUFFERING -> "Buffering"
                Player.STATE_READY -> "Ready"
                Player.STATE_ENDED -> "Ended"
                else -> "Unknown"
            }
            Log.d("VideoPlayerViewModel", "Playback state changed: $stateString")
            when (playbackState) {
                Player.STATE_IDLE -> _playerState.value = PlayerState.Idle
                Player.STATE_BUFFERING -> _playerState.value = PlayerState.Buffering
                Player.STATE_READY -> _playerState.value = PlayerState.Ready
                Player.STATE_ENDED -> _playerState.value = PlayerState.Ended
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Log.d("VideoPlayerViewModel", "Is playing changed: $isPlaying")
            if (isPlaying) {
                _playerState.value = PlayerState.Playing
            } else {
                // Nếu không phải đang phát và trạng thái không phải là Buffering hoặc Ended,
                // thì có thể là Paused.
                if (_exoPlayer?.playbackState != Player.STATE_BUFFERING && _exoPlayer?.playbackState != Player.STATE_ENDED) {
                    _playerState.value = PlayerState.Paused
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e("VideoPlayerViewModel", "Player error", error)
            _playerState.value = PlayerState.Error("Lỗi phát video: ${error.localizedMessage}")
        }
    }

    fun play() {
        _exoPlayer?.play()
    }

    fun pause() {
        _exoPlayer?.pause()
    }

    private fun releasePlayer() {
        _exoPlayer?.let { player ->
            Log.d("VideoPlayerViewModel", "Releasing ExoPlayer")
            // Lưu trạng thái trước khi giải phóng
            currentPlaybackPositionMs = player.currentPosition
            currentMediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady

            player.removeListener(playerListener)
            player.release()
        }
        _exoPlayer = null
        _playerState.value = PlayerState.Idle // Reset trạng thái
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("VideoPlayerViewModel", "ViewModel cleared, releasing player.")
        releasePlayer()
    }
}