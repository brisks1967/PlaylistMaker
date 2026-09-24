package com.practicum.playlistmaker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize



@Parcelize
data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека в милисекундах
    val artworkUrl100: String?, // Ссылка на изображение обложки
    val trackId: Long,          // ID трека
    val mark: Int  = 0,  // метка 1 - из истории, 0 - не из истории

    val collectionName: String?, // Название Альбома
    val country: String?, // Название Страны
    val primaryGenreName: String?, // Название Жанра
    val releaseDate: String? // Год релиза
): Parcelable
{
    // замена URL - картинки 100х100 на 512х512
    fun getCoverArtwork(): String? {
        return artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
    }
}

