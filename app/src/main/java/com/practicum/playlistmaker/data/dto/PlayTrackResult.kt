package com.practicum.playlistmaker.data.dto

data class PlayTrackResult(
    val shouldSearch: Boolean,
    val searchQuery: String?,
    val shouldHideHistory: Boolean

)
