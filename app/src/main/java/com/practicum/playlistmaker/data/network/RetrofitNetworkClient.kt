package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.NetworkResponse
import com.practicum.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTuneService = retrofit.create(iTunesSearchAPI::class.java)


    override fun doRequest(dto: Any): NetworkResponse {
        if (dto is TrackSearchRequest) {
            val resp = iTuneService.searchTracks(dto.expression).execute()

            val body = resp.body()

            return NetworkResponse(
                body = body,
                resultCode = resp.code()
            )

        } else {
            return NetworkResponse().apply { resultCode = 400 }
        }
    }

    fun getTrackById(id: Long): NetworkResponse {
        try {
            val resp = iTuneService.searchTracksID(id).execute()
            val body = resp.body()
            return  NetworkResponse(
                body = body,
                resultCode = resp.code()
            )
        } catch (e: Exception) {
            return NetworkResponse().apply { resultCode = 500 }
        }
    }

}