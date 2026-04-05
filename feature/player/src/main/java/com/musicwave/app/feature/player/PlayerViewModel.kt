package com.musicwave.app.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicwave.app.core.domain.model.TrackToPlay
import com.musicwave.app.core.domain.repository.MusicRepository
import com.musicwave.app.feature.player.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val currentTitle: String? = null,
    val currentTrackId: Long? = null,
    val isPlaying: Boolean = false,
    val isFavorite: Boolean = false,
    val artistName: String = "",
    val imageUrl: String? = null,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playerManager: PlayerManager
) : ViewModel() {

    val uiState: Flow<PlayerUiState> = combine(
        playerManager.currentTrack,
        playerManager.isPlaying,
        playerManager.currentPosition,
        playerManager.duration
    ) { track, playing, pos, dur ->
        val fav = track?.trackId?.let { repository.isFavorite(it) } ?: false
        PlayerUiState(
            currentTitle = track?.title,
            currentTrackId = track?.trackId,
            isPlaying = playing,
            isFavorite = fav,
            artistName = track?.artistName ?: "",
            imageUrl = track?.imageUrl,
            currentPositionMs = pos,
            durationMs = dur
        )
    }

    fun play(track: TrackToPlay) {
        val url = track.previewUrl ?: return
        playerManager.play(track.id, track.title, url, track.artistName, track.imageUrl)
    }

    fun seekTo(positionMs: Long) {
        playerManager.seekTo(positionMs)
    }

    fun togglePlay() {
        if (playerManager.isPlaying.value) playerManager.pause()
        else playerManager.resume()
    }

    fun toggleFavorite(trackId: Long, title: String) {
        viewModelScope.launch {
            repository.toggleFavorite(TrackToPlay(trackId, title, "", "", null, null))
        }
    }
}
