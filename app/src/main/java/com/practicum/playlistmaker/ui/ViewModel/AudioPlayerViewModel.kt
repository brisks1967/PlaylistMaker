package com.practicum.playlistmaker.ui.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.data.network.MediaPlayerImpl
import com.practicum.playlistmaker.domain.api.InterfacePlayer
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.Job
import com.practicum.playlistmaker.domain.api.PlayerCallback
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


class AudioPlayerViewModel(
    private val mediaPlayer : InterfacePlayer
): ViewModel() {
    // организация подписки
    private val statePlayer0 = MutableLiveData<PlayerUiState>(
        PlayerUiState(
        playerState = PlayerState.STATE_DEFAULT,
        currentPositionMs = 0L,
        track = null
    ))
    val statePlayer: LiveData<PlayerUiState> get() = statePlayer0

    private val trackData0 = MutableLiveData<Track>()
    val trackData: LiveData<Track> = trackData0

    private var progressJob: Job? = null

    fun setTrack(track: Track) {
        statePlayer0.value = statePlayer0.value?.copy(track = track)
        trackData0.value = track
        val url = track.previewUrl
        if (url.isNullOrBlank()) return

        mediaPlayer.setPlayerCallback(object : PlayerCallback {
            override fun onPrepared() {
                statePlayer0.value = statePlayer0.value?.copy(
                    playerState = PlayerState.STATE_PREPARED,
                    currentPositionMs = 0L
                )
                startProgressTimer()
            }

            override fun onCompleted() {
                stopProgressTimer()
                statePlayer0.value = statePlayer0.value?.copy(
                    playerState = PlayerState.STATE_PREPARED,
                    currentPositionMs = 0L
                )
            }

            override fun onError() {
                stopProgressTimer()
                statePlayer0.value = statePlayer0.value?.copy(
                    playerState = PlayerState.STATE_ERROR
                )
            }
        })
        mediaPlayer.prepare(url)
    }

    fun playbackControl() {
        val currentState = statePlayer0.value?.playerState ?: PlayerState.STATE_DEFAULT

        when (currentState) {
            PlayerState.STATE_PLAYING -> {
                mediaPlayer.pause()
                statePlayer0.value = statePlayer0.value?.copy(playerState = PlayerState.STATE_PAUSED)
            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                mediaPlayer.play()
                statePlayer0.value = statePlayer0.value?.copy(playerState = PlayerState.STATE_PLAYING)
                startProgressTimer()
            }
            else -> {}
        }
    }
    private fun startProgressTimer() {
        progressJob = viewModelScope.launch {
            while (true) {
                val progress = mediaPlayer.getProgress()
                statePlayer0.value = statePlayer0.value?.copy(currentPositionMs = progress)
                delay(1000)
            }
        }
    }
             private fun stopProgressTimer() {
                progressJob?.cancel()
                progressJob = null
            }

            fun pauseOnScreenHide() {
                if (statePlayer0.value?.playerState == PlayerState.STATE_PLAYING) {
                    mediaPlayer.pause()
                    statePlayer0.value = statePlayer0.value?.copy(playerState = PlayerState.STATE_PAUSED)
                    stopProgressTimer()
                }
            }

           override fun onCleared() {
               mediaPlayer.release()
                super.onCleared()
            }
}
class AudioPlayerViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioPlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            //Создаем реализацию (MediaPlayerImpl)
            val player = MediaPlayerImpl()
            //Передаем её в ViewModel
            return AudioPlayerViewModel(player) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}