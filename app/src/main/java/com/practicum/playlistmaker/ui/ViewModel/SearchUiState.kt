package com.practicum.playlistmaker.ui.ViewModel

import com.practicum.playlistmaker.domain.models.Track

data class SearchUiState(
    val mode: SearchMode = SearchMode.HISTORY, // Что сейчас показываем: HISTORY или SEARCH
    val isLoading: Boolean = false,  // прогресс бар работает или нет
    val tracks: List<Track> = emptyList(), // Результаты поиска
    val history: List<Track> = emptyList(), // История - треки
    val error: String? = null   // ошибки
)
enum class SearchMode { SEARCH, HISTORY }