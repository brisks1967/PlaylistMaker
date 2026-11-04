package com.practicum.playlistmaker.data.local

import android.content.Context
import com.practicum.playlistmaker.domain.api.ThemeRepository
import com.practicum.playlistmaker.presentation.ui_const.AppConst

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