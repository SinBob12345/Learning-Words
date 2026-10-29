package com.example.dailyword.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _selectedLevel = MutableStateFlow("medium")
    val selectedLevel: StateFlow<String> = _selectedLevel.asStateFlow()

    private val _showPhonetic = MutableStateFlow(false)
    val showPhonetic: StateFlow<Boolean> = _showPhonetic.asStateFlow()

    private val _showDefinition = MutableStateFlow(true)
    val showDefinition: StateFlow<Boolean> = _showDefinition.asStateFlow()

    private val _showExample = MutableStateFlow(true)
    val showExample: StateFlow<Boolean> = _showExample.asStateFlow()

    private val _showImage = MutableStateFlow(true)
    val showImage: StateFlow<Boolean> = _showImage.asStateFlow()

    private val _autoPlayAudio = MutableStateFlow(false)
    val autoPlayAudio: StateFlow<Boolean> = _autoPlayAudio.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setLevel(level: String) {
        // Advanced "hard" level requires Premium status
        if (level.lowercase() == "hard" && !_isPremium.value) {
            return
        }
        _selectedLevel.value = level
    }

    fun togglePhonetic() {
        // Show Phonetics is available exclusively to Premium members
        if (_isPremium.value) {
            _showPhonetic.value = !_showPhonetic.value
        }
    }

    fun toggleDefinition() {
        _showDefinition.value = !_showDefinition.value
    }

    fun toggleExample() {
        _showExample.value = !_showExample.value
    }

    fun toggleImage() {
        _showImage.value = !_showImage.value
    }

    fun toggleAutoPlayAudio() {
        _autoPlayAudio.value = !_autoPlayAudio.value
    }

    fun upgradeToPremium() {
        _isPremium.value = true
        _showPhonetic.value = true // Automatically unlock and enable phonetics on upgrade
    }

    fun toggleDarkMode() {
        // Dark mode toggle is available exclusively to Premium members
        if (_isPremium.value) {
            _isDarkMode.value = !_isDarkMode.value
        }
    }
}
