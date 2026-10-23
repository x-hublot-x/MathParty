package com.example.project2.ui.screens.forehead

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project2.data.model.ForeheadCard
import com.example.project2.data.model.ForeheadCategory
import com.example.project2.data.repository.MathRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ForeheadScreenState {
    object Setup : ForeheadScreenState
    object Loading : ForeheadScreenState
    object Playing : ForeheadScreenState
    object Results : ForeheadScreenState
}

data class ForeheadUiState(
    val category: ForeheadCategory = ForeheadCategory.MATHEMATICIANS,
    val durationSeconds: Int = 90,
    val remainingSeconds: Int = 90,
    val screenState: ForeheadScreenState = ForeheadScreenState.Setup,
    val deck: List<ForeheadCard> = emptyList(),
    val currentCardIndex: Int = 0,
    val guessedCards: List<ForeheadCard> = emptyList(),
    val passedCards: List<ForeheadCard> = emptyList()
) {
    val currentCard: ForeheadCard?
        get() = deck.getOrNull(currentCardIndex)

    val score: Int
        get() = guessedCards.size
}

class ForeheadViewModel(
    private val repository: MathRepository = MathRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForeheadUiState())
    val uiState: StateFlow<ForeheadUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun setCategory(category: ForeheadCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun setDuration(seconds: Int) {
        _uiState.update { it.copy(durationSeconds = seconds, remainingSeconds = seconds) }
    }

    fun startGame() {
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = ForeheadScreenState.Loading) }
            val cards = repository.getForeheadCards(_uiState.value.category, count = 25)

            _uiState.update {
                it.copy(
                    screenState = ForeheadScreenState.Playing,
                    deck = cards,
                    currentCardIndex = 0,
                    remainingSeconds = it.durationSeconds,
                    guessedCards = emptyList(),
                    passedCards = emptyList()
                )
            }

            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1000)
                val newSeconds = _uiState.value.remainingSeconds - 1
                if (newSeconds <= 0) {
                    _uiState.update {
                        it.copy(remainingSeconds = 0, screenState = ForeheadScreenState.Results)
                    }
                    break
                } else {
                    _uiState.update { it.copy(remainingSeconds = newSeconds) }
                }
            }
        }
    }

    fun onCorrect() {
        val current = _uiState.value.currentCard ?: return
        val nextIdx = _uiState.value.currentCardIndex + 1
        _uiState.update { state ->
            val updatedGuessed = state.guessedCards + current
            if (nextIdx >= state.deck.size) {
                state.copy(
                    guessedCards = updatedGuessed,
                    screenState = ForeheadScreenState.Results
                )
            } else {
                state.copy(
                    guessedCards = updatedGuessed,
                    currentCardIndex = nextIdx
                )
            }
        }
    }

    fun onPass() {
        val current = _uiState.value.currentCard ?: return
        val nextIdx = _uiState.value.currentCardIndex + 1
        _uiState.update { state ->
            val updatedPassed = state.passedCards + current
            if (nextIdx >= state.deck.size) {
                state.copy(
                    passedCards = updatedPassed,
                    screenState = ForeheadScreenState.Results
                )
            } else {
                state.copy(
                    passedCards = updatedPassed,
                    currentCardIndex = nextIdx
                )
            }
        }
    }

    fun finishEarly() {
        timerJob?.cancel()
        _uiState.update { it.copy(screenState = ForeheadScreenState.Results) }
    }

    fun resetToSetup() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                screenState = ForeheadScreenState.Setup,
                deck = emptyList(),
                currentCardIndex = 0,
                guessedCards = emptyList(),
                passedCards = emptyList()
            )
        }
    }
}
