package com.musicwave.app.core.domain.model

data class Artist(
    val id: Long,
    val name: String,
    val imageUrl: String? = null,
    val slug: String? = null,
    val appearsAs: String? = null,
    val isPlaceholderImage: Boolean = false
)

data class ArtistDetail(
    val id: Long,
    val name: String,
    val imageUrl: String? = null,
    val nbAlbum: Int? = null,
    val nbFan: Int? = null
)

data class Release(
    val id: Long,
    val title: String,
    val version: String? = null,
    val type: String? = null,
    val barcode: String? = null,
    val year: Int? = null,
    val explicitContent: Boolean = false,
    val imageUrl: String? = null,
    val slug: String? = null,
    val label: String? = null,
    val licensor: String? = null,
    val artist: Artist? = null,
    val popularity: Double = 0.0,
    val duration: Int? = null,
    val trackCount: Int = 0,
    val availableFor: List<String> = emptyList()
)

data class ReleaseDetail(
    val id: Long,
    val title: String,
    val version: String? = null,
    val type: String? = null,
    val barcode: String? = null,
    val year: Int? = null,
    val explicitContent: Boolean = false,
    val imageUrl: String? = null,
    val slug: String? = null,
    val label: String? = null,
    val licensor: String? = null,
    val artist: Artist? = null,
    val popularity: Double = 0.0,
    val duration: Int? = null,
    val trackCount: Int = 0,
    val availableFor: List<String> = emptyList(),
    val tracks: List<Track> = emptyList()
)

data class Track(
    val id: Long,
    val title: String,
    val version: String? = null,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val duration: Int? = null,
    val explicitContent: Boolean = false,
    val isrc: String? = null,
    val type: String? = null,
    val artist: Artist? = null,
    val release: Release? = null,
    val popularity: Double = 0.0,
    val availableFor: List<String> = emptyList(),
    val previewUrl: String? = null
)

data class SearchResults(
    val artists: List<Artist>,
    val releases: List<Release>,
    val tracks: List<Track>
)

data class TrackToPlay(
    val id: Long,
    val title: String,
    val artistName: String,
    val releaseTitle: String,
    val imageUrl: String?,
    val duration: Int?,
    val previewUrl: String? = null
)