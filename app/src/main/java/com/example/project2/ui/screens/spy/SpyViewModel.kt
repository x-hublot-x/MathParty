package com.example.project2.ui.screens.spy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project2.data.model.CourseLevel
import com.example.project2.data.model.MathDifficultyLevel
import com.example.project2.data.model.Player
import com.example.project2.data.model.SpyConfig
import com.example.project2.data.model.SpyRole
import com.example.project2.data.model.SpyRoundData
import com.example.project2.data.repository.MathRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface SpyScreenState {
    object Setup : SpyScreenState
    object Loading : SpyScreenState
    object Passing : SpyScreenState
    object Discussion : SpyScreenState
    object Reveal : SpyScreenState
}

data class SpyUiState(
    val config: SpyConfig = SpyConfig(),
    val screenState: SpyScreenState = SpyScreenState.Setup,
    val roundData: SpyRoundData? = null,
    val currentPlayerIndex: Int = 0,
    val cardPlayerIndex: Int = 0,
    val isCardFlipped: Boolean = false,
    val isTransitioning: Boolean = false,
    val remainingSeconds: Int = 180,
    val isTimerRunning: Boolean = false,
    val spyGuessText: String = "",
    val spyGuessResult: Boolean? = null
)

class SpyViewModel(
    private val repository: MathRepository = MathRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpyUiState())
    val uiState: StateFlow<SpyUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun updatePlayerCount(count: Int) {
        val validCount = count.coerceIn(3, 12)
        val maxSpies = (validCount / 2).coerceAtLeast(1)
        _uiState.update { state ->
            val updatedConfig = state.config.copy(
                playerCount = validCount,
                spyCount = state.config.spyCount.coerceAtMost(maxSpies)
            )
            state.copy(config = updatedConfig)
        }
    }

    fun updateSpyCount(count: Int) {
        val maxSpies = (_uiState.value.config.playerCount / 2).coerceAtLeast(1)
        val validCount = count.coerceIn(1, maxSpies)
        _uiState.update { state ->
            state.copy(config = state.config.copy(spyCount = validCount))
        }
    }

    fun toggleChaosMode(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(config = state.config.copy(isChaosMode = enabled))
        }
    }

    fun updateCourseLevel(level: Int) {
        val validLevel = level.coerceIn(0, 4)
        _uiState.update { state ->
            state.copy(config = state.config.copy(courseLevel = validLevel))
        }
    }

    fun setDifficultyLevel(level: Int) {
        val validLevel = level.coerceIn(0, 2)
        _uiState.update { state ->
            state.copy(config = state.config.copy(difficultyLevel = validLevel))
        }
    }

    fun startRound() {
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = SpyScreenState.Loading) }
            val config = _uiState.value.config
            val word = repository.getSpyTerm(config.courseLevel, config.difficultyLevel)

            // Calculate actual spies: in Chaos Mode, random from 0 to N/2
            val maxSpies = (config.playerCount / 2).coerceAtLeast(1)
            val actualSpyCount = if (config.isChaosMode) {
                Random.nextInt(0, maxSpies + 1)
            } else {
                config.spyCount
            }

            // Generate player list and randomly assign spies
            val allIndices = (0 until config.playerCount).shuffled()
            val spyIndices = allIndices.take(actualSpyCount).toSet()

            val players = (0 until config.playerCount).map { idx ->
                Player(
                    id = idx,
                    name = "Игрок ${idx + 1}",
                    role = if (spyIndices.contains(idx)) SpyRole.SPY else SpyRole.INNOCENT
                )
            }

            val course = CourseLevel.fromLevel(config.courseLevel)
            val difficulty = MathDifficultyLevel.fromLevel(config.difficultyLevel)
            val roundData = SpyRoundData(
                term = word.term,
                category = "${course.title} • ${difficulty.title}",
                description = word.hint,
                players = players,
                actualSpyCount = actualSpyCount,
                courseLevel = course,
                difficultyLevel = difficulty
            )

            _uiState.update {
                it.copy(
                    screenState = SpyScreenState.Passing,
                    roundData = roundData,
                    currentPlayerIndex = 0,
                    cardPlayerIndex = 0,
                    isCardFlipped = false,
                    isTransitioning = false,
                    remainingSeconds = 180,
                    isTimerRunning = false,
                    spyGuessText = "",
                    spyGuessResult = null
                )
            }
        }
    }

    fun toggleCardFlip() {
        if (_uiState.value.isTransitioning) return
        _uiState.update { it.copy(isCardFlipped = !it.isCardFlipped) }
    }

    fun nextPlayer() {
        val currentState = _uiState.value
        if (currentState.isTransitioning) return
        val round = currentState.roundData ?: return
        val nextIndex = currentState.currentPlayerIndex + 1

        viewModelScope.launch {
            if (currentState.isCardFlipped) {
                // 1. Flip card closed while KEEPING current player's role on back
                _uiState.update { it.copy(isCardFlipped = false, isTransitioning = true) }
                // 2. Wait until card completely finishes rotating to 0° (650ms animation + buffer)
                delay(680)
            } else {
                _uiState.update { it.copy(isTransitioning = true) }
            }

            // 3. Now that card is 100% closed and facing forward, advance to next player
            if (nextIndex < round.players.size) {
                _uiState.update {
                    it.copy(
                        currentPlayerIndex = nextIndex,
                        cardPlayerIndex = nextIndex,
                        isCardFlipped = false,
                        isTransitioning = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        screenState = SpyScreenState.Discussion,
                        isCardFlipped = false,
                        isTimerRunning = true,
                        isTransitioning = false
                    )
                }
                startDiscussionTimer()
            }
        }
    }

    private fun startDiscussionTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.isTimerRunning) {
                delay(1000)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
        }
    }

    fun toggleTimer() {
        val isRunning = _uiState.value.isTimerRunning
        if (isRunning) {
            timerJob?.cancel()
            _uiState.update { it.copy(isTimerRunning = false) }
        } else {
            _uiState.update { it.copy(isTimerRunning = true) }
            startDiscussionTimer()
        }
    }

    fun addTimerSeconds(seconds: Int = 30) {
        _uiState.update { it.copy(remainingSeconds = it.remainingSeconds + seconds) }
    }

    fun goToReveal() {
        timerJob?.cancel()
        _uiState.update { it.copy(screenState = SpyScreenState.Reveal, isTimerRunning = false) }
    }

    fun checkSpyGuess(guess: String) {
        val term = _uiState.value.roundData?.term ?: return
        val isCorrect = term.trim().equals(guess.trim(), ignoreCase = true) ||
                (guess.isNotBlank() && term.contains(guess.trim(), ignoreCase = true))
        _uiState.update {
            it.copy(spyGuessText = guess, spyGuessResult = isCorrect)
        }
    }

    fun resetToSetup() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                screenState = SpyScreenState.Setup,
                roundData = null,
                isCardFlipped = false,
                isTimerRunning = false
            )
        }
    }
}
