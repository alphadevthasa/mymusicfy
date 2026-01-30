package com.musicwave.app.core.network.api

import com.musicwave.app.core.network.dto.DeezerAlbumDto
import com.musicwave.app.core.network.dto.DeezerAlbumListDto
import com.musicwave.app.core.network.dto.DeezerArtistDto
import com.musicwave.app.core.network.dto.DeezerArtistListDto
import com.musicwave.app.core.network.dto.DeezerTrackDto
import com.musicwave.app.core.network.dto.DeezerTrackListDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerApi {

    /* ---- Search ---- */
    @GET("search/artist")
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("index") index: Int = 0,
        @Query("limit") limit: Int = 20
    ): DeezerArtistListDto

    @GET("search/album")
    suspend fun searchAlbums(
        @Query("q") query: String,
        @Query("index") index: Int = 0,
        @Query("limit") limit: Int = 20
    ): DeezerAlbumListDto

    @GET("search/track")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("index") index: Int = 0,
        @Query("limit") limit: Int = 20
    ): DeezerTrackListDto

    /* ---- Browse / editorial charts ---- */
    @GET("chart/0/tracks")
    suspend fun chartTracks(
        @Query("limit") limit: Int = 20
    ): DeezerTrackListDto

    @GET("chart/0/albums")
    suspend fun chartAlbums(
        @Query("limit") limit: Int = 20
    ): DeezerAlbumListDto

    /* ---- Artist ---- */
    @GET("artist/{id}")
    suspend fun getArtist(
        @Path("id") artistId: Long
    ): DeezerArtistDto

    @GET("artist/{id}/albums")
    suspend fun getArtistAlbums(
        @Path("id") artistId: Long,
        @Query("index") index: Int = 0,
        @Query("limit") limit: Int = 20
    ): DeezerAlbumListDto

    @GET("artist/{id}/top")
    suspend fun getArtistTopTracks(
        @Path("id") artistId: Long,
        @Query("index") index: Int = 0,
        @Query("limit") limit: Int = 20
    ): DeezerTrackListDto

    /* ---- Album ---- */
    @GET("album/{id}")
    suspend fun getAlbum(
        @Path("id") albumId: Long
    ): DeezerAlbumDto

    /* ---- Track ---- */
    @GET("track/{id}")
    suspend fun getTrack(
        @Path("id") trackId: Long
    ): DeezerTrackDto
}
