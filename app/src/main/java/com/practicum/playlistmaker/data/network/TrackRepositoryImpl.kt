package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.data.dto.iTunesResponse
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.data.dto.TrackConverter
import com.practicum.playlistmaker.domain.api.TrackRepository

class TrackRepositoryImpl (
    private val client: NetworkClient,
    private val converter: TrackConverter,
    private val history: SearchHistoryRepository
) : TrackRepository
{
    override fun getTrackById(id: Long): Track? {

        val response = client.getTrackById(id)

        // Проверяем успешность ответа
        if (response.resultCode == 200 && response.body != null) {
            val rawData = response.body as? iTunesResponse
            val dtoList = rawData?.results ?: emptyList()

            // Если трек найден, конвертируем и возвращаем
            if (dtoList.isNotEmpty()) {
                return converter.convertSingle(dtoList[0])
            }
        }

        return null // Если ошибка или трек не найден — возвращаем null
    }

    override fun searchTracks(query: String): List<Track> {
        val request = TrackSearchRequest(expression = query)
        val response = client.doRequest(request)

        if (response.resultCode != 200 || response.body == null) {
            return emptyList()
        }

        val rawData = response.body as? iTunesResponse
        val dtoList = rawData?.results ?: emptyList()

        return dtoList.map { dto ->
            converter.convertSingle(dto)
        }
    }

    override fun saveHistory(id: String) {
        history.addTrack(id)
    }

    override fun getHistory(): List<String> {
        return history.getTrackIDList()
    }

    override fun clearHistory() {
        history.clearTrack()
    }

    override fun getTrackListByIds(ids: List<Long>): List<Track> {
        // Берем каждый ID, пытаемся получить трек и оставляем только те, что нашлись
        return ids.mapNotNull { id -> getTrackById(id)}
    }
}