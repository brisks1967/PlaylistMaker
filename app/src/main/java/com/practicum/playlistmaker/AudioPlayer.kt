package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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


    // Блок авторизации и подключения API начало
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTuneService = retrofit.create(iTunesSearchAPI::class.java)

// Блок авторизации и подключения API - конец


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        val trackId = intent.getLongExtra("track_id", -1L)


        if (trackId == -1L) {
            Toast.makeText(this, "Не удалось загрузить трек", Toast.LENGTH_SHORT).show()
            finish()
        }


        backButton2 = findViewById<ImageView>(R.id.buttonBackAP)
        imageTrackAPView = findViewById<ImageView>(R.id.imageTrackAP)
        buttonAddAPView = findViewById<ImageButton>(R.id.buttonAddAP)
        buttonPlayAPView = findViewById<ImageButton>(R.id.buttonPlayAP)
        buttonFavouritesAPView = findViewById<ImageButton>(R.id.buttonFavouritesAP)

        titleSongView = findViewById(R.id.nameSongAP)
        titleSongView.text = getString(R.string.textNameSongHintAP)
        nameArtistView = findViewById(R.id.nameGroupAP)
        nameArtistView.text = getString(R.string.textNameArtistHintAP)
        durationTrackView = findViewById(R.id.durationTextAP)
        durationTrackView.text = getString(R.string.textDurationHintAP)
        nameAlbumView = findViewById(R.id.albumTextAP)
        nameAlbumView.text = getString(R.string.textAlbumHintAP)
        yearTrackView = findViewById(R.id.yearTextAP)
        yearTrackView.text = getString(R.string.textYearHintAP)
        genreTrackView = findViewById(R.id.genreTextAP)
        genreTrackView.text = getString(R.string.textGenreHintAP)
        countryTrackView = findViewById(R.id.countryTextAP)
        countryTrackView.text = getString(R.string.textCountryHintAP)

        setupClickListeners()

        loadUserTrack(trackId)

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

    private fun loadUserTrack(id: Long) {

        val call = iTuneService.searchTracksID(id)
        call.enqueue(object : Callback<iTunesResponse> {

            override fun onResponse(
                call: Call<iTunesResponse>,
                response: Response<iTunesResponse>
            ) {
                // Логика успешного ответа
                if (response.isSuccessful && response.body() != null) {
                    val resultsList= response.body()!!.results //resultsList - массив из 1 трека, который имеет много полей
                    if (resultsList.isNotEmpty()) {
                        val track = resultsList[0]
                        makeUI(track)

                    } else {
                        // показываем заглушку «ничего не найдено»
                    }
                } else {
                    // поиск завершен ничем

                }
            }
            // Нет связи
            override fun onFailure(call: Call<iTunesResponse>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@AudioPlayer, "Не удалось загрузить трек (нет сети или ошибка сервера)", Toast.LENGTH_LONG).show()

            }
        }
        )
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