package com.musicwave.app.feature.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.musicwave.app.core.domain.model.Artist
import com.musicwave.app.core.domain.model.ArtistDetail
import com.musicwave.app.core.domain.model.Release
import com.musicwave.app.core.ui.component.ShimmerCircle
import com.musicwave.app.core.ui.component.ShimmerRect
import com.musicwave.app.core.ui.component.ShimmerLine
import com.musicwave.app.feature.browse.AlbumCard

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    artistId: Long,
    onAlbumClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: ArtistViewModel = hiltViewModel()
) {
    LaunchedEffect(artistId) { viewModel.load(artistId) }
    val detail by viewModel.detail.collectAsStateWithLifecycle(null)
    val albums = remember(artistId) { viewModel.albums(artistId) }.collectAsLazyPagingItems()
    val albumList = (0 until albums.itemCount).mapNotNull { albums[it] }
    val isRefreshing = albums.loadState.refresh is LoadState.Loading
    val pullState = rememberPullRefreshState(isRefreshing, { viewModel.load(artistId) })
    val snack = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.error.collect { snack.showSnackbar(it) } }

    Scaffold(
        snackbarHost = { SnackbarHost(snack) },
        topBar = {
            TopAppBar(title = { Text("") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = MaterialTheme.colorScheme.onBackground) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize().pullRefresh(pullState)) {
            if (detail == null && albums.itemCount == 0) {
                ArtistShimmer()
            } else {
                ArtistContent(detail, albumList, onAlbumClick)
            }
            PullRefreshIndicator(isRefreshing, pullState, Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface, contentColor = Color(0xFF1DB954))
        }
    }
}

@Composable
private fun ArtistShimmer() {
    LazyColumn {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                ShimmerCircle(200); Spacer(Modifier.height(16.dp)); ShimmerLine(160, 24); Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) { ShimmerLine(80, 14); ShimmerLine(100, 14) }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(4) { Column(Modifier.width(150.dp)) { ShimmerRect(150, 150); Spacer(Modifier.height(8.dp)); ShimmerLine(120, 14); Spacer(Modifier.height(4.dp)); ShimmerLine(80, 12) } }
            }
        }
    }
}

@Composable
fun ArtistContent(detail: ArtistDetail?, albums: List<Release>, onAlbumClick: (Long) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 20.dp)) {
                AsyncImage(detail?.imageUrl, null, contentScale = ContentScale.Crop,
                    modifier = Modifier.size(200.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
                Spacer(Modifier.height(16.dp))
                Text(detail?.name ?: "", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                if (detail != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Text("${detail.nbAlbum ?: 0} Album", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Text("${formatFans(detail.nbFan)} Penggemar", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
        }
        item { Text("Album", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)) }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(albums, key = { it.id }) { AlbumCard(it, onAlbumClick) }
            }
        }
    }
}

private fun formatFans(n: Int?): String {
    val count = n ?: 0
    return if (count >= 1_000_000) "${count / 1_000_000}JT"
    else if (count >= 1_000) "${count / 1_000}RB"
    else "$count"
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistContentPreview() {
    com.musicwave.app.core.ui.theme.AppTheme {
        ArtistContent(ArtistDetail(27, "Daft Punk", nbAlbum = 10, nbFan = 2_500_000),
            listOf(Release(302127, "Discovery", artist = Artist(27, "Daft Punk")), Release(149159161, "RAM", artist = Artist(27, "Daft Punk"))), {})
    }
}
