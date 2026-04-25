package com.musicwave.app.feature.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musicwave.app.core.domain.model.TrackToPlay
import com.musicwave.app.core.ui.component.EmptyState
import com.musicwave.app.core.ui.component.ShimmerRect
import com.musicwave.app.core.ui.component.ShimmerLine
import com.musicwave.app.core.ui.component.TrackRow
import com.musicwave.app.feature.player.PlayerUiState
import com.musicwave.app.feature.player.PlayerViewModel

@Composable
fun FavoritesScreen(
    onAlbumClick: (Long) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle(emptyList())
    val playerState by playerViewModel.uiState.collectAsStateWithLifecycle(PlayerUiState())
    var hasLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(favorites) { hasLoaded = true }

    Column(Modifier.fillMaxSize()) {
        Text("Favorit Saya", fontWeight = FontWeight.Bold, fontSize = 26.sp, modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 12.dp))
        when {
            !hasLoaded -> Shimmer()
            favorites.isEmpty() -> EmptyState("Belum ada lagu favorit.")
            else -> FavoritesContent(favorites, playerState.currentTrackId, playerState.isPlaying, playerViewModel::play, viewModel::remove)
        }
    }
}

@Composable
private fun Shimmer() {
    LazyColumn {
        items(5) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                ShimmerRect(48, 48)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) { ShimmerLine(160, 14); Spacer(Modifier.height(4.dp)); ShimmerLine(100, 12) }
                Spacer(Modifier.width(8.dp)); ShimmerLine(18, 18); Spacer(Modifier.width(8.dp)); ShimmerLine(28, 28)
            }
        }
    }
}

@Composable
fun FavoritesContent(tracks: List<TrackToPlay>, currentTrackId: Long?, isPlaying: Boolean,
                     onPlay: (TrackToPlay) -> Unit, onRemove: (Long) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
        items(tracks, key = { it.id }) { track ->
            TrackRow(track = track, isPlaying = track.id == currentTrackId && isPlaying, isFavorite = true,
                onPlayClick = { onPlay(track) }, onToggleFavorite = { onRemove(track.id) })
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FavoritesContentPreview() {
    com.musicwave.app.core.ui.theme.AppTheme {
        FavoritesContent(listOf(TrackToPlay(1, "One More Time", "Daft Punk", "Discovery", null, 320),
            TrackToPlay(2, "Digital Love", "Daft Punk", "Discovery", null, 300)), null, false, {}, {})
    }
}
