package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.NetworkResponse

interface NetworkClient {
    fun doRequest(dto: Any): NetworkResponse

}