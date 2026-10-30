package com.practicum.playlistmaker.domain.models


class SearchTracksInteractor   (
    private val repository: TrackRepository
) {
    fun makeQuery(query: String): List<Track> {
        return repository.searchTracks(query)
    }
}
