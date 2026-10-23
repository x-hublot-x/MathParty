package com.example.project2.ui.screens.spy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.project2.data.model.CourseLevel
import com.example.project2.data.model.MathDifficultyLevel
import com.example.project2.data.model.SpyRole
import com.example.project2.ui.components.CircularNeonTimer
import com.example.project2.ui.components.FlipCard
import com.example.project2.ui.components.NeonGlassCard
import com.example.project2.ui.components.NeonGradientButton
import com.example.project2.ui.components.NeonOutlineButton
import com.example.project2.ui.components.SubtleGlassCard
import com.example.project2.ui.theme.GlassBorder
import com.example.project2.ui.theme.GlassBorderStrong
import com.example.project2.ui.theme.OnyxSurface
import com.example.project2.ui.theme.PureOnyx
import com.example.project2.ui.theme.PureWhite
import com.example.project2.ui.theme.SilverMuted
import com.example.project2.ui.theme.SoftWhite
import com.example.project2.ui.theme.SpyCrimson
import kotlin.math.roundToInt

@Composable
fun SpyScreen(
    viewModel: SpyViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureOnyx)
    ) {
        when (state.screenState) {
            is SpyScreenState.Setup -> {
                SpySetupContent(
                    viewModel = viewModel,
                    onNavigateBack = onNavigateBack
                )
            }

            is SpyScreenState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Подготовка секретного термина...",
                            color = PureWhite,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            is SpyScreenState.Passing -> {
                SpyPassingContent(
                    state = state,
                    onFlipToggle = viewModel::toggleCardFlip,
                    onNextPlayer = viewModel::nextPlayer,
                    onNavigateBack = viewModel::resetToSetup
                )
            }

            is SpyScreenState.Discussion -> {
                SpyDiscussionContent(
                    state = state,
                    onToggleTimer = viewModel::toggleTimer,
                    onAddSeconds = { viewModel.addTimerSeconds(30) },
                    onGoToReveal = viewModel::goToReveal,
                    onNavigateBack = viewModel::resetToSetup
                )
            }

            is SpyScreenState.Reveal -> {
                SpyRevealContent(
                    state = state,
                    onCheckGuess = viewModel::checkSpyGuess,
                    onNewRound = viewModel::resetToSetup,
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun SpySetupContent(
    viewModel: SpyViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val config = state.config
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = PureWhite
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Математический Шпион",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Text(
                    text = "Настройка партии",
                    fontSize = 13.sp,
                    color = SilverMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Players Count Selector
        SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Игроки", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("От 3 до 12 человек", color = SilverMuted, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.updatePlayerCount(config.playerCount - 1) },
                        enabled = config.playerCount > 3
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Меньше", tint = PureWhite)
                    }
                    Text(
                        text = "${config.playerCount}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(
                        onClick = { viewModel.updatePlayerCount(config.playerCount + 1) },
                        enabled = config.playerCount < 12
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Больше", tint = PureWhite)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Chaos Mode Toggle Card
        NeonGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderBrush = androidx.compose.ui.graphics.SolidColor(if (config.isChaosMode) PureWhite else GlassBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Хаос-режим",
                            tint = PureWhite,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "🎲 Рандомные шпионы",
                                color = PureWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Режим Хаоса",
                                color = SilverMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Switch(
                        checked = config.isChaosMode,
                        onCheckedChange = viewModel::toggleChaosMode,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureOnyx,
                            checkedTrackColor = PureWhite,
                            uncheckedThumbColor = SilverMuted,
                            uncheckedTrackColor = OnyxSurface
                        )
                    )
                }

                if (config.isChaosMode) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "В игре может оказаться 0, 1, 2 или больше шпионов в тайне от всех!",
                        fontSize = 12.sp,
                        color = SoftWhite,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Fixed Spy count (if Chaos Mode is disabled)
        if (!config.isChaosMode) {
            Spacer(modifier = Modifier.height(14.dp))
            SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Шпионы", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text("Количество шпионов в игре", color = SilverMuted, fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.updateSpyCount(config.spyCount - 1) },
                            enabled = config.spyCount > 1
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Меньше", tint = PureWhite)
                        }
                        Text(
                            text = "${config.spyCount}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { viewModel.updateSpyCount(config.spyCount + 1) },
                            enabled = config.spyCount < config.playerCount / 2
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Больше", tint = PureWhite)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 1. Course Level Slider (11-й класс -> 4-й курс)
        SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                val currentCourse = CourseLevel.fromLevel(config.courseLevel)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Уровень математики",
                            color = PureWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = currentCourse.subtitle,
                            color = SilverMuted,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentCourse.title,
                        color = PureWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Slider(
                    value = config.courseLevel.toFloat(),
                    onValueChange = { viewModel.updateCourseLevel(it.roundToInt()) },
                    valueRange = 0f..4f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = PureWhite,
                        activeTrackColor = PureWhite,
                        inactiveTrackColor = OnyxSurface,
                        activeTickColor = PureOnyx,
                        inactiveTickColor = GlassBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val courseLabels = listOf("11 кл", "1 курс", "2 курс", "3 курс", "4 курс")
                    courseLabels.forEachIndexed { index, label ->
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            color = if (config.courseLevel == index) PureWhite else SilverMuted,
                            fontWeight = if (config.courseLevel == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Difficulty Selector: 3 clean buttons (Легко, Средне, Сложно)
        SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                val currentDifficulty = MathDifficultyLevel.fromLevel(config.difficultyLevel)
                Text(
                    text = "Сложность",
                    color = PureWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = currentDifficulty.subtitle,
                    color = SilverMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val levels = listOf(
                        0 to "Легко",
                        1 to "Средне",
                        2 to "Сложно"
                    )

                    levels.forEach { (levelIndex, title) ->
                        val isSelected = config.difficultyLevel == levelIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PureWhite else OnyxSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) PureWhite else GlassBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setDifficultyLevel(levelIndex) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) PureOnyx else PureWhite,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Start Button (Pure White on Onyx)
        NeonGradientButton(
            text = "Начать партию",
            onClick = viewModel::startRound
        )
    }
}

@Composable
private fun SpyPassingContent(
    state: SpyUiState,
    onFlipToggle: () -> Unit,
    onNextPlayer: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val round = state.roundData ?: return
    val currentPlayer = round.players.getOrNull(state.currentPlayerIndex) ?: return
    val cardPlayer = round.players.getOrNull(state.cardPlayerIndex) ?: currentPlayer
    val isLastPlayer = state.currentPlayerIndex == round.players.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = PureWhite)
            }
            Text(
                text = "Передача устройства",
                color = SilverMuted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Pass Prompt
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Передайте телефон:",
                color = SilverMuted,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentPlayer.name,
                color = PureWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Игрок ${state.currentPlayerIndex + 1} из ${round.players.size}",
                color = SilverMuted,
                fontSize = 13.sp
            )
        }

        // 3D Flip Card: Portrait Elongated Proportion (~1:1.45)
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .fillMaxWidth()
                .aspectRatio(0.68f)
        ) {
            FlipCard(
                isFlipped = state.isCardFlipped,
                onFlipToggle = onFlipToggle,
                enabled = !state.isTransitioning,
                modifier = Modifier.fillMaxSize(),
                frontContent = {
                    // Card Front: Elegant Mystery Card
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(22.dp))
                            .background(OnyxSurface)
                            .border(1.5.dp, GlassBorderStrong, RoundedCornerShape(22.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x1AFFFFFF))
                                    .border(1.dp, GlassBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Секрет",
                                    tint = PureWhite,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Секретная карта",
                                color = PureWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Нажмите, чтобы перевернуть карту",
                                color = SilverMuted,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                backContent = {
                    // Card Back: Role & Term (strictly bound to cardPlayer until card finishes closing!)
                    val isSpy = cardPlayer.role == SpyRole.SPY
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (isSpy) Color(0xFF1F1115) else Color(0xFF14171E))
                            .border(
                                1.5.dp,
                                if (isSpy) SpyCrimson else PureWhite,
                                RoundedCornerShape(22.dp)
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (isSpy) {
                                Text(
                                    text = "ТЫ ШПИОН",
                                    color = SpyCrimson,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Ты не знаешь термин!",
                                    color = PureWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Слушай других, делай вид, что в теме, и попробуй отгадать секретное слово.",
                                    color = SilverMuted,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            } else {
                                Text(
                                    text = "ТВОЯ РОЛЬ",
                                    color = SilverMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = round.term,
                                    color = PureWhite,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 32.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = round.description,
                                    color = SoftWhite,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            )
        }

        // Bottom Action Button
        Column(modifier = Modifier.fillMaxWidth()) {
            if (state.isCardFlipped) {
                NeonGradientButton(
                    text = if (isLastPlayer) "К обсуждению ⏳" else "Скрыть и передать ➡️",
                    onClick = onNextPlayer,
                    enabled = !state.isTransitioning
                )
            } else {
                NeonOutlineButton(
                    text = "Показать роль 👁️",
                    onClick = onFlipToggle,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isTransitioning
                )
            }
        }
    }
}

@Composable
private fun SpyDiscussionContent(
    state: SpyUiState,
    onToggleTimer: () -> Unit,
    onAddSeconds: () -> Unit,
    onGoToReveal: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val round = state.roundData ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = PureWhite)
            }
            Text(
                text = "Обсуждение",
                color = PureWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Category Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x1AFFFFFF))
                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = round.category,
                color = PureWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Timer
        CircularNeonTimer(
            totalSeconds = 180,
            remainingSeconds = state.remainingSeconds,
            size = 210.dp,
            label = if (state.isTimerRunning) "ОБСУЖДЕНИЕ" else "ПАУЗА"
        )

        // Timer Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeonOutlineButton(
                text = if (state.isTimerRunning) "Пауза" else "Старт",
                onClick = onToggleTimer,
                icon = {
                    Icon(
                        imageVector = if (state.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = PureWhite
                    )
                }
            )

            NeonOutlineButton(
                text = "+30 сек",
                onClick = onAddSeconds
            )
        }

        // Vote / Reveal Button
        NeonGradientButton(
            text = "Раскрыть роли 🎯",
            onClick = onGoToReveal
        )
    }
}

@Composable
private fun SpyRevealContent(
    state: SpyUiState,
    onCheckGuess: (String) -> Unit,
    onNewRound: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val round = state.roundData ?: return
    var spyGuessInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = PureWhite)
            }
            Text(
                text = "Итоги партии",
                color = PureWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Secret Term Card
        NeonGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderBrush = androidx.compose.ui.graphics.SolidColor(PureWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ЗАГАДАННЫЙ ТЕРМИН",
                    color = SilverMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = round.term,
                    color = PureWhite,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = round.description,
                    color = SilverMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Players & Roles List
        Text(
            text = "Роли (${round.actualSpyCount} шпионов из ${round.players.size})",
            color = PureWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        round.players.forEach { player ->
            val isSpy = player.role == SpyRole.SPY
            SubtleGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                borderColor = if (isSpy) SpyCrimson else GlassBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = player.name,
                        color = PureWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSpy) SpyCrimson.copy(alpha = 0.2f) else Color(0x1AFFFFFF))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isSpy) "🕵️ ШПИОН" else "🎓 СТУДЕНТ",
                            color = if (isSpy) SpyCrimson else PureWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Spy Last Chance to Guess Term
        SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Шанс шпиона отгадать термин 🏆",
                    color = PureWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Если шпион угадает термин, победа достаётся ему:",
                    color = SilverMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = spyGuessInput,
                        onValueChange = { spyGuessInput = it },
                        placeholder = { Text("Версия шпиона...", color = SilverMuted, fontSize = 14.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PureWhite,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    NeonOutlineButton(
                        text = "Проверить",
                        onClick = { onCheckGuess(spyGuessInput) },
                        height = 54.dp
                    )
                }

                state.spyGuessResult?.let { isCorrect ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isCorrect) "🎉 ШПИОН УГАДАЛ И ПОБЕДИЛ!" else "❌ НЕВЕРНО! ПОБЕДА СТУДЕНТОВ!",
                        color = if (isCorrect) PureWhite else SpyCrimson,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        NeonGradientButton(
            text = "Сыграть ещё раз",
            onClick = onNewRound
        )
    }
}
