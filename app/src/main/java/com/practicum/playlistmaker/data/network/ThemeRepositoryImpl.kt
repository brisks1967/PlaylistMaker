package com.practicum.playlistmaker.data.network

import android.content.Context
import com.practicum.playlistmaker.domain.models.AppConst
import com.practicum.playlistmaker.domain.models.ThemeRepository

class ThemeRepositoryImpl(
    private val context: Context
) : ThemeRepository {

    private val sharedPrefs = context.getSharedPreferences(AppConst.PRACTICUM_PREFERENCES, Context.MODE_PRIVATE)

    override fun isDarkThemeEnabled(): Boolean {
        // Читаем сохраненное значение. Если ключа нет, считаем, что тема светлая (false)
        return sharedPrefs.getBoolean(AppConst.THEME_IS_DARK_KEY, false)
    }

    override fun setDarkThemeEnabled(isEnabled: Boolean) {
             sharedPrefs.edit()
            .putBoolean(AppConst.THEME_IS_DARK_KEY, isEnabled)
            .apply()
    }
}