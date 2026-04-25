package com.musicwave.app.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicwave.app.core.domain.model.TrackToPlay
import com.musicwave.app.core.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    val favorites: Flow<List<TrackToPlay>> = repository.getFavorites()

    fun remove(trackId: Long) {
        viewModelScope.launch { repository.removeFavorite(trackId) }
    }
}
