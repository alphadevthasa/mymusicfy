package com.musicwave.app.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicwave.app.core.domain.model.Artist
import com.musicwave.app.core.domain.model.Release
import com.musicwave.app.core.domain.model.Track
import com.musicwave.app.core.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchUiState(
    val artists: List<Artist> = emptyList(),
    val albums: List<Release> = emptyList(),
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val _error = Channel<String>(Channel.BUFFERED)
    val error: Flow<String> = _error.receiveAsFlow()

    val uiState: StateFlow<SearchUiState> = queryFlow
        .debounce(350)
        .distinctUntilChanged()
        .flatMapLatest { q ->
            if (q.isBlank()) {
                flowOf(SearchUiState())
            } else {
                repository.search(q)
                    .map { results ->
                        SearchUiState(
                            artists = results.artists,
                            albums = results.releases,
                            tracks = results.tracks
                        )
                    }
                    .onStart { emit(SearchUiState(isLoading = true)) }
                    .catch { e ->
                        _error.trySend(e.message ?: "Gagal mencari")
                        emit(SearchUiState())
                    }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    fun onQueryChanged(query: String) {
        queryFlow.value = query
    }
}
