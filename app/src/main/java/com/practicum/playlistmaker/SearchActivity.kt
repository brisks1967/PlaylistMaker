package com.practicum.playlistmaker

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
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.domain.models.TrackRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.AppConst
import com.practicum.playlistmaker.domain.models.PlayTrackInteractor
import com.practicum.playlistmaker.domain.models.SearchTracksInteractor
import com.practicum.playlistmaker.presentation.TrackAdapter
import kotlin.concurrent.thread


class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    var processingClick = false

    var saveText : String = ""
    private var lastFailedQuery: String? = null // здесь запоминаем последний запрос для кнопки Обновить
    private lateinit var emptyStateView: LinearLayout
    private lateinit var noInternetView: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var resetInternet: ImageView
    private lateinit var historyContainer: LinearLayout
    private lateinit var clearHistoryButton: FrameLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var clearButton: ImageView
    private lateinit var backButton: LinearLayout
    private val trackadapter = TrackAdapter { clickedTrack -> handleItemClick(clickedTrack) }

    private lateinit var repository: TrackRepository
    private lateinit var searchInteractor: SearchTracksInteractor
    private lateinit var searchDebouncer: SearchDebouncer
    private lateinit var playTrackInteractor: PlayTrackInteractor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = Creator.createTrackRepository(this)
        searchInteractor = Creator.createSearchTracksInteractor(this)
        searchDebouncer = SearchDebouncer(this)
        playTrackInteractor = Creator.createPlayTrackInteractor(this)



        setContentView(R.layout.activity_search)

        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = trackadapter

        emptyStateView = findViewById<LinearLayout>(R.id.emptyStateView)
        noInternetView = findViewById<LinearLayout>(R.id.noInternet)
        historyContainer = findViewById<LinearLayout>(R.id.historyBox)
        historyContainer.visibility = View.GONE

        clearHistoryButton = findViewById<FrameLayout>(R.id.clearHistoryButton)
        clearHistoryButton.visibility = View.GONE
        clearHistoryButton.setOnClickListener {
            clearHistory()
        }

        inputEditText = findViewById<EditText>(R.id.inputEditText)

        clearButton = findViewById<ImageView>(R.id.clearIcon)

        if (inputEditText.text.toString().isEmpty()) {
            val historyIds = repository.getHistory()
            loadHistoryTracksFromApi(historyIds)
        }

        resetInternet = findViewById<ImageView>(R.id.resetInternet)
        resetInternet.visibility = View.GONE
        retryButton()

        // Обработка кнопки «Назад»
        backButton = findViewById<LinearLayout>(R.id.containerSearchBack)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finish()
        }

        // устанавливаем слушателя нажатия на Трек
        clearButton.setOnClickListener {
            inputEditText.setText("")

            // Скрываем клавиатуру
           val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

          // Запускаем обновление через TextWatcher
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                saveText = s.toString()
                clearButton.visibility = clearButtonVisibility(s)

                if (saveText.isEmpty()) {
// если строка поиска пуста, то показываем историю
                    historyContainer.visibility = View.VISIBLE
                    clearHistoryButton.visibility = View.VISIBLE
                     val historyIds = repository.getHistory()
                      loadHistoryTracksFromApi(historyIds)
                }else {
   // если строка поиска заполняется пользователем, то историю убираем
                    historyContainer.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
   // Делаем задержку в поле ввода
                     searchDebouncer.triggerSearchWithDelay(AppConst.SEARCH_DEBOUNCE_DELAY)
                }
            }
        }
        // ввод пользователя посимвольно
        inputEditText.addTextChangedListener(simpleTextWatcher)

        // вводит и подтверждает ввод кнопкой
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                performSearch()
          // скрываем клавиатуру, т.к польз сказал что готово
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

                true // сообщение системе
            }else {
                false  // Если это была не кнопка пуск, система разбирается
            }
        }
    }

    private fun performSearch() {
        val query = inputEditText.text.toString()

        if (query.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE

            thread {
                try {

                    val tracksList = searchInteractor.makeQuery(query)
                    runOnUiThread {
                        progressBar.visibility = View.GONE

                        if (tracksList.isNotEmpty()) {
                            trackadapter.submitList(tracksList)
                            emptyStateView.visibility = View.GONE
                            noInternetView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            lastFailedQuery = null
                        } else {
                            // показываем заглушку «ничего не найдено»
                            emptyStateView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                            noInternetView.visibility = View.GONE
                            trackadapter.submitList(emptyList())
                        }
                    }

                } catch (e: Exception) {
                    lastFailedQuery = query
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        noInternetView.visibility = View.VISIBLE
                        resetInternet.visibility = View.VISIBLE
                        trackadapter.submitList(emptyList())
                    }
                }
            }
        } else {
            trackadapter.submitList(emptyList())
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
        resetInternet.setOnClickListener {
            if (!lastFailedQuery.isNullOrBlank()) {
                resetInternet.visibility = View.GONE
                noInternetView.visibility = View.GONE
                inputEditText.setText(lastFailedQuery!!)
                performSearch()
                lastFailedQuery = null
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
            inputEditText.setText(restoredText)
        }
    }

// Обработка блока истории
    private fun loadHistoryTracksFromApi(historyIds: List<String>) {
        val loadedTracks = mutableListOf<Track>()

        if (historyIds.isEmpty()) {
            runOnUiThread {
                trackadapter.submitList(emptyList())
                historyContainer.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
            }
            return
        }

       historyContainer.visibility = View.VISIBLE
       clearHistoryButton.visibility = View.VISIBLE

      thread {
          for (idStr in historyIds) {
              val id = idStr.toLongOrNull() ?: continue

              val track = repository.getTrackById(id)
              if (track != null) {
                  loadedTracks.add(track.copy(mark = 1)) //трек существует в истории, mark = 1
              }
          }

          runOnUiThread {
              if (loadedTracks.isNotEmpty()) {
                  trackadapter.submitList(loadedTracks.toList())
                  historyContainer.visibility = View.VISIBLE
                  clearHistoryButton.visibility = View.VISIBLE
              } else {
                  trackadapter.submitList(emptyList())
                  historyContainer.visibility = View.GONE
                  clearHistoryButton.visibility = View.GONE
              }
          }
      }
    }

    private fun clearHistory() {
        repository.clearHistory()
        trackadapter.submitList(emptyList())
        historyContainer.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun handleItemClick(track: Track) {
        if (processingClick) {
            // Клик проигнорирован: предыдущий еще обрабатывается
            return
        }
        processingClick = true

        val result = playTrackInteractor.workTrack(track)

        if (result.shouldSearch) {
            inputEditText.setText(result.searchQuery)
            performSearch()

            if (result.shouldHideHistory) {
                historyContainer.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
            }
        }


        // Отправляем поля трека в AudioPlayer
        val intent = Intent(this@SearchActivity, AudioPlayer::class.java)
        intent.putExtra("track", track)
        startActivity(intent)

        // Обработка задержки клика по треку
        Handler(Looper.getMainLooper()).postDelayed({
            processingClick = false}, AppConst.TRACK_CLICK_DEBOUNCE_DELAY)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchDebouncer.cancel()
    }

    private class SearchDebouncer(private val activity: SearchActivity) {

        private val handler = Handler(Looper.getMainLooper())
        private val findRunnable = Runnable {
            if (activity.isFinishing || activity.isDestroyed) {
                return@Runnable
            }
            activity.performSearch()
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
