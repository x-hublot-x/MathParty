package com.example.project2.ui.screens.telephone

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project2.data.model.DrawingStroke
import com.example.project2.data.model.MathVisualDiagram
import com.example.project2.data.model.PlayerIteration
import com.example.project2.data.model.TelephonePhase
import com.example.project2.data.repository.MathRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TelephoneUiState(
    val playerCount: Int = 4,
    val memorizeDuration: Int = 10, // 5, 10, 15 seconds
    val phase: TelephonePhase = TelephonePhase.GATE_START,
    val isSetupActive: Boolean = true,
    val isLoading: Boolean = false,
    val visualDiagram: MathVisualDiagram? = null,
    val currentPlayerIndex: Int = 0,
    val countdownSeconds: Int = 10,
    val iterations: List<PlayerIteration> = emptyList(),
    val finalGuessText: String = ""
)

class TelephoneViewModel(
    private val repository: MathRepository = MathRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TelephoneUiState())
    val uiState: StateFlow<TelephoneUiState> = _uiState.asStateFlow()

    val activeStrokes = mutableStateListOf<DrawingStroke>()

    private var countdownJob: Job? = null

    fun setPlayerCount(count: Int) {
        _uiState.update { it.copy(playerCount = count.coerceIn(3, 8)) }
    }

    fun setMemorizeDuration(seconds: Int) {
        _uiState.update { it.copy(memorizeDuration = seconds.coerceIn(5, 30)) }
    }

    fun startGame() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isSetupActive = false) }
            val diagram = repository.getTelephoneDiagram()
            activeStrokes.clear()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    visualDiagram = diagram,
                    currentPlayerIndex = 0,
                    iterations = emptyList(),
                    finalGuessText = "",
                    phase = TelephonePhase.GATE_START
                )
            }
        }
    }

    fun reloadDiagram() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val diagram = repository.getTelephoneDiagram()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    visualDiagram = diagram
                )
            }
        }
    }

    /**
     * Player 1 taps "Посмотреть задание" -> countdown starts and image is shown
     */
    fun startPlayer1Memorize() {
        val duration = _uiState.value.memorizeDuration
        _uiState.update {
            it.copy(
                phase = TelephonePhase.MEMORIZE_ORIGINAL,
                countdownSeconds = duration
            )
        }
        startCountdown(duration) {
            startDrawingPhase()
        }
    }

    fun startDrawingPhase() {
        countdownJob?.cancel()
        activeStrokes.clear()
        _uiState.update {
            it.copy(phase = TelephonePhase.DRAWING)
        }
    }

    /**
     * Player taps "Сохранить и скрыть": drawing is saved, canvas clears immediately,
     * and screen transitions to shielded pass.
     */
    fun saveAndShieldDrawing() {
        val state = _uiState.value
        val playerIdx = state.currentPlayerIndex
        val savedStrokes = activeStrokes.toList()

        val iteration = PlayerIteration(
            playerIndex = playerIdx,
            playerName = "Игрок ${playerIdx + 1}",
            strokes = savedStrokes
        )

        val updatedIterations = state.iterations + iteration
        val nextPlayerIdx = playerIdx + 1
        activeStrokes.clear() // Immediately clear strokes for privacy!

        val nextPhase = if (nextPlayerIdx >= state.playerCount - 1) {
            TelephonePhase.SHIELD_PASS // Next player is the guesser!
        } else {
            TelephonePhase.SHIELD_PASS
        }

        _uiState.update {
            it.copy(
                iterations = updatedIterations,
                currentPlayerIndex = nextPlayerIdx,
                phase = nextPhase
            )
        }
    }

    /**
     * Next player confirms they hold the device -> ready gate
     */
    fun onHandedToNextPlayer() {
        val state = _uiState.value
        val isGuesser = state.currentPlayerIndex >= state.playerCount - 1

        if (isGuesser) {
            _uiState.update { it.copy(phase = TelephonePhase.FINAL_GUESS) }
        } else {
            _uiState.update { it.copy(phase = TelephonePhase.GATE_NEXT_VIEW) }
        }
    }

    /**
     * Player N taps "Посмотреть рисунок" -> countdown starts and previous drawing is revealed
     */
    fun startPlayerMemorizeDrawing() {
        val duration = _uiState.value.memorizeDuration
        _uiState.update {
            it.copy(
                phase = TelephonePhase.MEMORIZE_DRAWING,
                countdownSeconds = duration
            )
        }
        startCountdown(duration) {
            startDrawingPhase()
        }
    }

    fun submitFinalGuess(guess: String) {
        _uiState.update {
            it.copy(
                finalGuessText = guess,
                phase = TelephonePhase.GALLERY
            )
        }
    }

    private fun startCountdown(seconds: Int, onFinish: () -> Unit) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var current = seconds
            while (current > 0) {
                delay(1000)
                current--
                _uiState.update { it.copy(countdownSeconds = current) }
            }
            onFinish()
        }
    }

    fun resetToSetup() {
        countdownJob?.cancel()
        activeStrokes.clear()
        _uiState.update {
            it.copy(
                isSetupActive = true,
                isLoading = false,
                iterations = emptyList(),
                finalGuessText = "",
                currentPlayerIndex = 0
            )
        }
    }
}
