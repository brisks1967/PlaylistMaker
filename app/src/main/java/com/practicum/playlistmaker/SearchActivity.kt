package com.practicum.playlistmaker

import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    var saveText : String = ""
    private var lastFailedQuery: String? = null // здесь запоминаем последний запрос для кнопки Обновить
    private lateinit var emptyStateView: LinearLayout
    private lateinit var noInternetView: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var resetInternet: ImageView
    private lateinit var historyContainer: LinearLayout
    private lateinit var clearHistoryButton: FrameLayout
    private lateinit var searchHistory: SearchHistory


    // Блок авторизации и подключения API начало
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTuneService = retrofit.create(iTunesSearchAPI::class.java)

// Блок авторизации и подключения API - конец

    private val trackadapter = TrackAdapter { clickedTrack -> handleItemClick(clickedTrack) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        //Карусель записей - начало

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


        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)


        searchHistory = SearchHistory(this)

        if (inputEditText.text.toString().isEmpty()) {
            val historyIds = searchHistory.getTrackIDList()
            loadHistoryTracksFromApi(historyIds)
        }

        resetInternet = findViewById<ImageView>(R.id.resetInternet)
        resetInternet.visibility = View.GONE
        retryButton()


        // переменная linearLayout нужна была для изменения цвета фона поисковой строки - см ниже
        //   val linearLayout = findViewById<LinearLayout>(R.id.searchField2)

        // Обработка кнопки «Назад»
        val backButton = findViewById<LinearLayout>(R.id.containerSearchBack)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")

            // Скрываем клавиатуру
           val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

          // Запускаем обновление через TextWatcher
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
          //      // Изменение фона поисковой строки - начало блока
          //      if (s.isNullOrEmpty()) {
          //          linearLayout.setBackgroundColor(getColor(R.color.neutral))
          //      } else {
          //          linearLayout.setBackgroundColor(getColor(R.color.neutral))
          //      }
          //     // Изменение фона поисковой строки - конец блока

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

                    performSearch(s.toString())
                }
            }
        }

        // ввод пользователя посимвольно
        inputEditText.addTextChangedListener(simpleTextWatcher)

        // вводит и подтверждает ввод кнопкой
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                performSearch(inputEditText.text.toString())

                                // скрываем клавиатуру, т.к польз сказал что готово
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

                true // сообщение системе
            }else {
                false  // Если это была не кнопка пуск, система разбирается
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            val call = iTuneService.searchTracks(query)
            call.enqueue(object : Callback<iTunesResponse> {

                 override fun onResponse(call: Call<iTunesResponse>, response: Response<iTunesResponse>) {

                    // Логика успешного ответа
                    if (response.isSuccessful && response.body() != null) {
                        val tracksList = response.body()!!.results
                        if (tracksList.isNotEmpty()) {
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

                // Нет связи
                override fun onFailure(call: Call<iTunesResponse>, t: Throwable) {
                    t.printStackTrace()
                    lastFailedQuery = query // запомнили последний запрос для кнопки Обновить
                    noInternetView.visibility = View.VISIBLE
                    resetInternet.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    trackadapter.submitList(emptyList())
                }
            })
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
                performSearch(lastFailedQuery!!)

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
        val myEditText: EditText = findViewById(R.id.inputEditText)

        if (restoredText != null) {
            myEditText.setText(restoredText)
        }
    }
// Обработка блока истории
    private fun loadHistoryTracksFromApi(historyIds: List<String>) {

        val loadedTracks = mutableListOf<Track>()

        if (historyIds.isEmpty()) {
            trackadapter.submitList(emptyList())
            historyContainer.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE

            return
        }

       historyContainer.visibility = View.VISIBLE
        clearHistoryButton.visibility = View.VISIBLE

       historyIds.forEach { idStr ->
            val id = idStr.toLongOrNull()
            if (id == null) return@forEach

            val call = iTuneService.searchTracksID(id)

            call.enqueue(object : Callback<iTunesResponse> {
                override fun onResponse(call: Call<iTunesResponse>, response: Response<iTunesResponse>) {

                    if (response.isSuccessful && response.body() != null) {
                        val results = response.body()!!.results

                        if (results.isNotEmpty()) {
                            val track = results[0]

                            val trackForHistory = Track(
                                trackName = track.trackName,
                                artistName = track.artistName,
                                trackTimeMillis = track.trackTimeMillis,
                                artworkUrl100 = track.artworkUrl100,
                                trackId = track.trackId,
                                mark = 1  //  ЭТО МЕТКА
                            )

                            loadedTracks.add(trackForHistory)
                            trackadapter.submitList(loadedTracks.toList())
                        }
                    }
                }

                override fun onFailure(call: Call<iTunesResponse>, t: Throwable) {
                    // Если трек не загрузился .....
                }
            })
        }
    }

    private fun handleItemClick(track: Track) {

        searchHistory.addTrack(track.trackId.toString())

        if (track.mark == 1) {

            performSearch(track.trackName)
            historyContainer.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        } else {
        }
    }

    private fun clearHistory() {
        searchHistory.clearTrack()
        trackadapter.submitList(emptyList())
        historyContainer.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE

    }
}
