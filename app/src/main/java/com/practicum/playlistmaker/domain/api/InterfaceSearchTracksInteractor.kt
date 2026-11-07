package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface InterfaceSearchTracksInteractor {
        fun makeQuery(query: String): List<Track>
        fun getHistoryTracks(): List<Track>   // Возвращает уже готовые треки с mark=1
        fun clearHistory()                     // Очищает историю
        fun addToHistory(track: Track)        // Добавляет трек в историю
}