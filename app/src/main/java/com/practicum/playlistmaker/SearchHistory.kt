package com.practicum.playlistmaker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val TRACK_HISTORY = "track_history"
const val KEY_HISTORY = "search_history_key"

class SearchHistory (private val context: Context) {
    private val MAX_ITEMS = 10
    private val prefs = context.getSharedPreferences(TRACK_HISTORY, MODE_PRIVATE)
    private val gson = Gson()


     fun addTrack(trackId: String){
        if (trackId.isBlank()) return

        val currentId = getTrackIDList()
        val noDuplicate = currentId.filterNot { it==trackId }

         val newListId = listOf(trackId) + noDuplicate

         val finalId = if (newListId.size > MAX_ITEMS) {
             newListId.subList(0, MAX_ITEMS)
        } else {
             newListId
        }

        saveIdsToPrefs(finalId)
    }


     fun getTrackIDList(): List<String> {
        val json = prefs.getString(KEY_HISTORY, null)

        if (json.isNullOrBlank()) {
            return emptyList()
        }

        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
             emptyList()
        }
    }

     fun clearTrack(){
        prefs.edit()
            .remove(KEY_HISTORY)
            .apply()
    }

      fun saveIdsToPrefs(track: List<String>) {
        val json = gson.toJson(track)
        prefs.edit()
            .putString(KEY_HISTORY, json)
            .apply()
    }
}