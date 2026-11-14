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

    private val uiState1 = MutableLiveData<SearchUiState>(SearchUiState(mode = SearchMode.HISTORY) ) // общее состояние UI Search-активити
    val uiState: LiveData<SearchUiState> = uiState1

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
        uiState1.value = uiState1.value?.copy(
            mode = SearchMode.SEARCH,
            isLoading = true,
            error = null,
            tracks = emptyList(),
            history = emptyList()
        )?: SearchUiState(mode = SearchMode.SEARCH, isLoading = true)
                viewModelScope.launch {
                    try {
                        val results = withContext(Dispatchers.IO) {
                            searchInteractor.makeQuery(query)
                        }
                        uiState1.value = uiState1.value?.copy(
                            isLoading = false,
                            tracks = results,
                            error = null
                        ) ?: SearchUiState(
                            isLoading = false,
                            tracks = results,
                            mode = SearchMode.SEARCH
                        )
                    } catch (e: Exception) {
                        uiState1.value = uiState1.value?.copy(
                            isLoading = false,
                            tracks = emptyList(),
                            error = e.message ?: "Ошибка поиска"
                        ) ?: SearchUiState(isLoading = false, error = e.message, mode = SearchMode.SEARCH)
                        lastFailedQuery = query
                    }
        }
    }

    fun loadHistory (){
        if (historyLoaded) {
            updateUiStateWithHistory(cachedHistory)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val history = searchInteractor.getHistoryTracks()
            cachedHistory = history
            historyLoaded = true
            withContext(Dispatchers.Main) {
                updateUiStateWithHistory(history)
            }
        }
    }

    private fun updateUiStateWithHistory(history: List<Track>) {
        uiState1.value = uiState1.value?.copy(
            mode = SearchMode.HISTORY,
            history = history,
            isLoading = false,
            tracks = emptyList()
        ) ?: SearchUiState(mode = SearchMode.HISTORY, history = history)
    }

    fun clearSearchAndLoadHistory() {
        uiState1.value = uiState1.value?.copy(
            mode = SearchMode.HISTORY,
            tracks = emptyList(),
            isLoading = false
        ) ?: SearchUiState(mode = SearchMode.HISTORY)
        loadHistory()
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