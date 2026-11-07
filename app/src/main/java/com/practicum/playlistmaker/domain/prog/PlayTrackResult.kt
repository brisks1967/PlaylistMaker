package com.practicum.playlistmaker.domain.prog

data class PlayTrackResult(
    val shouldSearch: Boolean,
    val searchQuery: String?,
    val shouldHideHistory: Boolean

)