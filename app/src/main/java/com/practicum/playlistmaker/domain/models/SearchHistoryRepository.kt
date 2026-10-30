package com.practicum.playlistmaker.domain.models

interface SearchHistoryRepository {
    fun addTrack(id: String)
    fun getTrackIDList(): List<String>
    fun clearTrack()
}