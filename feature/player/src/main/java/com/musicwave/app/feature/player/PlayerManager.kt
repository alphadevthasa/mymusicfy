package com.musicwave.app.feature.player

import android.app.Application
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerManager @Inject constructor(
    private val application: Application
) {

    private var exoPlayer: ExoPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _currentTrack = MutableStateFlow<TrackState?>(null)
    val currentTrack: StateFlow<TrackState?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    @UnstableApi
    fun initialize() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(application).build()
            exoPlayer?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    if (isPlaying) startProgressUpdates()
                    else stopProgressUpdates()
                }
            })
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    fun play(trackId: Long, title: String, url: String, artistName: String = "", imageUrl: String? = null) {
        if (exoPlayer == null) initialize()
        val player = exoPlayer ?: return
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()
        _currentTrack.value = TrackState(trackId, title, artistName, imageUrl)
        _isPlaying.value = true
        val dur = player.duration
        if (dur > 0) _duration.value = dur
    }

    fun pause() {
        exoPlayer?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        exoPlayer?.play()
        _isPlaying.value = true
    }

    fun release() {
        progressJob?.cancel()
        exoPlayer?.release()
        exoPlayer = null
        _currentTrack.value = null
        _isPlaying.value = false
        _currentPosition.value = 0L
        _duration.value = 0L
        _isPlaying.value = false
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (true) {
                exoPlayer?.let {
                    _currentPosition.value = it.currentPosition
                    val dur = it.duration
                    if (dur > 0) _duration.value = dur
                }
                delay(250)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    data class TrackState(
        val trackId: Long,
        val title: String,
        val artistName: String = "",
        val imageUrl: String? = null
    )
}
