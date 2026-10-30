package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.models.ThemeRepository


class App : Application() {

    private lateinit var themeRepository: ThemeRepository

    override fun onCreate() {
        super.onCreate()
        themeRepository = Creator.createThemeRepository(this)
        applySavedTheme()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        themeRepository.setDarkThemeEnabled(darkThemeEnabled)

            if (darkThemeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
    }

    private fun applySavedTheme() {

            if (themeRepository.isDarkThemeEnabled()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
    }
}