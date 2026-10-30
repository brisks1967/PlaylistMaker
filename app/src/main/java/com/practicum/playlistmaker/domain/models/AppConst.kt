package com.practicum.playlistmaker.domain.models

object AppConst {
    const val SEARCH_DEBOUNCE_DELAY = 2000L // задержка обработки поля ввода - поиск трека
    const val TRACK_CLICK_DEBOUNCE_DELAY = 300L // задержка клика по треку

    const val STATE_DEFAULT = 0
    const val STATE_PREPARED = 1
    const val STATE_PLAYING = 2
    const val STATE_PAUSED = 3
    const val THREAD_DELAY = 300L // задержка обновления таймера
    const val THREAD_DELAY_PLAY_BUTTON = 300L // задержка клика по кнопке PLAY

    const val THEME_IS_DARK_KEY= "is_dark_theme" // ключ для светлой-темной темы

    const val PRACTICUM_PREFERENCES = "practicum_preferences"
}