package com.musicwave.app.feature.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.musicwave.app.core.domain.model.ArtistDetail
import com.musicwave.app.core.domain.model.Release
import com.musicwave.app.core.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _detail = MutableStateFlow<ArtistDetail?>(null)
    val detail: StateFlow<ArtistDetail?> = _detail

    private val _error = Channel<String>(Channel.BUFFERED)
    val error: Flow<String> = _error.receiveAsFlow()

    fun load(artistId: Long) {
        _detail.value = null
        viewModelScope.launch {
            try {
                repository.getArtistDetail(artistId).collect { _detail.value = it }
            } catch (e: Exception) {
                _error.send(e.message ?: "Gagal memuat artis")
            }
        }
    }

    fun albums(artistId: Long): Flow<PagingData<Release>> =
        repository.getArtistAlbums(artistId).cachedIn(viewModelScope)
}
