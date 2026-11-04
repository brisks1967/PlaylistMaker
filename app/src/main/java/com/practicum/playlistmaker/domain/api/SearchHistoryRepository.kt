package com.practicum.playlistmaker.domain.api

interface SearchHistoryRepository {
    fun addTrack(id: String)
    fun getTrackIDList(): List<String>
    fun clearTrack()
}