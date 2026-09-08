package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesSearchAPI {

    @GET("/search?entity=song")
    fun searchTracks(@Query("term") text: String) : Call<iTunesResponse>

    @GET("lookup")
    fun searchTracksID(@Query("id") id: Long) : Call<iTunesResponse>

}