package com.musicwave.app.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.musicwave.app.core.ui.component.formatDuration
import com.musicwave.app.core.ui.theme.SpotifyGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle(PlayerUiState())
    val sliderPos = if (state.durationMs > 0) state.currentPositionMs.toFloat() / state.durationMs.toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = MaterialTheme.colorScheme.onBackground) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (state.currentTitle == null) return@Scaffold
        PlayerContent(
            imageUrl = state.imageUrl,
            title = state.currentTitle ?: "",
            artistName = state.artistName,
            isPlaying = state.isPlaying,
            isFavorite = state.isFavorite,
            sliderPos = sliderPos,
            onSliderChange = { viewModel.seekTo((it * state.durationMs).toLong()) },
            onTogglePlay = viewModel::togglePlay,
            onToggleFavorite = { state.currentTrackId?.let { viewModel.toggleFavorite(it, state.currentTitle ?: "") } },
            currentPositionMs = state.currentPositionMs,
            durationMs = state.durationMs,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun PlayerContent(
    imageUrl: String?,
    title: String,
    artistName: String,
    isPlaying: Boolean,
    isFavorite: Boolean,
    sliderPos: Float,
    onSliderChange: (Float) -> Unit,
    onTogglePlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    currentPositionMs: Long = 0L,
    durationMs: Long = 0L,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        AsyncImage(model = imageUrl, contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.size(300.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant))

        Spacer(Modifier.height(32.dp))

        Text(title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Text(artistName, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 15.sp,
            maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(32.dp))

        Slider(value = sliderPos, onValueChange = onSliderChange,
            colors = SliderDefaults.colors(thumbColor = SpotifyGreen, activeTrackColor = SpotifyGreen, inactiveTrackColor = Color(0xFF535353)),
            modifier = Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatDuration((currentPositionMs / 1000).toInt()), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            Text(formatDuration((durationMs / 1000).toInt()), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }

        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onToggleFavorite) {
                Icon(if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, null,
                    tint = if (isFavorite) SpotifyGreen else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
            }
            Icon(Icons.Filled.SkipPrevious, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(36.dp))
            IconButton(onClick = onTogglePlay, modifier = Modifier.size(72.dp)) {
                Icon(if (isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircleFilled, null,
                    tint = SpotifyGreen, modifier = Modifier.size(72.dp))
            }
            Icon(Icons.Filled.SkipNext, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(36.dp))
        }

        Spacer(Modifier.weight(1f))
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlayerContentPreview() {
    com.musicwave.app.core.ui.theme.AppTheme {
        PlayerContent(
            imageUrl = null, title = "One More Time", artistName = "Daft Punk",
            isPlaying = true, isFavorite = true, sliderPos = 0.3f,
            onSliderChange = {}, onTogglePlay = {}, onToggleFavorite = {},
            currentPositionMs = 83000L, durationMs = 226000L
        )
    }
}
