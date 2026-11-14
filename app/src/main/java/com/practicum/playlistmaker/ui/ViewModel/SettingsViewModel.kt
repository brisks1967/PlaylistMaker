package com.practicum.playlistmaker.ui.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.domain.api.ThemeRepository


class SettingsViewModel (private val themeRepository: ThemeRepository) : ViewModel() {
    private val themeState0 = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> get() = themeState0

    fun loadTheme() {
        themeState0.value = themeRepository.isDarkThemeEnabled()
    }
    fun setTheme(isEnabled: Boolean) {
        themeRepository.setDarkThemeEnabled(isEnabled)
        themeState0.value = isEnabled
    }
}

class SettingsViewModelFactory(private val themeRepository: ThemeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(themeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}