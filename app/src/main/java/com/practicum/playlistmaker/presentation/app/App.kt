package com.practicum.playlistmaker.presentation.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.domainModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.domain.impl.SettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application(){

    lateinit var isDark: SettingsInteractor

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App) // Передаем контекст приложения, чтобы Koin мог работать с Android-API, SharedPreferences ...
            modules(listOf(dataModule, domainModule, viewModelModule)) // Подключаем модули с зависимостями
         }
        isDark = org.koin.core.context.GlobalContext.get().get<SettingsInteractor>() // Подключение темы

        applySavedTheme() // Применяем тему
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