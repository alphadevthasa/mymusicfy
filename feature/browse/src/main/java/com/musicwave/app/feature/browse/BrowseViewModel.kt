package com.musicwave.app.feature.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.musicwave.app.core.domain.model.Release
import com.musicwave.app.core.domain.model.Track
import com.musicwave.app.core.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    repository: MusicRepository
) : ViewModel() {

    val albums: Flow<PagingData<Release>> = repository.chartAlbums().cachedIn(viewModelScope)
    val tracks: Flow<PagingData<Track>> = repository.chartTracks().cachedIn(viewModelScope)

    private val _error = Channel<String>(Channel.BUFFERED)
    val error: Flow<String> = _error.receiveAsFlow()

    fun refresh() { viewModelScope.launch { /* paging handles it */ } }

    fun showError(msg: String) { viewModelScope.launch { _error.send(msg) } }
}
