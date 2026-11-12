package com.practicum.playlistmaker.ui.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.domain.api.InterfacePlayTrackInteractor
import com.practicum.playlistmaker.domain.api.InterfaceSearchTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel (
    private val searchInteractor: InterfaceSearchTracksInteractor,
    private val playTrackInteractor: InterfacePlayTrackInteractor
) : ViewModel(){
    private var historyLoaded = false
    var lastFailedQuery: String? = null // здесь запоминаем последний запрос для кнопки Обновить

    private var cachedHistory: List<Track> = emptyList()

    val currentQuery = MutableLiveData<String>("")

    // данные для поиска
    private val searchLiveData= MutableLiveData<List<Track>>(emptyList())
    val observeTracks: LiveData<List<Track>> get()= searchLiveData
    // данные для истории
    private val historyLiveData = MutableLiveData<List<Track>>(emptyList())
    val observeHistory: LiveData<List<Track>> get() = historyLiveData

    private val errorState1 = MutableLiveData<Exception?>() // это обработка ошибок
    val errorState: LiveData<Exception?> = errorState1

    private val isLoading1 = MutableLiveData<Boolean>()   // это состояние прогресс-бара
    val isLoading: LiveData<Boolean> = isLoading1

    companion object {
        fun getFactory(interactor: InterfaceSearchTracksInteractor,
                       playTrackInteractor: InterfacePlayTrackInteractor): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(interactor,playTrackInteractor)
            }
        }
    }

    fun search(query: String) {
        currentQuery.value = query
        viewModelScope.launch {
            errorState1.value = null // Сбрасываем ошибки
            isLoading1.value = true // Прогресс бар показывается

            try {
                val results = withContext(Dispatchers.IO) {
                    searchInteractor.makeQuery(query)
                }
                searchLiveData.value = results
            } catch (e: Exception) {
                errorState1.value = e // Отправляем ошибку в LiveData
                searchLiveData.value = emptyList()
                lastFailedQuery = query
            } finally {
                isLoading1.value = false // Скрываем прогресс бар
            }
        }
    }

    fun loadHistory (){
        if (historyLoaded) {
            historyLiveData.value = cachedHistory
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val history = searchInteractor.getHistoryTracks()
            cachedHistory = history
            historyLoaded = true
            withContext(Dispatchers.Main) {
                historyLiveData.value = history
            }
        }
    }

    fun clearSearchAndLoadHistory() {
        searchLiveData.value = emptyList() // Очистка текущих результатов
        loadHistory() // Запуск загрузки истории из локального хранилища
    }
    fun clearHistory() {
        searchInteractor.clearHistory()
        historyLoaded = false // Сбрасываем флаг, чтобы загрузить актуальные данные
        loadHistory()         // Отправит пустой список в observeHistory
    }

    fun refreshHistory(){
        historyLoaded = false
        loadHistory()
    }
    fun onTrackClicked(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            playTrackInteractor.workTrack(track)
            withContext(Dispatchers.Main) {
                refreshHistory() // Сброс кэша и загрузка новой истории
            }
        }
    }
}