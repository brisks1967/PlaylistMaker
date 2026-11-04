package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.data.dto.PlayTrackResult
import com.practicum.playlistmaker.domain.api.InterfacePlayTrackInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class PlayTrackInteractor (
    private val searchHistory: SearchHistoryRepository
): InterfacePlayTrackInteractor {
    override fun workTrack(track: Track): PlayTrackResult {

            searchHistory.addTrack(track.trackId.toString())

            return if (track.mark == 1) {
                PlayTrackResult(
                    shouldSearch = true,
                    searchQuery = track.trackName,
                    shouldHideHistory = true
                )
            } else {
                PlayTrackResult(shouldSearch = false, searchQuery = null, shouldHideHistory = false)
            }
        }
}