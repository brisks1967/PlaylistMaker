package com.practicum.playlistmaker.domain.models

interface TrackRepository {
    fun getTrackById(id: Long): Track?
    fun searchTracks(query: String): List<Track>
    fun saveHistory(id: String)
    fun getHistory(): List<String>

    fun clearHistory()
}