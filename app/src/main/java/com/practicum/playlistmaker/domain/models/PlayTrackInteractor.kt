package com.practicum.playlistmaker.domain.models


class PlayTrackInteractor (
    private val searchHistory: SearchHistoryRepository
) {
    data class Result(
        val shouldSearch: Boolean,
        val searchQuery: String?,
        val shouldHideHistory: Boolean
    )

        fun workTrack(track: Track): Result {
            searchHistory.addTrack(track.trackId.toString())
            return if (track.mark == 1) {
                Result(shouldSearch = true, searchQuery = track.trackName, shouldHideHistory = true)
            } else {
                Result(shouldSearch = false, searchQuery = null, shouldHideHistory = false)
            }
        }
}