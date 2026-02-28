package com.musicwave.app.feature.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.musicwave.app.core.domain.model.Artist
import com.musicwave.app.core.domain.model.Release
import com.musicwave.app.core.domain.model.Track
import com.musicwave.app.core.domain.model.TrackToPlay
import com.musicwave.app.core.ui.component.ShimmerRect
import com.musicwave.app.core.ui.component.ShimmerLine
import com.musicwave.app.feature.player.PlayerViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BrowseScreen(
    onAlbumClick: (Long) -> Unit,
    viewModel: BrowseViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val albums = viewModel.albums.collectAsLazyPagingItems()
    val tracks = viewModel.tracks.collectAsLazyPagingItems()
    val snack = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.error.collect { snack.showSnackbar(it) } }
    LaunchedEffect(albums.loadState.refresh) {
        val e = albums.loadState.refresh as? LoadState.Error
        if (e != null) snack.showSnackbar(e.error.message ?: "Gagal memuat")
    }

    val pullState = rememberPullRefreshState(
        tracks.loadState.refresh is LoadState.Loading, { viewModel.refresh() }
    )

    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().pullRefresh(pullState)) {
            if (albums.loadState.refresh is LoadState.Loading && albums.itemCount == 0) {
                BrowseShimmer()
            } else {
                BrowseContent(
                    (0 until albums.itemCount).mapNotNull { albums[it] },
                    (0 until tracks.itemCount).mapNotNull { tracks[it] },
                    onAlbumClick,
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
                    }
                )
            }
            PullRefreshIndicator(tracks.loadState.refresh is LoadState.Loading, pullState,
                Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface, contentColor = Color(0xFF1DB954))
        }
        SnackbarHost(snack, Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp))
    }
}

@Composable
private fun BrowseShimmer() {
    LazyColumn(Modifier.fillMaxSize()) {
        item { Text("Album Populer", fontWeight = FontWeight.Bold, fontSize = 21.sp, modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 12.dp)) }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(4) {
                    Column(Modifier.width(150.dp)) { ShimmerRect(150, 150); Spacer(Modifier.height(8.dp)); ShimmerLine(120, 14); Spacer(Modifier.height(4.dp)); ShimmerLine(80, 12) }
                }
            }
        }
        item { Text("Lagu Populer", fontWeight = FontWeight.Bold, fontSize = 21.sp, modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)) }
        items(5) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                ShimmerRect(56, 56); Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) { ShimmerLine(180, 14); Spacer(Modifier.height(6.dp)); ShimmerLine(120, 12) }
            }
        }
    }
}

@Composable
fun BrowseContent(albums: List<Release>, tracks: List<Track>, onAlbumClick: (Long) -> Unit, onTrackClick: (Track) -> Unit = {}) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 8.dp)) {
        item { Text("Album Populer", fontWeight = FontWeight.Bold, fontSize = 21.sp, modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 12.dp)) }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(albums, key = { it.id }) { AlbumCard(it, onAlbumClick) }
            }
        }
        item { Text("Lagu Populer", fontWeight = FontWeight.Bold, fontSize = 21.sp, modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)) }
        items(tracks, key = { it.id }) { TrackListItem(it, onTrackClick) }
    }
}

@Composable
fun AlbumCard(release: Release, onClick: (Long) -> Unit) {
    Column(Modifier.width(150.dp).clickable { onClick(release.id) }) {
        AsyncImage(release.imageUrl, null, contentScale = ContentScale.Crop,
            modifier = Modifier.width(150.dp).height(150.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
        Spacer(Modifier.height(8.dp))
        Text(release.title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(release.artist?.name ?: release.label ?: "Album", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun TrackListItem(track: Track, onTrackClick: (Track) -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth()
            .clickable { onTrackClick(track) }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(track.release?.imageUrl, null, contentScale = ContentScale.Crop,
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(track.title, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist?.name ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BrowseContentPreview() {
    com.musicwave.app.core.ui.theme.AppTheme {
        BrowseContent(
            listOf(Release(302127, "Discovery", artist = Artist(27, "Daft Punk")), Release(149159161, "RAM", artist = Artist(27, "Daft Punk"))),
            listOf(Track(1, "One More Time", artist = Artist(27, "Daft Punk")), Track(2, "Digital Love", artist = Artist(27, "Daft Punk"))),
            {}, {})
    }
}
