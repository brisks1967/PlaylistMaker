package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TrackRepository {
    fun getTrackById(id: Long): Track?
    fun searchTracks(query: String): List<Track>
    fun saveHistory(id: String)
    fun getHistory(): List<String>

    fun clearHistory()

    fun getTrackListByIds(ids: List<Long>): List<Track>
}