package com.practicum.playlistmaker.ui.ViewModel

import android.app.Application
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.domain.api.InterfacePlayTrackInteractor
import com.practicum.playlistmaker.domain.api.InterfaceSearchTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui_const.AppConst
import java.util.Locale

class AudioPlayerViewModel: ViewModel() {

    private val mediaPlayer = MediaPlayer()
    private var track: Track? = null

    // организация подписки
    private val statePlayer0 = MutableLiveData<PlayerUiState>(
        PlayerUiState(
        playerState = PlayerState.STATE_DEFAULT,
        currentPositionMs = 0L,
        track = null
    ))
    val statePlayer: LiveData<PlayerUiState> get() = statePlayer0

    private val mainThreadHandler = Handler(Looper.getMainLooper())


    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            statePlayer0.value = statePlayer0.value?.copy(
                currentPositionMs = mediaPlayer.currentPosition.toLong()
            )
            mainThreadHandler.postDelayed(this, AppConst.THREAD_DELAY)
        }
    }

    fun setTrack(track: Track) {
        statePlayer0.value = statePlayer0.value?.copy(track = track)

        val url = track.previewUrl
        if (url.isNullOrBlank()) return

        try {
            mediaPlayer.setDataSource(url)

            mediaPlayer.setOnPreparedListener {
                statePlayer0.value = statePlayer0.value?.copy(
                    playerState = PlayerState.STATE_PREPARED
                )
            }
            mediaPlayer.setOnCompletionListener {
                mainThreadHandler.removeCallbacks(updateTimerRunnable)
                statePlayer0.value = statePlayer0.value?.copy(
                    playerState = PlayerState.STATE_PREPARED,
                    currentPositionMs = 0L
                )
            }
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
            statePlayer0.value = statePlayer0.value?.copy(playerState = PlayerState.STATE_DEFAULT)
        }
    }

    fun playbackControl() {
        val state = statePlayer0.value?.playerState

        when (state) {
            PlayerState.STATE_PLAYING -> pause()
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> play()
            else -> Log.d("VM", "Ignore click, state=$state")
        }
    }

    private fun play() {

        mediaPlayer.start()
        statePlayer0.value = statePlayer0.value?.copy(playerState = PlayerState.STATE_PLAYING)
        mainThreadHandler.removeCallbacks(updateTimerRunnable)
        mainThreadHandler.post(updateTimerRunnable)
    }

    private fun pause() {
        mediaPlayer.pause()
        statePlayer0.value = statePlayer0.value?.copy(playerState = PlayerState.STATE_PAUSED)
        mainThreadHandler.removeCallbacks(updateTimerRunnable)
    }

    fun pauseOnScreenHide() {
        // вызывать из Activity.onPause()
        if (statePlayer0.value?.playerState == PlayerState.STATE_PLAYING) {
            pause()
        }
    }

    override fun onCleared() {
        mainThreadHandler.removeCallbacks(updateTimerRunnable)
        mediaPlayer.release()
        super.onCleared()
    }

}