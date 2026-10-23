package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
import com.practicum.playlistmaker.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.data.dto.iTunesResponse
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.network.SearchHistory
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TrackConverter
import com.practicum.playlistmaker.domain.utils.AppConst
import com.practicum.playlistmaker.domain.utils.searchDebounce
import com.practicum.playlistmaker.presentation.TrackAdapter
import kotlin.concurrent.thread


class SearchActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var inputEditText: EditText
    private val searchRunnable = Runnable {
        val currentText = inputEditText.text.toString()
        if (currentText.isNotEmpty()) {
            performSearch()
        }
    }

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
    private lateinit var searchHistory: SearchHistory
    private lateinit var clearButton: ImageView
    private lateinit var backButton: LinearLayout
            private val trackadapter = TrackAdapter { clickedTrack -> handleItemClick(clickedTrack) }

    private val client = Creator.createClient()
    private val converter = Creator.createTrackConverter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        searchHistory = Creator.createSearchHistory(this)

        if (inputEditText.text.toString().isEmpty()) {
            val historyIds = searchHistory.getTrackIDList()
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
                     val historyIds = searchHistory.getTrackIDList()
                      loadHistoryTracksFromApi(historyIds)
                }else {
   // если строка поиска заполняется пользователем, то историю убираем
                    historyContainer.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
   // Делаем задержку в поле ввода
                    searchRunnable.searchDebounce(AppConst.SEARCH_DEBOUNCE_DELAY)
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
                    val request = TrackSearchRequest(expression = query)
                    val response = client.doRequest(request)

                    runOnUiThread {
                    progressBar.visibility = View.GONE

                    if (response.resultCode == 200 && response.body != null) {
                        val rawData = response.body as? iTunesResponse
                        val dtoList = rawData?.results ?: emptyList()
                        val tracksList = dtoList.map { dto -> converter.convertSingle(dto) }

                        if (dtoList.isNotEmpty()) {
                            trackadapter.submitList(tracksList)
                            emptyStateView.visibility = View.GONE
                            noInternetView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        } else {
                            // показываем заглушку «ничего не найдено»
                            emptyStateView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                            noInternetView.visibility = View.GONE
                            trackadapter.submitList(emptyList())
                        }
                    } else {
                        // поиск завершен ничем
                        emptyStateView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        noInternetView.visibility = View.GONE
                        trackadapter.submitList(emptyList())
                    }
                    }
                } catch (e: Exception) {

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

              val response = client.getTrackById(id)

              if (response.resultCode == 200 && response.body != null) {

                  val rawData = response.body as? iTunesResponse
                  val dtoList = rawData?.results ?: emptyList()

                  if (dtoList.isNotEmpty()) {
                      val dto = dtoList[0] // Берем первый (и единственный) трек
                      val trackForHistory = converter.convertSingle(dto).copy(mark = 1)
                      loadedTracks.add(trackForHistory)
                  }
              } else {
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
        searchHistory.clearTrack()
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
        searchHistory.addTrack(track.trackId.toString())

        if (track.mark == 1) {
            inputEditText.setText(track.trackName)
            performSearch()
            historyContainer.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        } else {
        }
        // Отправляем поля трека в AudioPlayer
        val intent = Intent(this@SearchActivity, AudioPlayer::class.java)
        intent.putExtra("track", track)
        startActivity(intent)

        // Обработка задержки клика по треку
        Handler(Looper.getMainLooper()).postDelayed({
            processingClick = false}, AppConst.TRACK_CLICK_DEBOUNCE_DELAY)
    }
}
