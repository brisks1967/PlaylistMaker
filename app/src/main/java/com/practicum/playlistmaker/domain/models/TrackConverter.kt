package com.practicum.playlistmaker.domain.models

import android.os.Parcelable
import android.view.View
import com.practicum.playlistmaker.TrackDTO
import kotlinx.parcelize.Parcelize

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
        var yearTrackView:String
        val year = releaseDate?.substring(0, 4)
        if (!year.isNullOrBlank()) {
            yearTrackView= year
        } else {
            yearTrackView= ""
        }
        return yearTrackView
    }

}