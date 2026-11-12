package com.practicum.playlistmaker.presentation.activity

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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.presentation.app.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.api.InterfacePlayTrackInteractor
import com.practicum.playlistmaker.domain.api.InterfaceSearchTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui_const.AppConst
import com.practicum.playlistmaker.presentation.adapter_veiwholder.TrackAdapter
import com.practicum.playlistmaker.ui.ViewModel.SearchViewModel
import kotlin.concurrent.thread

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

        // делаем наполнение блоков экрана

        binding.progressBar.visibility = View.GONE
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackadapter

        binding.historyBox.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
        binding.clearHistoryButton.setOnClickListener {
            clearHistory()
        }

// Подписка на ошибки
        viewModel.errorState.observe(this) { error ->
            if (error != null) {
                binding.noInternet.visibility = View.VISIBLE
                binding.resetInternet.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.emptyStateView.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            } else {
                binding.noInternet.visibility = View.GONE
            }
        }

        // Подписка на загрузку прогресс бар (isLoading)
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        //если активен поиск - подписка на поиск
        viewModel.observeTracks.observe(this) { list ->
            if (viewModel.currentQuery.value?.isNotEmpty() == true) {
                trackadapter.submitList(list)
                if (viewModel.errorState.value == null) {
                if (list.isEmpty()) {
                    binding.historyBox.visibility = View.GONE
                    binding.clearHistoryButton.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyStateView.visibility = View.VISIBLE
                } else {
                    binding.historyBox.visibility = View.GONE
                    binding.clearHistoryButton.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyStateView.visibility = View.GONE
                    binding.noInternet.visibility = View.GONE
                }
                } else {
                    // При ошибке скрываем заглушку пустого поиска
                    binding.emptyStateView.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
            }
        }
        // если поиска НЕТ, обрабатываем только историю - подписка на историю
        viewModel.observeHistory.observe(this) { list ->

                    trackadapter.submitList(list)
                    if (list.isEmpty()) {
                        binding.historyBox.visibility = View.GONE
                        binding.clearHistoryButton.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.historyBox.visibility = View.VISIBLE
                        binding.clearHistoryButton.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyStateView.visibility = View.GONE
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
        viewModel.refreshHistory()
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