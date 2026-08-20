package com.practicum.playlistmaker

data class iTunesResponse(
    val searchType: String,
    val expression: String,
    val results: List<Track>
)
