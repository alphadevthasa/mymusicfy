package com.musicwave.app.core.domain.repository

import androidx.paging.PagingData
import com.musicwave.app.core.domain.model.Artist
import com.musicwave.app.core.domain.model.ArtistDetail
import com.musicwave.app.core.domain.model.Release
import com.musicwave.app.core.domain.model.ReleaseDetail
import com.musicwave.app.core.domain.model.Track
import com.musicwave.app.core.domain.model.SearchResults
import com.musicwave.app.core.domain.model.TrackToPlay
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun search(query: String): Flow<SearchResults>

    fun chartAlbums(): Flow<PagingData<Release>>

    fun chartTracks(): Flow<PagingData<Track>>

    fun getArtistDetail(artistId: Long): Flow<ArtistDetail>

    fun getArtistAlbums(artistId: Long): Flow<PagingData<Release>>

    fun getArtistTopTracks(artistId: Long): Flow<PagingData<Track>>

    fun getAlbumDetail(albumId: Long): Flow<ReleaseDetail>

    fun getAlbumTracks(albumId: Long): Flow<PagingData<Track>>

    fun getTrackDetail(trackId: Long): Flow<Track>

    fun getFavorites(): Flow<List<TrackToPlay>>

    suspend fun toggleFavorite(track: TrackToPlay)

    suspend fun removeFavorite(trackId: Long)

    suspend fun isFavorite(trackId: Long): Boolean
}
