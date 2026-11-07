package com.practicum.playlistmaker.presentation.app

import android.content.Context
import com.practicum.playlistmaker.data.dto.TrackConverter
import com.practicum.playlistmaker.data.local.SearchHistory
import com.practicum.playlistmaker.data.local.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.network.TrackRepositoryImpl
import com.practicum.playlistmaker.domain.api.InterfacePlayTrackInteractor
import com.practicum.playlistmaker.domain.api.InterfaceSearchTracksInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.ThemeRepository
import com.practicum.playlistmaker.domain.api.TrackRepository
import com.practicum.playlistmaker.domain.impl.PlayTrackInteractor
import com.practicum.playlistmaker.domain.impl.SearchTracksInteractor
import com.practicum.playlistmaker.domain.impl.SettingsInteractor

object Creator {

    private fun createClient(): RetrofitNetworkClient {
        return RetrofitNetworkClient()
    }
    private fun createTrackConverter(): TrackConverter {
        return TrackConverter()
    }

    //Обработка поиска треков
    fun createSearchTracksInteractor(context: Context): InterfaceSearchTracksInteractor {
        val repo = createTrackRepository(context)
        return SearchTracksInteractor(repo)
    }

    //обработка Истории поиска треков - history
    fun createTrackRepository(context: Context): TrackRepository {
        val client = createClient()
        val converter = createTrackConverter()
        val history = createHistory(context)

        return TrackRepositoryImpl(client, converter, history)
    }

    fun createPlayTrackInteractor(context: Context): InterfacePlayTrackInteractor {
        val history = createHistory(context)
        return PlayTrackInteractor(history)
    }
    private fun createHistory(context: Context): SearchHistoryRepository {
        return SearchHistory(context)
    }
    fun createThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(context)
    }
    fun createThemeInteractor(context: Context): SettingsInteractor {
        val repository = createThemeRepository(context)
        return SettingsInteractor (repository)
    }

    }