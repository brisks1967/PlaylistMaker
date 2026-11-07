package com.practicum.playlistmaker.data.dto

data class iTunesResponse(
    val searchType: String,
    val expression: String,
    val results: List<TrackDTO>
)