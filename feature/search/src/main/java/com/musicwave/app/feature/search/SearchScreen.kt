package com.musicwave.app.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.musicwave.app.core.domain.model.Artist
import com.musicwave.app.core.domain.model.Release
import com.musicwave.app.core.domain.model.Track
import com.musicwave.app.core.domain.model.TrackToPlay
import com.musicwave.app.core.ui.component.EmptyState
import com.musicwave.app.core.ui.component.ShimmerCircle
import com.musicwave.app.core.ui.component.ShimmerRect
import com.musicwave.app.core.ui.component.ShimmerLine
import com.musicwave.app.feature.player.PlayerViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    onArtistClick: (Long) -> Unit,
    onAlbumClick: (Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val state by viewModel.uiState.collectAsStateWithLifecycle(SearchUiState())
    val pullState = rememberPullRefreshState(state.isLoading, { viewModel.onQueryChanged(query) })
    val snack = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.error.collect { snack.showSnackbar(it) } }
    LaunchedEffect(query) { viewModel.onQueryChanged(query) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Text("Cari", fontWeight = FontWeight.Bold, fontSize = 26.sp, modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp))
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                placeholder = { Text("Apa yang ingin kamu putar?") },
                leadingIcon = { Icon(Icons.Filled.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingIcon = if (query.isNotEmpty()) {{ IconButton(onClick = { query = "" }) { Icon(Icons.Filled.Clear, "Hapus") } }} else null,
                singleLine = true, shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp))
            Box(Modifier.weight(1f).pullRefresh(pullState)) {
                when {
                    query.isBlank() -> EmptyState("")
                    state.isLoading -> SearchShimmer()
                    else -> SearchContent(state.artists, state.albums, state.tracks, onArtistClick, onAlbumClick,
                        onTrackClick = { track ->
                            playerViewModel.play(TrackToPlay(
                                id = track.id,
                                title = track.title,
                                artistName = track.artist?.name ?: "",
                                releaseTitle = track.release?.title ?: "",
                                imageUrl = track.release?.imageUrl,
                                duration = track.duration,
                                previewUrl = track.previewUrl
                            ))
                        })
                }
                PullRefreshIndicator(state.isLoading, pullState, Modifier.align(Alignment.TopCenter),
                    backgroundColor = MaterialTheme.colorScheme.surface, contentColor = Color(0xFF1DB954))
            }
        }
        SnackbarHost(snack, Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp))
    }
}

@Composable
private fun SearchShimmer() {
    LazyColumn {
        item { Text("Artis", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)) }
        items(3) { Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            ShimmerCircle(56); Spacer(Modifier.width(12.dp)); ShimmerLine(150, 16)
        } }
        item { Text("Album", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)) }
        items(3) { Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            ShimmerRect(56, 56); Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { ShimmerLine(160, 14); Spacer(Modifier.height(4.dp)); ShimmerLine(100, 12) }
        } }
        item { Text("Lagu", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)) }
        items(3) { Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            ShimmerRect(56, 56); Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { ShimmerLine(180, 14); Spacer(Modifier.height(4.dp)); ShimmerLine(120, 12) }
        } }
    }
}

@Composable
fun SearchContent(artists: List<Artist>, albums: List<Release>, tracks: List<Track>,
                  onArtistClick: (Long) -> Unit, onAlbumClick: (Long) -> Unit,
                  onTrackClick: (Track) -> Unit = {}) {
    LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
        if (artists.isNotEmpty()) {
            item { Text("Artis", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)) }
            items(artists, key = { "a_${it.id}" }) { a ->
                Row(Modifier.fillMaxWidth().clickable { onArtistClick(a.id) }.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(a.imageUrl, null, contentScale = ContentScale.Crop, modifier = Modifier.size(56.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
                    Spacer(Modifier.width(12.dp)); Text(a.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        if (albums.isNotEmpty()) {
            item { Text("Album", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)) }
            items(albums, key = { "r_${it.id}" }) { a ->
                Row(Modifier.fillMaxWidth().clickable { onAlbumClick(a.id) }.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(a.imageUrl, null, contentScale = ContentScale.Crop, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) { Text(a.title, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(a.artist?.name ?: "Album", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }
                }
            }
        }
        if (tracks.isNotEmpty()) {
            item { Text("Lagu", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)) }
            items(tracks, key = { "t_${it.id}" }) { t ->
                Row(Modifier.fillMaxWidth().clickable { onTrackClick(t) }.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(t.release?.imageUrl, null, contentScale = ContentScale.Crop, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) { Text(t.title, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(t.artist?.name ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SearchContentPreview() {
    com.musicwave.app.core.ui.theme.AppTheme {
        SearchContent(listOf(Artist(27, "Daft Punk")), listOf(Release(302127, "Discovery", artist = Artist(27, "Daft Punk"))),
            listOf(Track(3135556, "One More Time", artist = Artist(27, "Daft Punk"))), {}, {}, {})
    }
}
