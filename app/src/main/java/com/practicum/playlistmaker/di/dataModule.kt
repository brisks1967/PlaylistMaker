package com.practicum.playlistmaker.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.data.dto.TrackConverter
import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.network.SharedPreferencesSearchHistoryStorage
import com.practicum.playlistmaker.data.network.TrackRepositoryImpl
import com.practicum.playlistmaker.data.network.iTunesSearchAPI
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.TrackRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single { Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build() }

    single<iTunesSearchAPI> { get<Retrofit>().create(iTunesSearchAPI::class.java) }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    factory { Gson() }

    single { androidContext().getSharedPreferences("local_storage", Context.MODE_PRIVATE) }

    single { TrackConverter() }

    single<SearchHistoryRepository> {
        SharedPreferencesSearchHistoryStorage(get(), get())
    }

    single<TrackRepository> { TrackRepositoryImpl(get(), get(), get()) }

}