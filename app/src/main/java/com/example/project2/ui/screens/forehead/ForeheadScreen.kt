package com.example.project2.ui.screens.forehead

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project2.data.model.ForeheadCategory
import com.example.project2.ui.components.CircularNeonTimer
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

@Composable
fun ForeheadScreen(
    viewModel: ForeheadViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureOnyx)
    ) {
        when (state.screenState) {
            is ForeheadScreenState.Setup -> {
                ForeheadSetupContent(
                    viewModel = viewModel,
                    onNavigateBack = onNavigateBack
                )
            }

            is ForeheadScreenState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Подготовка колоды...",
                            color = PureWhite,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            is ForeheadScreenState.Playing -> {
                ForeheadPlayingContent(
                    state = state,
                    onCorrect = viewModel::onCorrect,
                    onPass = viewModel::onPass,
                    onFinishEarly = viewModel::finishEarly
                )
            }

            is ForeheadScreenState.Results -> {
                ForeheadResultsContent(
                    state = state,
                    onPlayAgain = viewModel::resetToSetup,
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun ForeheadSetupContent(
    viewModel: ForeheadViewModel,
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = PureWhite
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "«Кто я?» Математический Лоб",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Text(
                    text = "Угадай слово по подсказкам друзей",
                    fontSize = 13.sp,
                    color = SilverMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category Selection
        Text(
            text = "Выберите категорию",
            color = PureWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        ForeheadCategory.values().forEach { cat ->
            val isSelected = state.category == cat
            NeonGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                borderBrush = SolidColor(if (isSelected) PureWhite else GlassBorder),
                onClick = { viewModel.setCategory(cat) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(cat.icon, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = cat.displayName,
                                color = PureWhite,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = when (cat) {
                                    ForeheadCategory.MATHEMATICIANS -> "Тьюринг, Гаусс, Эйлер, Перельман..."
                                    ForeheadCategory.CONCEPTS -> "Формула Эйлера, интегралы, тор..."
                                    ForeheadCategory.MEMES_PARADOXES -> "Кот Шрёдингера, Монти Холл, лента Мёбиуса..."
                                },
                                color = SilverMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Timer Duration Selection
        Text(
            text = "Время раунда",
            color = PureWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(60 to "60 сек", 90 to "90 сек", 120 to "120 сек").forEach { (sec, label) ->
                val isSelected = state.durationSeconds == sec
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
                        .clickable { viewModel.setDuration(sec) }
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

        Spacer(modifier = Modifier.height(24.dp))

        // Rules notice
        SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📱 Как играть:",
                    color = PureWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Приложите телефон ко лбу или держите экраном к друзьям. Друзья объясняют термин словами (без однокоренных), а вы угадываете!",
                    color = SilverMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        NeonGradientButton(
            text = "Начать раунд",
            onClick = viewModel::startGame
        )
    }
}

@Composable
private fun ForeheadPlayingContent(
    state: ForeheadUiState,
    onCorrect: () -> Unit,
    onPass: () -> Unit,
    onFinishEarly: () -> Unit
) {
    val card = state.currentCard ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x1AFFFFFF))
                    .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Счёт: ${state.score}",
                    color = PureWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            CircularNeonTimer(
                totalSeconds = state.durationSeconds,
                remainingSeconds = state.remainingSeconds,
                size = 72.dp,
                strokeWidth = 6.dp
            )

            IconButton(onClick = onFinishEarly) {
                Icon(Icons.Default.Close, contentDescription = "Завершить", tint = SilverMuted)
            }
        }

        // Portrait Elongated Card (~1:1.45 aspect ratio)
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .fillMaxWidth()
                .aspectRatio(0.72f)
                .clip(RoundedCornerShape(22.dp))
                .background(OnyxSurface)
                .border(1.5.dp, GlassBorderStrong, RoundedCornerShape(22.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x1AFFFFFF))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = card.category.displayName,
                        color = SilverMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = card.term,
                    color = PureWhite,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = card.clue,
                    color = SoftWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        // Fast Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            NeonOutlineButton(
                text = "Пас ⏭",
                onClick = onPass,
                modifier = Modifier.weight(1f),
                height = 58.dp
            )

            NeonGradientButton(
                text = "Верно! ✅",
                onClick = onCorrect,
                modifier = Modifier.weight(1.3f),
                height = 58.dp
            )
        }
    }
}

@Composable
private fun ForeheadResultsContent(
    state: ForeheadUiState,
    onPlayAgain: () -> Unit,
    onNavigateBack: () -> Unit
) {
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
                text = "Раунд завершён",
                color = PureWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Score Card
        SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ИТОГОВЫЙ СЧЁТ",
                    color = SilverMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${state.score}",
                    color = PureWhite,
                    fontSize = 60.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = when {
                        state.score >= 10 -> "🏆 Легендарный математик!"
                        state.score >= 5 -> "🎓 Достойный результат!"
                        else -> "📚 Нужно повторить конспекты!"
                    },
                    color = SoftWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Guessed cards list
        if (state.guessedCards.isNotEmpty()) {
            Text(
                text = "Угадано (${state.guessedCards.size})",
                color = PureWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            state.guessedCards.forEach { card ->
                SubtleGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(card.term, color = PureWhite, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.Default.Check, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Passed cards list
        if (state.passedCards.isNotEmpty()) {
            Text(
                text = "Пропущено (${state.passedCards.size})",
                color = SilverMuted,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            state.passedCards.forEach { card ->
                SubtleGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(card.term, color = SilverMuted, fontSize = 14.sp)
                        Icon(Icons.Default.Close, contentDescription = null, tint = SilverMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        NeonGradientButton(
            text = "Сыграть ещё раз",
            onClick = onPlayAgain
        )
    }
}
