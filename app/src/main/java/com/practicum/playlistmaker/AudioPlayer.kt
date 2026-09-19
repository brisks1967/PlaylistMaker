package com.practicum.playlistmaker

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AudioPlayer  : AppCompatActivity() {

    private lateinit var backButton2: ImageView
    private lateinit var imageTrackAPView: ImageView
    private lateinit var buttonAddAPView: ImageButton
    private lateinit var buttonPlayAPView: ImageButton
    private lateinit var buttonFavouritesAPView: ImageButton

    private lateinit var titleSongView: TextView
    private lateinit var nameArtistView: TextView
    private lateinit var durationTrackView: TextView
    private lateinit var nameAlbumView: TextView
    private lateinit var yearTrackView: TextView
    private lateinit var genreTrackView: TextView
    private lateinit var countryTrackView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

            // получаем поля трека из SearchActivity
        val track = intent.getParcelableExtra("track") as Track?

        if (track == null) {
            Toast.makeText(this, "Не удалось загрузить трек", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        backButton2 = findViewById<ImageView>(R.id.buttonBackAP)
        imageTrackAPView = findViewById<ImageView>(R.id.imageTrackAP)
        buttonAddAPView = findViewById<ImageButton>(R.id.buttonAddAP)
        buttonPlayAPView = findViewById<ImageButton>(R.id.buttonPlayAP)
        buttonFavouritesAPView = findViewById<ImageButton>(R.id.buttonFavouritesAP)

        titleSongView = findViewById(R.id.nameSongAP)
        nameArtistView = findViewById(R.id.nameGroupAP)
        durationTrackView = findViewById(R.id.durationTextAP)
        nameAlbumView = findViewById(R.id.albumTextAP)
        yearTrackView = findViewById(R.id.yearTextAP)
        genreTrackView = findViewById(R.id.genreTextAP)
        countryTrackView = findViewById(R.id.countryTextAP)


        setupClickListeners()
        makeUI(track)
    }


    private fun setupClickListeners() {
        backButton2.setOnClickListener {
            finish() // Закрыть этот экран
        }

        buttonAddAPView.setOnClickListener {
            Log.d("Player", "Нажата кнопка  - Добавить в плейлист!")
        }

        buttonPlayAPView.setOnClickListener {
            Log.d("Player", "Нажата кнопка Play!")
        }

        buttonFavouritesAPView.setOnClickListener {
            Log.d("Player", "Нажата кнопка Добавить в избранное!")
        }
    }


    private fun makeUI(track: Track) {

        titleSongView.text = track.trackName
        nameArtistView.text = track.artistName

        val durationMs = track.trackTimeMillis
        val minutes = durationMs / (1000 * 60)
        val seconds = (durationMs / 1000) % 60
        val formattedDuration = String.format("%02d:%02d", minutes, seconds)
        durationTrackView.text = formattedDuration


        genreTrackView.text = track.primaryGenreName
        countryTrackView.text = track.country

        // обрабатываем Альбом
        nameAlbumView.text = ""
        nameAlbumView.visibility = View.GONE
        if (!track.collectionName.isNullOrBlank()) {
            nameAlbumView.text = track.collectionName
            nameAlbumView.visibility = View.VISIBLE
        }

       // обрабатываем Год релиза - нужен год, а не вся дата
        yearTrackView.text = ""
        yearTrackView.visibility = View.GONE
        val year = track.releaseDate?.substring(0, 4)
        if (!year.isNullOrBlank()) {
            yearTrackView.text = year
            yearTrackView.visibility = View.VISIBLE
        } else {
            yearTrackView.text = ""
            yearTrackView.visibility = View.GONE
        }

        //Обложка

        val radiusDp = 8
        val density2 = imageTrackAPView.context.resources.displayMetrics.density
        val radiusPx = (radiusDp * density2).toInt()
        val coverUrl = track.getCoverArtwork()

        // Проверяем, есть ли ссылка на картинку

        coverUrl?.let { url ->
                 Glide.with(this@AudioPlayer)
                .load(url)
                .placeholder(R.drawable.image_hint_ap) // Что показать, пока грузится
                .transform(RoundedCorners(radiusPx)) // скругления углов
                .into(imageTrackAPView)        // Куда вставить картинку
        } ?: run {
            // Если ссылки нет вообще, ставим заглушку
            imageTrackAPView.setImageResource(R.drawable.image_hint_ap)
        }

    }

}