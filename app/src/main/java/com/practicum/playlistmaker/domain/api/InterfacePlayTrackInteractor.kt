package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.prog.PlayTrackResult
import com.practicum.playlistmaker.domain.models.Track

interface InterfacePlayTrackInteractor {
    fun workTrack(track: Track): PlayTrackResult
}