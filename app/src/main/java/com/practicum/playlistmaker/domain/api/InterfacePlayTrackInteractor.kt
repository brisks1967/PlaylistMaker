package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.dto.PlayTrackResult
import com.practicum.playlistmaker.domain.models.Track

interface InterfacePlayTrackInteractor {
    fun workTrack(track: Track): PlayTrackResult
}