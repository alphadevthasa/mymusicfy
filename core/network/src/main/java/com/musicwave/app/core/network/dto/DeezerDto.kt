package com.musicwave.app.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/* ---- Concrete list wrappers ---- */

@JsonClass(generateAdapter = true)
data class DeezerTrackListDto(
    @Json(name = "data") val data: List<DeezerTrackDto> = emptyList(),
    @Json(name = "next") val next: String? = null,
    @Json(name = "total") val total: Int? = null
)

@JsonClass(generateAdapter = true)
data class DeezerAlbumListDto(
    @Json(name = "data") val data: List<DeezerAlbumDto> = emptyList(),
    @Json(name = "next") val next: String? = null,
    @Json(name = "total") val total: Int? = null
)

@JsonClass(generateAdapter = true)
data class DeezerArtistListDto(
    @Json(name = "data") val data: List<DeezerArtistDto> = emptyList(),
    @Json(name = "next") val next: String? = null,
    @Json(name = "total") val total: Int? = null
)

@JsonClass(generateAdapter = true)
data class DeezerTrackListNested(
    @Json(name = "data") val data: List<DeezerTrackDto> = emptyList()
)

/* ---- Artist ---- */

@JsonClass(generateAdapter = true)
data class DeezerArtistDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "picture") val picture: String? = null,
    @Json(name = "picture_medium") val pictureMedium: String? = null,
    @Json(name = "picture_big") val pictureBig: String? = null,
    @Json(name = "picture_xl") val pictureXl: String? = null,
    @Json(name = "tracklist") val tracklist: String? = null,
    @Json(name = "nb_album") val nbAlbum: Int? = null,
    @Json(name = "nb_fan") val nbFan: Int? = null
)

/* ---- Album ---- */

@JsonClass(generateAdapter = true)
data class DeezerAlbumDto(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "upc") val upc: String? = null,
    @Json(name = "cover") val cover: String? = null,
    @Json(name = "cover_medium") val coverMedium: String? = null,
    @Json(name = "cover_big") val coverBig: String? = null,
    @Json(name = "cover_xl") val coverXl: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "record_type") val recordType: String? = null,
    @Json(name = "explicit_lyrics") val explicitLyrics: Boolean = false,
    @Json(name = "artist") val artist: DeezerArtistDto? = null,
    @Json(name = "nb_tracks") val nbTracks: Int? = null,
    @Json(name = "duration") val duration: Int? = null,
    @Json(name = "label") val label: String? = null,
    @Json(name = "fans") val fans: Int? = null,
    @Json(name = "tracks") val tracks: DeezerTrackListNested? = null,
    @Json(name = "tracklist") val tracklist: String? = null
)

/* ---- Track ---- */

@JsonClass(generateAdapter = true)
data class DeezerTrackDto(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "title_version") val titleVersion: String? = null,
    @Json(name = "duration") val duration: Int? = null,
    @Json(name = "track_position") val trackPosition: Int? = null,
    @Json(name = "disk_number") val diskNumber: Int? = null,
    @Json(name = "isrc") val isrc: String? = null,
    @Json(name = "explicit_lyrics") val explicitLyrics: Boolean = false,
    @Json(name = "preview") val preview: String? = null,
    @Json(name = "rank") val rank: Long? = null,
    @Json(name = "artist") val artist: DeezerArtistDto? = null,
    @Json(name = "album") val album: DeezerAlbumDto? = null,
    @Json(name = "readable") val readable: Boolean? = null,
    @Json(name = "title_short") val titleShort: String? = null,
    @Json(name = "link") val link: String? = null,
    @Json(name = "md5_image") val md5Image: String? = null,
    @Json(name = "type") val typeName: String? = null
)