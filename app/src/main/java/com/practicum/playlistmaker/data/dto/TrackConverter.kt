package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.TrackDTO
import com.practicum.playlistmaker.domain.models.Track

class TrackConverter {
    fun convertSingle(dto: TrackDTO): Track {
        return Track(
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100,
            trackId = dto.trackId,
            collectionName = dto.collectionName,
            country = dto.country,
            primaryGenreName = dto.primaryGenreName,
            releaseDate = convertDateInYear(dto.releaseDate), //год релиза
            previewUrl = dto.previewUrl
        )
    }
    fun convertDateInYear(releaseDate:String?) :String {
        return releaseDate?.take(4) ?: ""
    }

}