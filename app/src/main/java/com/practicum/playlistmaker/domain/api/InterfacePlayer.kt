package com.practicum.playlistmaker.domain.api

interface InterfacePlayer {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun getProgress(): Long // Текущее время в мс
    fun release()
    fun setPlayerCallback(callback: PlayerCallback) // Для уведомлений
}

interface PlayerCallback {
    fun onPrepared()
    fun onCompleted()
    fun onError()
}