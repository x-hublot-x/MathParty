package com.example.project2.ui.screens.telephone

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project2.data.model.TelephonePhase
import com.example.project2.ui.components.CircularNeonTimer
import com.example.project2.ui.components.MathDiagramCard
import com.example.project2.ui.components.NeonGlassCard
import com.example.project2.ui.components.NeonGradientButton
import com.example.project2.ui.components.NeonOutlineButton
import com.example.project2.ui.components.RenderStrokesCanvas
import com.example.project2.ui.components.StylusCanvasView
import com.example.project2.ui.components.SubtleGlassCard
import com.example.project2.ui.theme.GlassBorder
import com.example.project2.ui.theme.OnyxSurface
import com.example.project2.ui.theme.PureOnyx
import com.example.project2.ui.theme.PureWhite
import com.example.project2.ui.theme.SilverMuted
import com.example.project2.ui.theme.SoftWhite

@Composable
fun TelephoneScreen(
    viewModel: TelephoneViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureOnyx)
    ) {
        if (state.isSetupActive) {
            TelephoneSetupContent(
                viewModel = viewModel,
                onNavigateBack = onNavigateBack
            )
        } else if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Генерация математической схемы...",
                        color = PureWhite,
                        fontSize = 15.sp
                    )
                }
            }
        } else {
            when (state.phase) {
                TelephonePhase.GATE_START -> {
                    TelephoneGateStartContent(
                        state = state,
                        onStartMemorize = viewModel::startPlayer1Memorize,
                        onReloadDiagram = viewModel::reloadDiagram,
                        onNavigateBack = viewModel::resetToSetup
                    )
                }

                TelephonePhase.MEMORIZE_ORIGINAL -> {
                    TelephoneMemorizeOriginalContent(
                        state = state,
                        onSkipToDraw = viewModel::startDrawingPhase
                    )
                }

                TelephonePhase.DRAWING -> {
                    TelephoneDrawingContent(
                        state = state,
                        viewModel = viewModel
                    )
                }

                TelephonePhase.SHIELD_PASS -> {
                    TelephoneShieldPassContent(
                        state = state,
                        onNext = viewModel::onHandedToNextPlayer
                    )
                }

                TelephonePhase.GATE_NEXT_VIEW -> {
                    TelephoneGateNextViewContent(
                        state = state,
                        onStartView = viewModel::startPlayerMemorizeDrawing
                    )
                }

                TelephonePhase.MEMORIZE_DRAWING -> {
                    TelephoneMemorizeDrawingContent(
                        state = state,
                        onSkipToDraw = viewModel::startDrawingPhase
                    )
                }

                TelephonePhase.FINAL_GUESS -> {
                    TelephoneFinalGuessContent(
                        state = state,
                        onSubmitGuess = viewModel::submitFinalGuess
                    )
                }

                TelephonePhase.GALLERY -> {
                    TelephoneGalleryContent(
                        state = state,
                        onPlayAgain = viewModel::resetToSetup,
                        onNavigateBack = onNavigateBack
                    )
                }
            }
        }
    }
}

