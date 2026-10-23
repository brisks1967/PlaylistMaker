package com.practicum.playlistmaker


data class TrackDTO(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека в милисекундах
    val artworkUrl100: String?, // Ссылка на изображение обложки
    val trackId: Long,          // ID трека

    val collectionName: String?, // Название Альбома
    val country: String?, // Название Страны
    val primaryGenreName: String?, // Название Жанра
    val releaseDate: String?, //год релиза
    val previewUrl: String // Часть трека для ознакомления


)



