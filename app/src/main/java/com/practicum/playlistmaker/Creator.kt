package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.network.SearchHistory
import com.practicum.playlistmaker.data.network.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.dto.TrackConverter
import com.practicum.playlistmaker.data.network.TrackRepositoryImpl
import com.practicum.playlistmaker.domain.models.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.ThemeRepository
import com.practicum.playlistmaker.domain.models.TrackRepository
import com.practicum.playlistmaker.domain.models.PlayTrackInteractor
import com.practicum.playlistmaker.domain.models.SearchTracksInteractor


object Creator {

    fun createClient(): RetrofitNetworkClient {
        return RetrofitNetworkClient()
    }
    fun createTrackConverter(): TrackConverter {
        return TrackConverter()
    }

    //Обработка поиска треков
    fun createSearchTracksInteractor(context: Context): SearchTracksInteractor {
        val client = createTrackRepository(context)
        return SearchTracksInteractor(client)
    }

    //обработка Истории поиска треков - history
    fun createTrackRepository(context: Context): TrackRepository {
        val client = createClient()
        val converter = createTrackConverter()
        val history = createHistory(context)

        return TrackRepositoryImpl(client, converter, history)
    }
    fun createThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(context)
    }
    fun createPlayTrackInteractor(context: Context): PlayTrackInteractor {
        val history = createHistory(context)
        return PlayTrackInteractor(history)
    }
    private fun createHistory(context: Context): SearchHistoryRepository {
        return SearchHistory(context)
    }

    }