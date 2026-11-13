package com.practicum.playlistmaker.data.network

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.api.InterfacePlayer
import com.practicum.playlistmaker.domain.api.PlayerCallback

class MediaPlayerImpl  : InterfacePlayer {
    private val mediaPlayer = MediaPlayer()
    private var callback: PlayerCallback? = null

    override fun setPlayerCallback(callback: PlayerCallback) {
        this.callback = callback
    }

    override fun prepare(url: String) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener { callback?.onPrepared() }
            mediaPlayer.setOnCompletionListener { callback?.onCompleted() }
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            callback?.onError()
        }
    }

    override fun play() {
        if (!mediaPlayer.isPlaying) mediaPlayer.start()
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getProgress(): Long {
        return mediaPlayer.currentPosition.toLong()
    }

}