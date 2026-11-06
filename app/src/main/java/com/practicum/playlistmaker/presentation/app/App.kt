package com.practicum.playlistmaker.presentation.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.impl.SettingsInteractor

class App : Application() {

    lateinit var isDark: SettingsInteractor

    override fun onCreate() {
        super.onCreate()
        isDark = Creator.createThemeInteractor(this)
        applySavedTheme()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        isDark.setDarkTheme(darkThemeEnabled)

            if (darkThemeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
    }

    private fun applySavedTheme() {

            if (isDark.isDarkThemeEnabled()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
    }
}