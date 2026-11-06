package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.ThemeRepository



class SettingsInteractor (private val themeRepository: ThemeRepository){

    fun isDarkThemeEnabled(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }
    fun setDarkTheme(isEnabled: Boolean) {
        themeRepository.setDarkThemeEnabled(isEnabled)
    }
}