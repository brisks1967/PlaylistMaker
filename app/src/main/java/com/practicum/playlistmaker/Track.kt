package com.practicum.playlistmaker

data class Track(
    var trackName: String, // Название композиции
    var artistName: String, // Имя исполнителя
    var trackTime: String, // Продолжительность трека
    var artworkUrl100: String  // Ссылка на изображение обложки
)
