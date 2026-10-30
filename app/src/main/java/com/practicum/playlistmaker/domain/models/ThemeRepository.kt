package com.practicum.playlistmaker.domain.models

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(isEnabled: Boolean)
}