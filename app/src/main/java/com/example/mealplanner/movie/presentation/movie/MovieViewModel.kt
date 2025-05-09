package com.example.mealplanner.movie.presentation.movie

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.movie.domain.model.ServerEpisodeData
import com.example.mealplanner.movie.domain.usecase.GetMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    val exoPlayer: ExoPlayer? get() = _exoPlayer // UI sẽ sử dụng instance này cho PlayerView

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle) // Trạng thái ban đầu
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    // --- THÊM StateFlow cho currentEpisodeIndexInFirstServer ---
    private val _currentEpisodeIndexInFirstServer = MutableStateFlow(-1)
    val currentEpisodeIndexInFirstServer: StateFlow<Int> = _currentEpisodeIndexInFirstServer.asStateFlow()
    // --- KẾT THÚC THÊM ---

    val firstServerEpisodes: StateFlow<List<ServerEpisodeData>> =
        movieDetailState.map<Resource<Movie>, List<ServerEpisodeData>> { movieResource ->
            when (movieResource) {
                is Resource.Success -> {
                    val movieData = movieResource.data
                    run {
                        val firstServerGroup = movieData.episodes!!.firstOrNull()
                        if (firstServerGroup != null) {
                            firstServerGroup.serverData
                        } else {
                            Log.w("MovieViewModel", "Movie '${movieData.metadata.name}' has no server groups in its episodes list.")
                            emptyList<ServerEpisodeData>()
                        }
                    }
                }
                is Resource.Loading, is Resource.Error -> {
                    emptyList<ServerEpisodeData>()
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList<ServerEpisodeData>()
        )

    // Lưu trạng thái phát lại cho việc khôi phục (ví dụ khi xoay màn hình hoặc chuyển tập)
    private var currentPlaybackPositionMs: Long = 0L
    private var playWhenReady: Boolean = true // Mặc định tự động phát khi sẵn sàng

    // State cho tốc độ phát
    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    fun fetchMovieDetail(slug: String) {
        _movieDetailState.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            val result = getMovieUseCase(slug)
            _movieDetailState.value = result
            if (result is Resource.Success) {
                _currentEpisodeIndexInFirstServer.value = -1 // Reset index khi fetch phim mới
            }
        }
    }

    init {
        fetchMovieDetail(slug)
        println(slug)
    }

    /**
     * Phát một tập phim cụ thể từ server đầu tiên.
     * @param episodeData Đối tượng ServerEpisodeData chứa thông tin tập phim (bao gồm link_m3u8).
     * @param indexInFirstServerList Index của tập phim này trong danh sách server_data của server ĐẦU TIÊN.
     */
    fun playEpisodeFromFirstServer(episodeData: ServerEpisodeData, indexInFirstServerList: Int) {
        val episodeUrl = episodeData.link_m3u8
        if (episodeUrl.isBlank()) {
            _playerState.value = PlayerState.Error("URL của tập phim không hợp lệ.")
            Log.e("MovieViewModel", "playEpisodeFromFirstServer called with blank URL.")
            return
        }
        Log.d("MovieViewModel", "Attempting to play episode (from first server, ep_idx: $indexInFirstServerList): ${episodeData.name} - $episodeUrl")
        releasePlayerInternal(saveState = false)

        _currentEpisodeIndexInFirstServer.value = indexInFirstServerList // Cập nhật StateFlow
        _playerState.value = PlayerState.Loading

        _exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.Builder().setUri(episodeUrl).setMediaId(episodeUrl).build())
            this.playWhenReady = true
            setPlaybackSpeed(this@MovieViewModel._playbackSpeed.value)
            addListener(playerListener)
            prepare()
        }
    }

    private val playerListener = object : Player.Listener {
        // Trong MovieViewModel, trong playerListener
        override fun onVideoSizeChanged(videoSize: androidx.media3.common.VideoSize) {
            super.onVideoSizeChanged(videoSize)
            Log.d("MovieViewModel", "Video size changed: width=${videoSize.width}, height=${videoSize.height}, pixelRatio=${videoSize.pixelWidthHeightRatio}")
            if (videoSize.width == 0 || videoSize.height == 0) {
                Log.w("MovieViewModel", "Video size reported as zero, this could be an issue with the stream or decoder.")
            }
        }

        override fun onRenderedFirstFrame() {
            Log.d("MovieScreen", "Đã render frame đầu tiên")
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val currentExoPlayerIsPlaying = _exoPlayer?.isPlaying ?: false
            Log.d("MovieViewModel", "ExoPlayer state changed: $playbackState, isPlaying: $currentExoPlayerIsPlaying. Current VM state: ${_playerState.value}")

            val newAppState: PlayerState = when (playbackState) {
                Player.STATE_IDLE -> PlayerState.Idle
                Player.STATE_BUFFERING -> PlayerState.Buffering
                Player.STATE_READY -> {
                    if (currentExoPlayerIsPlaying) PlayerState.Playing else PlayerState.Ready
                }
                Player.STATE_ENDED -> {
                    playNextEpisodeFromFirstServerIfAvailable()
                    PlayerState.Ended
                }
                else -> _playerState.value
            }
            if (_playerState.value != newAppState || newAppState == PlayerState.Ended) {
                _playerState.value = newAppState
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            val currentVmState = _playerState.value
            val exoPlaybackState = _exoPlayer?.playbackState
            Log.d("MovieViewModel", "ExoPlayer isPlaying changed: $isPlaying. Current VM state: $currentVmState, Exo playbackState: $exoPlaybackState")

            when {
                isPlaying -> {
                    if (exoPlaybackState == Player.STATE_READY || exoPlaybackState == Player.STATE_BUFFERING) {
                        if (currentVmState != PlayerState.Playing) _playerState.value = PlayerState.Playing
                    }
                }
                else -> {
                    if (exoPlaybackState == Player.STATE_READY) {
                        if (currentVmState == PlayerState.Playing) {
                            _playerState.value = PlayerState.Paused
                        } else if (currentVmState != PlayerState.Paused && currentVmState != PlayerState.Ready) {
                            _playerState.value = PlayerState.Ready
                        }
                    } else if (exoPlaybackState == Player.STATE_ENDED) {
                        if (currentVmState != PlayerState.Ended) _playerState.value = PlayerState.Ended
                    }
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e("MovieViewModel", "ExoPlayer error: ${error.message}", error)
            _playerState.value = PlayerState.Error("Lỗi trình phát: ${error.message ?: "Không rõ lỗi"}")
        }
    }

    fun togglePlayPause() {
        _exoPlayer?.let {
            if (it.isPlaying) pause() else play()
        }
    }

    fun play() {
        _exoPlayer?.playWhenReady = true
        _exoPlayer?.play()
    }

    fun pause() {
        _exoPlayer?.playWhenReady = false
        _exoPlayer?.pause()
    }

    fun seekForward(seconds: Int = 10) {
        _exoPlayer?.let { player ->
            val newPosition = player.currentPosition + (seconds * 1000L)
            val duration = player.duration.coerceAtLeast(0L)
            player.seekTo(newPosition.coerceAtMost(duration))
        }
    }

    fun seekRewind(seconds: Int = 10) {
        _exoPlayer?.let { player ->
            val newPosition = player.currentPosition - (seconds * 1000L)
            player.seekTo(newPosition.coerceAtLeast(0L))
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _exoPlayer?.let {
            val currentSpeed = it.playbackParameters.speed
            if (currentSpeed != speed) {
                it.playbackParameters = PlaybackParameters(speed)
                _playbackSpeed.value = speed
                Log.d("MovieViewModel", "Playback speed set to: $speed")
            }
        }
    }

    fun playNextEpisodeFromFirstServerIfAvailable() {
        val firstServerEpisodesList = firstServerEpisodes.value
        val currentIdx = _currentEpisodeIndexInFirstServer.value // Lấy giá trị từ StateFlow

        if (currentIdx >= 0 && currentIdx < firstServerEpisodesList.size - 1) {
            val nextEpisodeIndex = currentIdx + 1
            val nextEpisode = firstServerEpisodesList[nextEpisodeIndex]
            Log.d("MovieViewModel", "Playing next episode (from first server, next_ep_idx: $nextEpisodeIndex): ${nextEpisode.name}")
            playEpisodeFromFirstServer(nextEpisode, nextEpisodeIndex)
        } else {
            Log.d("MovieViewModel", "No next episode available in the first server.")
            _playerState.value = PlayerState.Ended
        }
    }

    private fun releasePlayerInternal(saveState: Boolean) {
        _exoPlayer?.let { player ->
            if (saveState) {
                currentPlaybackPositionMs = player.currentPosition
                playWhenReady = player.playWhenReady
            } else {
                currentPlaybackPositionMs = 0L
                playWhenReady = true
            }
            player.removeListener(playerListener)
            player.release()
        }
        _exoPlayer = null
        if (_playerState.value !is PlayerState.Error || !saveState) {
            _playerState.value = PlayerState.Idle
        }
    }

    fun prepareForRelease() {
        releasePlayerInternal(saveState = true)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayerInternal(saveState = false)
    }
}