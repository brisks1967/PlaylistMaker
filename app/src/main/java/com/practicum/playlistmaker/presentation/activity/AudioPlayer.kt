package com.practicum.playlistmaker.presentation.activity

import android.content.Context
import android.icu.text.SimpleDateFormat

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui_const.AppConst
import com.practicum.playlistmaker.ui.ViewModel.AudioPlayerViewModel
import com.practicum.playlistmaker.ui.ViewModel.AudioPlayerViewModelFactory
import com.practicum.playlistmaker.ui.ViewModel.PlayerState
import com.practicum.playlistmaker.ui.ViewModel.PlayerUiState
import com.practicum.playlistmaker.ui.ViewModel.SearchViewModel
import java.util.Locale
import kotlin.getValue

class AudioPlayer  : AppCompatActivity() {

    private lateinit var binding: ActivityAudioplayerBinding
    private val viewModel: AudioPlayerViewModel by viewModels {
        AudioPlayerViewModelFactory()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPlayAP.setOnClickListener {
            Log.d("AudioPlayer", "Клик по Play! Текущее состояние: ${viewModel.statePlayer.value}")
            viewModel.playbackControl()
        }

            // получаем поля трека из SearchActivity
        val track = intent.getParcelableExtra<Track>("track")
        if (track == null) {
            Toast.makeText(this, "Не удалось загрузить трек", Toast.LENGTH_SHORT).show()
         finish()
            return
        }

        setupClickListeners()
        viewModel.setTrack(track)

        viewModel.trackData.observe(this) { trackModel ->
            if (trackModel != null) {
                makeUI(trackModel)
            }
        }

        viewModel.statePlayer.observe(this) { state ->
            //Обработка ОШИБКИ
            if (state.playerState == PlayerState.STATE_ERROR) {
                Toast.makeText(this, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show()
                binding.buttonPlayAP.isEnabled = false // Блокируем кнопку
                binding.buttonPlayAP.setImageResource(R.drawable.button_play_song)
                return@observe // Прерываем
            }

            //Обновляем кнопку Play/Pause
            val isPlaying = state.playerState == PlayerState.STATE_PLAYING
            binding.buttonPlayAP.setImageResource(
                if (isPlaying) R.drawable.button_pause else R.drawable.button_play_song
            )

            // Блокируем кнопку, если плеер не готов (DEFAULT)
            binding.buttonPlayAP.isEnabled = state.playerState != PlayerState.STATE_DEFAULT

            //форматирование времени
            val safePosition = if (state.currentPositionMs < 0) 0 else state.currentPositionMs
            try {
                val timeString = SimpleDateFormat("mm:ss", Locale.getDefault()).format(safePosition)
                binding.timeAP.text = timeString
            } catch (e: Exception) {
                Log.e("AudioPlayer", "Ошибка форматирования времени", e)
                binding.timeAP.text = "00:00"
            }
        }

    }


    private fun setupClickListeners() {
        binding.buttonBackAP.setOnClickListener {
            finish() // Закрыть этот экран
        }
        binding.buttonAddAP.setOnClickListener {
            Log.d("Player", "Нажата кнопка  - Добавить в плейлист!")
        }
        binding.buttonPlayAP.setOnClickListener {
            viewModel.playbackControl()
            Log.d("Player", "Нажата кнопка Play!")
        }
        binding.buttonFavouritesAP.setOnClickListener {
            Log.d("Player", "Нажата кнопка Добавить в избранное!")
        }
    }

    private fun makeUI(track: Track) {

        binding.nameSongAP.text = track.trackName
        binding.nameGroupAP.text = track.artistName

        val durationMs = track.trackTimeMillis
        val minutes = durationMs / (1000 * 60)
        val seconds = (durationMs / 1000) % 60
        val formattedDuration = String.format("%02d:%02d", minutes, seconds)
        binding.durationTextAP.text = formattedDuration


        binding.genreTextAP.text = track.primaryGenreName
        binding.countryTextAP.text = track.country

        // обрабатываем Альбом
        binding.albumTextAP.text = ""
        binding.albumTextAP.visibility = View.GONE
        if (!track.collectionName.isNullOrBlank()) {
            binding.albumTextAP.text = track.collectionName
            binding.albumTextAP.visibility = View.VISIBLE
        }

       // обрабатываем Год релиза - нужен год, а не вся дата
        binding.yearTextAP.text = ""
        binding.yearTextAP.visibility = View.GONE
        val year = track.releaseDate?.substring(0, 4)
        if (!year.isNullOrBlank()) {
            binding.yearTextAP.text = year
            binding.yearTextAP.visibility = View.VISIBLE
        } else {
            binding.yearTextAP.text = ""
            binding.yearTextAP.visibility = View.GONE
        }

        //Обложка -   замена URL - картинки 100х100 на 512х512
        val coverUrl = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
        loadCoverImageGlideAP(this@AudioPlayer,binding.imageTrackAP,coverUrl)
    }

    // Логика расчета радиуса для AP вынесена сюда
    fun calculateRadiusPx(context: Context, radiusDp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (radiusDp * density).toInt()
    }

    fun loadCoverImageGlideAP(context: Context, imageView: ImageView, url: String?) {
        val radiusPx = calculateRadiusPx(context, 8)

        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.image_hint_ap) // Что показать, пока грузится
            .transform(RoundedCorners(radiusPx)) // скругления углов
            .into(imageView)    // Куда вставить картинку
    }

    override fun onPause() {
        super.onPause()

        viewModel.pauseOnScreenHide()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.statePlayer.value?.let { state ->
            binding.buttonPlayAP.isEnabled = state.playerState != PlayerState.STATE_DEFAULT
            binding.buttonPlayAP.setImageResource(
                if (state.playerState == PlayerState.STATE_PLAYING)
                    R.drawable.button_pause
                else
                    R.drawable.button_play_song
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val state = viewModel.statePlayer.value ?: return
        binding.buttonPlayAP.isEnabled = state.playerState != PlayerState.STATE_DEFAULT
        binding.buttonPlayAP.setImageResource(
            if (state.playerState == PlayerState.STATE_PLAYING)
                R.drawable.button_pause
            else
                R.drawable.button_play_song
        )
    }

}