@Composable
private fun TelephoneSetupContent(
    viewModel: TelephoneViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = PureWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Испорченный Телефон",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Text(
                    text = "Математические схемы по памяти",
                    fontSize = 13.sp,
                    color = SilverMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Players Count
        SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Количество участников", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("От 3 до 8 человек", color = SilverMuted, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.setPlayerCount(state.playerCount - 1) },
                        enabled = state.playerCount > 3
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Меньше", tint = PureWhite)
                    }
                    Text(
                        text = "${state.playerCount}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(
                        onClick = { viewModel.setPlayerCount(state.playerCount + 1) },
                        enabled = state.playerCount < 8
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Больше", tint = PureWhite)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Memorization Duration Selector
        SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Время на запоминание",
                    color = PureWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Сколько секунд показывается чертёж или рисунок",
                    color = SilverMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(5 to "5 сек", 10 to "10 сек", 15 to "15 сек").forEach { (sec, label) ->
                        val isSelected = state.memorizeDuration == sec
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PureWhite else OnyxSurface)
                                .border(1.dp, if (isSelected) PureWhite else GlassBorder, RoundedCornerShape(12.dp))
                                .clickable { viewModel.setMemorizeDuration(sec) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.Black else PureWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Explanation Card
        SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("📋 Как играть:", color = PureWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                val steps = listOf(
                    "1. Игрок 1 нажимает кнопку и запоминает чертёж (${state.memorizeDuration} сек).",
                    "2. Рисует его по памяти стилусом S Pen и нажимает «Сохранить и скрыть».",
                    "3. Рисунок мгновенно пропадает с экрана для передачи.",
                    "4. Следующий игрок нажимает кнопку, видит рисунок ${state.memorizeDuration} сек и перерисовывает.",
                    "5. Последний игрок угадывает исходный математический образ."
                )
                steps.forEach { step ->
                    Text(step, color = SilverMuted, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        NeonGradientButton(
            text = "Начать игру 🎨",
            onClick = viewModel::startGame
        )
    }
}

/**
 * Gate screen before Player 1 starts memorizing
 */
@Composable
private fun TelephoneGateStartContent(
    state: TelephoneUiState,
    onStartMemorize: () -> Unit,
    onReloadDiagram: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = PureWhite)
            }
            Text("Игрок 1", color = SilverMuted, fontSize = 16.sp)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0x1AFFFFFF))
                    .border(1.5.dp, GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = PureWhite, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Игрок 1, вы готовы?",
                color = PureWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            val sourceText = state.visualDiagram?.source ?: "🌐 Википедия / Интернет"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x22FFFFFF))
                    .border(0.8.dp, Color(0x44FFFFFF), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = sourceText,
                    color = PureWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Убедитесь, что экран видите только вы.\nПосле нажатия кнопки появится изображение/формула, и запустится таймер на ${state.memorizeDuration} секунд.",
                color = SilverMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NeonOutlineButton(
                text = "🔄 Другой чертёж / формула",
                onClick = onReloadDiagram,
                modifier = Modifier.fillMaxWidth()
            )

            NeonGradientButton(
                text = "▶️ Посмотреть задание (${state.memorizeDuration} сек)",
                onClick = onStartMemorize
            )
        }
    }
}

/**
 * Displays the initial visual diagram with countdown
 */
@Composable
private fun TelephoneMemorizeOriginalContent(
    state: TelephoneUiState,
    onSkipToDraw: () -> Unit
) {
    val diagram = state.visualDiagram ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Запоминайте чертёж!",
                color = PureWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            CircularNeonTimer(
                totalSeconds = state.memorizeDuration,
                remainingSeconds = state.countdownSeconds,
                size = 64.dp,
                strokeWidth = 6.dp
            )
        }

        // Visual Math Diagram Card
        MathDiagramCard(
            diagram = diagram,
            modifier = Modifier.fillMaxWidth()
        )

        NeonOutlineButton(
            text = "Запомнил, начать рисовать 🎨",
            onClick = onSkipToDraw,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Canvas drawing phase for current player
 */
@Composable
private fun TelephoneDrawingContent(
    state: TelephoneUiState,
    viewModel: TelephoneViewModel
) {
    val playerIdx = state.currentPlayerIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Игрок ${playerIdx + 1} рисует",
                    color = PureWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Рисуйте по памяти стилусом S Pen",
                    color = SilverMuted,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Canvas View
        StylusCanvasView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            strokes = viewModel.activeStrokes
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Save & Shield button
        NeonGradientButton(
            text = "💾 Сохранить и передать ➡️",
            onClick = viewModel::saveAndShieldDrawing,
            height = 52.dp
        )
    }
}

/**
 * Privacy screen shown immediately after saving drawing
 */
