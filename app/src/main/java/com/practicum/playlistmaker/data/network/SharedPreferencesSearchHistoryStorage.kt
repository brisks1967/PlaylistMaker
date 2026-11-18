package com.practicum.playlistmaker.data.network

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.local.KEY_HISTORY
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository

const val KEY_HISTORY = "search_history_key"

class SharedPreferencesSearchHistoryStorage(
    private val prefs: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    private val MAX_ITEMS = 10

    override fun addTrack(id: String){
        if (id.isBlank()) return

        val currentIds = getTrackIDList().toMutableList() // получаем список треков
        val noDuplicate = currentIds.filterNot { it == id } // Убираем дубликаты, сохраняя порядок (новый ID в начале)
        val newList = listOf(id) + noDuplicate // добавляем в список первым

        val finalList = if (newList.size > MAX_ITEMS) {
            newList.subList(0, MAX_ITEMS)
        } else {
            newList
        }
        val json = gson.toJson(finalList)
        prefs.edit()
            .putString(KEY_HISTORY, json)
            .apply()

    }

    override fun getTrackIDList(): List<String>{
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

    override fun clearTrack(){
        prefs.edit()
            .remove(KEY_HISTORY)
            .apply()
    }
}