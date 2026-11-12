package com.practicum.playlistmaker.ui.ViewModel

import com.practicum.playlistmaker.domain.models.Track

data class PlayerUiState(
    val playerState: PlayerState,     //статус плеера
    val currentPositionMs: Long,      // текущая позиция в миллисекундах
    val track: Track?                // трек с параметрами
)
