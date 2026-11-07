package com.practicum.playlistmaker.data.local

import android.content.Context
import com.practicum.playlistmaker.data.dto.DataConst
import com.practicum.playlistmaker.domain.api.ThemeRepository


class ThemeRepositoryImpl(
    private val context: Context
) : ThemeRepository {

    private val sharedPrefs = context.getSharedPreferences(DataConst.PRACTICUM_PREFERENCES, Context.MODE_PRIVATE)

    override fun isDarkThemeEnabled(): Boolean {
        // Читаем сохраненное значение. Если ключа нет, считаем, что тема светлая (false)
        return sharedPrefs.getBoolean(DataConst.THEME_IS_DARK_KEY, false)
    }

    override fun setDarkThemeEnabled(isEnabled: Boolean) {
             sharedPrefs.edit()
            .putBoolean(DataConst.THEME_IS_DARK_KEY, isEnabled)
            .apply()
    }
}