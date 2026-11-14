package com.practicum.playlistmaker.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.presentation.app.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.api.InterfacePlayTrackInteractor
import com.practicum.playlistmaker.domain.api.InterfaceSearchTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui_const.AppConst
import com.practicum.playlistmaker.presentation.adapter_veiwholder.TrackAdapter
import com.practicum.playlistmaker.ui.ViewModel.SearchViewModel
import com.practicum.playlistmaker.ui.ViewModel.SearchMode

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    var processingClick = false
    var saveText : String = ""
    private val trackadapter = TrackAdapter { clickedTrack -> handleItemClick(clickedTrack) }

    private lateinit var searchDebouncer: SearchDebouncer

    private val searchInteractor: InterfaceSearchTracksInteractor by lazy {
        Creator.createSearchTracksInteractor(applicationContext)
    }
     private val playTrackInteractor: InterfacePlayTrackInteractor by lazy {
        Creator.createPlayTrackInteractor(applicationContext)
    }

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModel.getFactory(searchInteractor,playTrackInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        searchDebouncer=SearchDebouncer()

        // Настройка recyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackadapter

            // Настройка наполнения UI
        binding.progressBar.visibility = View.GONE
        binding.historyBox.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
        binding.noInternet.visibility = View.GONE
        binding.resetInternet.visibility = View.GONE

            //Настройка кнопки очистки Истории
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

// Подписка на изменения состояния
        viewModel.uiState.observe(this) { state ->
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            when (state.mode) {
                SearchMode.SEARCH -> {
                    // Режим поиска: показываем результаты поиска
                    trackadapter.submitList(state.tracks)

                    // Скрываем элементы истории
                    binding.historyBox.visibility = View.GONE
                    binding.clearHistoryButton.visibility = View.GONE

                    if (state.tracks.isEmpty()) {
                        // Пустые результаты поиска
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyStateView.visibility = View.VISIBLE
                        binding.noInternet.visibility = View.GONE
                    } else {
                        // Есть результаты
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyStateView.visibility = View.GONE
                        binding.noInternet.visibility = View.GONE
                    }

                    // Обработка ошибок поиска
                    if (!state.error.isNullOrBlank()) {
                        binding.noInternet.visibility = View.VISIBLE
                        binding.resetInternet.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyStateView.visibility = View.GONE
                    } else {
                        binding.noInternet.visibility = View.GONE
                    }
                }

                SearchMode.HISTORY -> {
                    // Режим истории: показываем историю
                    trackadapter.submitList(state.history)

                    if (state.history.isEmpty()) {
                        // Пустая история
                        binding.historyBox.visibility = View.GONE
                        binding.clearHistoryButton.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyStateView.visibility = View.VISIBLE
                        binding.noInternet.visibility = View.GONE
                    } else {
                        // Есть история
                        binding.historyBox.visibility = View.VISIBLE
                        binding.clearHistoryButton.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyStateView.visibility = View.GONE
                        binding.noInternet.visibility = View.GONE
                    }
                }
                else -> {
                    Log.w("SearchActivity", "Unknown search mode: ${state.mode}")
                }
            }
        }

       viewModel.loadHistory()
        // устанавливаем слушателя нажатия на Трек
        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")

            // Скрываем клавиатуру
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            // обработка коллизии
            viewModel.clearSearchAndLoadHistory()
        }

        binding.resetInternet.visibility = View.GONE
        retryButton()

        // Обработка кнопки «Назад»
        binding.containerSearchBack.setOnClickListener {
              finish()
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                saveText = s.toString()
                binding.clearIcon.visibility = clearButtonVisibility(s)

                if (saveText.isEmpty()) {
                    searchDebouncer.cancel() // Отменяем отложенный поиск
                    viewModel.loadHistory()  // ViewModel показывает историю
                } else {
                    //задержка поиска пока пользователь печатает (чтобы не был поиск посимвольно)
                    searchDebouncer.triggerSearchWithDelay(AppConst.SEARCH_DEBOUNCE_DELAY)
                }
            }
        }
        // ввод пользователя посимвольно
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        // вводит и подтверждает ввод кнопкой
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.inputEditText.text.toString()
                viewModel.search(query)

          // скрываем клавиатуру, т.к польз сказал что готово
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)

                true // сообщение системе
            }else {
                false  // Если это была не кнопка пуск, система разбирается
            }
        }
    }

    override fun onResume() {
        super.onResume()

         if (saveText.isNotEmpty()) {
             viewModel.search(saveText)
        } else {
             viewModel.refreshHistory()
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun retryButton() {
        binding.resetInternet.setOnClickListener {
            if (!viewModel.lastFailedQuery.isNullOrBlank()) {
                binding.resetInternet.visibility = View.GONE
                binding.noInternet.visibility = View.GONE
                binding.inputEditText.setText(viewModel.lastFailedQuery!!)
                viewModel.search(binding.inputEditText.text.toString())
                viewModel.lastFailedQuery = null
            }
        }
    }
     override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putString("EDIT_TEXT_KEY", saveText)
     }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString("EDIT_TEXT_KEY")
        if (restoredText != null) {
            binding.inputEditText.setText(restoredText)
        }
    }

    private fun clearHistory() {
        viewModel.clearHistory()
    }

    private fun handleItemClick(track: Track) {
        if (processingClick) {
            // Клик проигнорирован: предыдущий еще обрабатывается
            return
        }
        processingClick = true

        viewModel.onTrackClicked(track)

        // Отправляем поля трека в AudioPlayer
        val intent = Intent(this@SearchActivity, AudioPlayer::class.java).apply {
           flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        intent.putExtra("track", track)

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("SearchActivity", "Error starting AudioPlayer", e)
            Toast.makeText(this, "Не удалось открыть плеер", Toast.LENGTH_SHORT).show()
        }

        // Обработка задержки клика по треку
        Handler(Looper.getMainLooper()).postDelayed({
            processingClick = false}, AppConst.TRACK_CLICK_DEBOUNCE_DELAY)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchDebouncer.cancel()
    }

   inner class SearchDebouncer() {
        private val handler = Handler(Looper.getMainLooper())
        private val findRunnable = Runnable {
            if (this@SearchActivity.isFinishing || this@SearchActivity.isDestroyed) {
                return@Runnable
            }
            viewModel.search(binding.inputEditText.text.toString())
        }

        fun triggerSearchWithDelay(delayMs: Long) {
            handler.removeCallbacks(findRunnable)
            handler.postDelayed(findRunnable, delayMs)
        }

        fun cancel() {
            handler.removeCallbacks(findRunnable)
        }
    }
}