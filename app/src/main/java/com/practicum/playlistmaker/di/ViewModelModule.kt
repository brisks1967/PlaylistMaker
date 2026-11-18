package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.network.MediaPlayerImpl
import com.practicum.playlistmaker.domain.api.InterfacePlayer
import com.practicum.playlistmaker.ui.ViewModel.AudioPlayerViewModel
import com.practicum.playlistmaker.ui.ViewModel.SearchViewModel
import com.practicum.playlistmaker.ui.ViewModel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Регистрация SearchViewModel
    viewModel { SearchViewModel(get(), get()) }

    //Регистрация реализации MediaPlayerImpl
    single<InterfacePlayer> { MediaPlayerImpl() }

    // Регистрация AudioPlayerViewModel
    viewModel { AudioPlayerViewModel(get()) }

    // Регистрация SettingsViewModel
    viewModel { SettingsViewModel(get()) }
}