package com.example.dailyword.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyword.data.model.Word
import com.example.dailyword.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed class UiState {
    object Loading : UiState()
    data class Success(val word: Word) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(
    private val repository: WordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _refreshCount = MutableStateFlow(0)
    val refreshCount: StateFlow<Int> = _refreshCount.asStateFlow()

    private var currentLevel: String? = null

    /**
     * Determines whether the user is eligible to refresh words.
     * Easy mode has unlimited refreshes for all users (free & premium).
     * Medium mode allows 5 refreshes for free users, unlimited for premium.
     * Hard mode requires Premium membership.
     */
    fun canRefresh(isPremium: Boolean, level: String? = currentLevel): Boolean {
        if (isPremium) return true
        val targetLevel = (level ?: currentLevel ?: "medium").lowercase()
        if (targetLevel == "easy") return true
        return _refreshCount.value < 5
    }

    fun loadWord(level: String, isPremium: Boolean, forceRefresh: Boolean = false) {
        // Prevent unnecessary reloading if a word is already loaded for this level and not forceRefresh
        if (!forceRefresh && _uiState.value is UiState.Success && currentLevel == level) {
            return
        }

        // Check refresh limit for free tier
        if (forceRefresh && !canRefresh(isPremium, level)) {
            _uiState.value = UiState.Error("Daily refresh limit reached (5/5). Upgrade to Premium for unlimited refreshes.")
            return
        }

        currentLevel = level
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getRandomWord(level, fullDefinition = isPremium)
                .onSuccess { word ->
                    if (forceRefresh && level.lowercase() != "easy") {
                        _refreshCount.value += 1
                    }
                    _uiState.value = UiState.Success(word)
                }
                .onFailure { exception ->
                    val errorMessage = when (exception) {
                        is HttpException -> "Failed to load word (HTTP ${exception.code()}). Please try again."
                        is IOException -> "Network error. Please check your internet connection."
                        else -> exception.localizedMessage ?: "An error occurred while loading word."
                    }
                    _uiState.value = UiState.Error(errorMessage)
                }
        }
    }
}
