package com.practicum.playlistmaker.domain.utils
import android.os.Handler
import android.os.Looper

 fun Runnable.searchDebounce(delayMs: Long) {
     val handler = Handler(Looper.getMainLooper())
    handler.removeCallbacks(this)
    handler.postDelayed(this, delayMs)
}