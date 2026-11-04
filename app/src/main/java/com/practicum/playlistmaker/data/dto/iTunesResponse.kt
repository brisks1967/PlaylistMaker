package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.data.dto.TrackDTO

data class iTunesResponse(
    val searchType: String,
    val expression: String,
    val results: List<TrackDTO>
)