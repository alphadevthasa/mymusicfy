package com.musicwave.app.core.network.dto

import com.musicwave.app.core.domain.model.Artist
import com.musicwave.app.core.domain.model.ArtistDetail
import com.musicwave.app.core.domain.model.Release
import com.musicwave.app.core.domain.model.ReleaseDetail
import com.musicwave.app.core.domain.model.Track
import com.musicwave.app.core.domain.model.TrackToPlay

/* ---- Artist ---- */

fun DeezerArtistDto.toDomain(): Artist = Artist(
    id = id,
    name = name,
    imageUrl = pictureMedium ?: picture
)

fun DeezerArtistDto.toDetail(): ArtistDetail = ArtistDetail(
    id = id,
    name = name,
    imageUrl = pictureXl ?: pictureBig ?: picture,
    nbAlbum = nbAlbum,
    nbFan = nbFan
)

/* ---- Album (Release / ReleaseDetail) ---- */

private fun DeezerAlbumDto.toReleaseBase(): Release = Release(
    id = id,
    title = title,
    type = recordType,
    barcode = upc,
    year = releaseDate?.take(4)?.toIntOrNull(),
    explicitContent = explicitLyrics,
    imageUrl = coverMedium ?: cover,
    label = label,
    artist = artist?.toDomain(),
    popularity = (fans ?: 0).toDouble(),
    trackCount = nbTracks ?: 0,
    duration = duration
)

fun DeezerAlbumDto.toDomain(): Release = toReleaseBase()

fun DeezerAlbumDto.toDetail(): ReleaseDetail = ReleaseDetail(
    id = id,
    title = title,
    type = recordType,
    barcode = upc,
    year = releaseDate?.take(4)?.toIntOrNull(),
    explicitContent = explicitLyrics,
    imageUrl = coverBig ?: cover,
    label = label,
    artist = artist?.toDomain(),
    popularity = (fans ?: 0).toDouble(),
    trackCount = nbTracks ?: 0,
    duration = duration,
    tracks = (tracks?.data ?: emptyList()).map { it.toDomain() }
)

/* ---- Track ---- */

fun DeezerTrackDto.toDomain(): Track = Track(
    id = id,
    title = title,
    version = titleVersion?.ifBlank { null },
    trackNumber = trackPosition,
    discNumber = diskNumber,
    duration = duration,
    explicitContent = explicitLyrics,
    isrc = isrc,
    type = "audio",
    artist = artist?.toDomain(),
    release = album?.toReleaseBase(),
    popularity = (rank ?: 0).toDouble(),
    previewUrl = preview
)

fun DeezerTrackDto.toPlay(releaseTitle: String? = null): TrackToPlay = TrackToPlay(
    id = id,
    title = title,
    artistName = artist?.name ?: "",
    releaseTitle = releaseTitle ?: album?.title ?: "",
    imageUrl = album?.coverMedium ?: artist?.pictureMedium,
    duration = duration,
    previewUrl = preview
)
