package com.musicwave.app.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.musicwave.app.core.common.util.Constants
import com.musicwave.app.core.database.dao.FavoriteDao
import com.musicwave.app.core.database.entity.FavoriteTrackEntity
import com.musicwave.app.core.domain.model.ArtistDetail
import com.musicwave.app.core.domain.model.Release
import com.musicwave.app.core.domain.model.ReleaseDetail
import com.musicwave.app.core.domain.model.SearchResults
import com.musicwave.app.core.domain.model.Track
import com.musicwave.app.core.domain.model.TrackToPlay
import com.musicwave.app.core.domain.repository.MusicRepository
import com.musicwave.app.core.network.api.DeezerApi
import com.musicwave.app.core.network.dto.toDetail
import com.musicwave.app.core.network.dto.toDomain
import com.musicwave.app.core.network.dto.toPlay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val api: DeezerApi,
    private val favoriteDao: FavoriteDao
) : MusicRepository {

    override fun search(query: String): Flow<SearchResults> = flow {
        val artists = api.searchArtists(query).data.map { it.toDomain() }
        val albums = api.searchAlbums(query).data.map { it.toDomain() }
        val tracks = api.searchTracks(query).data.map { it.toDomain() }
        emit(SearchResults(artists, albums, tracks))
    }

    override fun chartAlbums(): Flow<PagingData<Release>> = Pager(
        config = PagingConfig(pageSize = Constants.DEFAULT_PAGE_SIZE, enablePlaceholders = false)
    ) { DeezerAlbumPagingSource(api) }.flow

    override fun chartTracks(): Flow<PagingData<Track>> = Pager(
        config = PagingConfig(pageSize = Constants.DEFAULT_PAGE_SIZE, enablePlaceholders = false)
    ) { DeezerTrackPagingSource(api) }.flow

    override fun getArtistDetail(artistId: Long): Flow<ArtistDetail> = flow {
        emit(api.getArtist(artistId).toDetail())
    }

    override fun getArtistAlbums(artistId: Long): Flow<PagingData<Release>> = Pager(
        config = PagingConfig(pageSize = Constants.DEFAULT_PAGE_SIZE, enablePlaceholders = false)
    ) { ArtistAlbumPagingSource(api, artistId) }.flow

    override fun getArtistTopTracks(artistId: Long): Flow<PagingData<Track>> = Pager(
        config = PagingConfig(pageSize = Constants.DEFAULT_PAGE_SIZE, enablePlaceholders = false)
    ) { ArtistTopPagingSource(api, artistId) }.flow

    override fun getAlbumDetail(albumId: Long): Flow<ReleaseDetail> = flow {
        emit(api.getAlbum(albumId).toDetail())
    }

    override fun getAlbumTracks(albumId: Long): Flow<PagingData<Track>> = Pager(
        config = PagingConfig(pageSize = Constants.DEFAULT_PAGE_SIZE, enablePlaceholders = false)
    ) { AlbumTrackPagingSource(api, albumId) }.flow

    override fun getTrackDetail(trackId: Long): Flow<Track> = flow {
        emit(api.getTrack(trackId).toDomain())
    }

    override fun getFavorites(): Flow<List<TrackToPlay>> =
        favoriteDao.getAll().map { list ->
            list.map {
                TrackToPlay(
                    id = it.id, title = it.title, artistName = it.artistName,
                    releaseTitle = it.releaseTitle ?: "", imageUrl = it.imageUrl,
                    duration = it.duration
                )
            }
        }

    override suspend fun toggleFavorite(track: TrackToPlay) {
        if (favoriteDao.getById(track.id) != null) {
            favoriteDao.deleteById(track.id)
        } else {
            favoriteDao.insert(FavoriteTrackEntity(
                id = track.id, title = track.title, artistName = track.artistName,
                releaseTitle = track.releaseTitle, imageUrl = track.imageUrl, duration = track.duration
            ))
        }
    }

    override suspend fun removeFavorite(trackId: Long) {
        if (favoriteDao.getById(trackId) != null) favoriteDao.deleteById(trackId)
    }

    override suspend fun isFavorite(trackId: Long): Boolean =
        favoriteDao.getById(trackId) != null
}

/* ---- Paging helpers ---- */

private class DeezerAlbumPagingSource(private val api: DeezerApi) : PagingSource<Int, Release>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Release> {
        val index = params.key ?: 0
        return try {
            val resp = api.chartAlbums(limit = params.loadSize)
            LoadResult.Page(resp.data.map { it.toDomain() },
                prevKey = if (index == 0) null else index - params.loadSize,
                nextKey = resp.next?.let { index + params.loadSize })
        } catch (e: Exception) { LoadResult.Error(e) }
    }
    override fun getRefreshKey(state: PagingState<Int, Release>) = null
}

private class DeezerTrackPagingSource(private val api: DeezerApi) : PagingSource<Int, Track>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> {
        val index = params.key ?: 0
        return try {
            val resp = api.chartTracks(limit = params.loadSize)
            LoadResult.Page(resp.data.map { it.toDomain() },
                prevKey = if (index == 0) null else index - params.loadSize,
                nextKey = resp.next?.let { index + params.loadSize })
        } catch (e: Exception) { LoadResult.Error(e) }
    }
    override fun getRefreshKey(state: PagingState<Int, Track>) = null
}

private class ArtistAlbumPagingSource(
    private val api: DeezerApi, private val artistId: Long
) : PagingSource<Int, Release>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Release> {
        val index = params.key ?: 0
        return try {
            val resp = api.getArtistAlbums(artistId, index, params.loadSize)
            LoadResult.Page(resp.data.map { it.toDomain() },
                prevKey = if (index == 0) null else index - params.loadSize,
                nextKey = resp.next?.let { index + params.loadSize })
        } catch (e: Exception) { LoadResult.Error(e) }
    }
    override fun getRefreshKey(state: PagingState<Int, Release>) = null
}

private class ArtistTopPagingSource(
    private val api: DeezerApi, private val artistId: Long
) : PagingSource<Int, Track>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> {
        val index = params.key ?: 0
        return try {
            val resp = api.getArtistTopTracks(artistId, index, params.loadSize)
            LoadResult.Page(resp.data.map { it.toDomain() },
                prevKey = if (index == 0) null else index - params.loadSize,
                nextKey = resp.next?.let { index + params.loadSize })
        } catch (e: Exception) { LoadResult.Error(e) }
    }
    override fun getRefreshKey(state: PagingState<Int, Track>) = null
}

private class AlbumTrackPagingSource(
    private val api: DeezerApi, private val albumId: Long
) : PagingSource<Int, Track>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> {
        val index = params.key ?: 0
        return try {
            val resp = api.searchTracks("", index, params.loadSize)
            LoadResult.Page(resp.data.map { it.toDomain() },
                prevKey = if (index == 0) null else index - params.loadSize,
                nextKey = resp.next?.let { index + params.loadSize })
        } catch (e: Exception) { LoadResult.Error(e) }
    }
    override fun getRefreshKey(state: PagingState<Int, Track>) = null
}