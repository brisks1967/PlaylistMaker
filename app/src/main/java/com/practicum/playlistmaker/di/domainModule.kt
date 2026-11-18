package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.local.ThemeRepositoryImpl
import com.practicum.playlistmaker.domain.api.InterfacePlayTrackInteractor
import com.practicum.playlistmaker.domain.api.InterfaceSearchTracksInteractor
import com.practicum.playlistmaker.domain.api.ThemeRepository
import com.practicum.playlistmaker.domain.impl.PlayTrackInteractor
import com.practicum.playlistmaker.domain.impl.SearchTracksInteractor
import com.practicum.playlistmaker.domain.impl.SettingsInteractor
import com.practicum.playlistmaker.ui.ViewModel.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val domainModule = module {
    single<InterfaceSearchTracksInteractor> {
        SearchTracksInteractor(get()) // получает TrackRepository
    }
    single<InterfacePlayTrackInteractor> {
        PlayTrackInteractor(get()) //получает SearchHistoryRepository
    }

    single<ThemeRepository> { ThemeRepositoryImpl(androidContext()) }

    single<SettingsInteractor> { SettingsInteractor(get()) }

}