@Composable
private fun TelephoneShieldPassContent(
    state: TelephoneUiState,
    onNext: () -> Unit
) {
    val nextPlayerIdx = state.currentPlayerIndex
    val isGuesser = nextPlayerIdx >= state.playerCount - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0x1AFFFFFF))
                    .border(1.5.dp, GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = PureWhite, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Рисунок скрыт 🔒",
                color = SilverMuted,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Передайте телефон:",
                color = SilverMuted,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Игроку ${nextPlayerIdx + 1}",
                color = PureWhite,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isGuesser) {
                    "Этот игрок финальный! Он увидит рисунок и напишет свою догадку."
                } else {
                    "Следующий игрок увидит рисунок всего на ${state.memorizeDuration} сек после нажатия кнопки."
                },
                color = SilverMuted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }

        NeonGradientButton(
            text = "Телефон передан Игроку ${nextPlayerIdx + 1} 📱",
            onClick = onNext
        )
    }
}

/**
 * Gate screen before Player N starts viewing previous drawing
 */
@Composable
private fun TelephoneGateNextViewContent(
    state: TelephoneUiState,
    onStartView: () -> Unit
) {
    val playerIdx = state.currentPlayerIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0x1AFFFFFF))
                    .border(1.5.dp, GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = PureWhite, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Игрок ${playerIdx + 1}, вы готовы?",
                color = PureWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Никому не показывайте экран.\nПосле нажатия кнопки рисунок предыдущего игрока откроется на ${state.memorizeDuration} сек.",
                color = SilverMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        NeonGradientButton(
            text = "▶️ Посмотреть рисунок (${state.memorizeDuration} сек)",
            onClick = onStartView
        )
    }
}

/**
 * Player N viewing previous drawing with countdown
 */
@Composable
private fun TelephoneMemorizeDrawingContent(
    state: TelephoneUiState,
    onSkipToDraw: () -> Unit
) {
    val prevIteration = state.iterations.lastOrNull() ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Рисунок ${prevIteration.playerName}",
                color = PureWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            CircularNeonTimer(
                totalSeconds = state.memorizeDuration,
                remainingSeconds = state.countdownSeconds,
                size = 64.dp,
                strokeWidth = 6.dp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        RenderStrokesCanvas(
            strokes = prevIteration.strokes,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        NeonOutlineButton(
            text = "Запомнил, начать рисовать 🎨",
            onClick = onSkipToDraw,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Final guess screen for last player
 */
@Composable
private fun TelephoneFinalGuessContent(
    state: TelephoneUiState,
    onSubmitGuess: (String) -> Unit
) {
    val lastIteration = state.iterations.lastOrNull() ?: return
    var guessInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Финальная догадка! 🧠",
                color = PureWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Игрок ${state.playerCount}, что было изображено изначально?",
                color = SilverMuted,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        RenderStrokesCanvas(
            strokes = lastIteration.strokes,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = guessInput,
                onValueChange = { guessInput = it },
                label = { Text("Ваша догадка", color = SilverMuted) },
                placeholder = { Text("например: Трёхмерный тор...", color = SilverMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PureWhite,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            NeonGradientButton(
                text = "Открыть галерею 🎭",
                onClick = { onSubmitGuess(guessInput) },
                enabled = guessInput.isNotBlank()
            )
        }
    }
}

/**
 * Final gallery comparison of original vs iterations vs final guess
 */
@Composable
private fun TelephoneGalleryContent(
    state: TelephoneUiState,
    onPlayAgain: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val diagram = state.visualDiagram

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = PureWhite)
            }
            Text(
                text = "Галерея мутаций 🎭",
                color = PureWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Original Diagram
        if (diagram != null) {
            Text("🌱 ИСХОДНЫЙ ЧЕРТЁЖ (ОРИГИНАЛ)", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            MathDiagramCard(diagram = diagram, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Iteration drawings in sequence
        state.iterations.forEach { iter ->
            SubtleGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Рисунок: ${iter.playerName}",
                        color = PureWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    RenderStrokesCanvas(
                        strokes = iter.strokes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Final Guess Card
        SubtleGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = PureWhite
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "🎯 ДОГАДКА ПОСЛЕДНЕГО ИГРОКА",
                    color = SilverMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = state.finalGuessText.ifBlank { "Ничего не введено" },
                    color = PureWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        NeonGradientButton(
            text = "Сыграть ещё раз 🔄",
            onClick = onPlayAgain
        )
    }
}
