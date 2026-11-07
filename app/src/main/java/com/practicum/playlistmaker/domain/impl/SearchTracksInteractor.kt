package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.InterfaceSearchTracksInteractor
import com.practicum.playlistmaker.domain.api.TrackRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchTracksInteractor   (
    private val repository: TrackRepository
)  : InterfaceSearchTracksInteractor {
    override fun makeQuery(query: String): List<Track> {
        return repository.searchTracks(query)
    }

    override fun getHistoryTracks(): List<Track> {
        val historyIds = repository.getHistory()

        // Берем треки по ID
        val tracks = repository.getTrackListByIds(historyIds.mapNotNull { it.toLongOrNull() })
        return tracks.map { it.copy(mark = 1)  // Помечаем их как историю (mark = 1)
        }
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    override fun addToHistory(track: Track) {
        repository.saveHistory(track.trackId.toString())
    }
}