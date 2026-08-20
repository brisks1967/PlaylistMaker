package com.practicum.playlistmaker

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека в милисекундах
    val artworkUrl100: String  // Ссылка на изображение обложки
)
