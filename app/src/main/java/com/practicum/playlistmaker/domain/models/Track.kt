package com.practicum.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека в милисекундах
    val artworkUrl100: String?, // Ссылка на изображение обложки
    val trackId: Long,          // ID трека

    val collectionName: String?, // Название Альбома
    val country: String?, // Название Страны
    val primaryGenreName: String?, // Название Жанра
    val releaseDate: String?, //год релиза
    val previewUrl: String, // Часть трека для ознакомления

    val mark: Int  = 0  // метка 1 - из истории, 0 - не из истории
): Parcelable
{

}