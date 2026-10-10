package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
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
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import java.util.Locale

class AudioPlayer  : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val THREAD_DELAY = 300L // задержка обновления таймера
        private const val THREAD_DELAY_PLAY_BUTTON = 300L // задержка клика по кнопке PLAY
      }
    private var playerState = STATE_DEFAULT
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

    private lateinit var secondsTextView: TextView

    private var mediaPlayer = MediaPlayer()
    private var uri: Uri? = null
    private var timeText: String? = "00:00"
    private var mainThreadHandler: Handler? = null
    private val updateTimerRunnable = object :  Runnable {
        override fun run() {
            val currentPosition = mediaPlayer.currentPosition

                timeText =SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                secondsTextView.text = timeText

            if (mediaPlayer.isPlaying) {
                mainThreadHandler?.postDelayed(this, THREAD_DELAY)
            }
        }
    }

    private var isProcessingPlayback = false


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
        // Объявляем поток - для secondsTextView
        mainThreadHandler = Handler(Looper.getMainLooper())

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

        secondsTextView= findViewById(R.id.timeAP)



        makeUI(track)
        preparePlayer()
        setupClickListeners()


    }


    private fun setupClickListeners() {
        backButton2.setOnClickListener {
            finish() // Закрыть этот экран
        }

        buttonAddAPView.setOnClickListener {
            Log.d("Player", "Нажата кнопка  - Добавить в плейлист!")
        }

        buttonPlayAPView.setOnClickListener {
            playbackControl()
            Log.d("Player", "Нажата кнопка Play!")
        }

        buttonFavouritesAPView.setOnClickListener {
            Log.d("Player", "Нажата кнопка Добавить в избранное!")
        }
    }

    private fun preparePlayer() {

        uri?.let { validUri ->
            try {
                // validUri теперь гарантированно не null
                mediaPlayer.setDataSource(this, validUri)

                mediaPlayer.setOnPreparedListener {
                    buttonPlayAPView.isEnabled = true
                    playerState = STATE_PREPARED
                }
                mediaPlayer.setOnCompletionListener {
                    playerState = STATE_PREPARED
                    mainThreadHandler?.removeCallbacks(updateTimerRunnable)
                    secondsTextView.text = getString(R.string.timerZeroAP)

                    buttonPlayAPView.setImageResource(R.drawable.button_play_song)
                    buttonPlayAPView.isEnabled = true
                }
                mediaPlayer.prepareAsync()

            } catch (e: Exception) {
                //  формат неверный
                e.printStackTrace()
                Toast.makeText(this, "Не удалось загрузить аудиофайл", Toast.LENGTH_SHORT).show()
                buttonPlayAPView.isEnabled = false
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
           playerState = STATE_PLAYING

           mainThreadHandler?.post(updateTimerRunnable)
        buttonPlayAPView.setImageResource(R.drawable.button_pause)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        mainThreadHandler?.removeCallbacks(updateTimerRunnable)
        buttonPlayAPView.setImageResource(R.drawable.button_play_song)
    }

    private fun playbackControl() {
        if (isProcessingPlayback) {
            Log.d("Player", "Игнорируем клик: обработка уже идет")
            return
        }
        isProcessingPlayback = true

        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
// задержка нажатия на кнопку PLAY
        mainThreadHandler?.postDelayed(Runnable {isProcessingPlayback = false}, THREAD_DELAY_PLAY_BUTTON)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacks(updateTimerRunnable)
        mediaPlayer.release()
        mainThreadHandler = null
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
        val url : String? = track.previewUrl
        if (!url.isNullOrBlank()) {
            try {
                uri = Uri.parse(url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Ссылки нет (null или пустая). Ничего не делаем
          }

    }

}