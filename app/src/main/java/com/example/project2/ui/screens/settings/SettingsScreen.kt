package com.example.project2.ui.screens.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project2.data.model.AiSettings
import com.example.project2.data.repository.MathRepository
import com.example.project2.ui.components.NeonGlassCard
import com.example.project2.ui.components.NeonGradientButton
import com.example.project2.ui.components.SubtleGlassCard
import com.example.project2.ui.theme.GlassBorder
import com.example.project2.ui.theme.OnyxSurface
import com.example.project2.ui.theme.PureOnyx
import com.example.project2.ui.theme.PureWhite
import com.example.project2.ui.theme.SilverMuted
import com.example.project2.ui.theme.SoftWhite

@Composable
fun SettingsScreen(
    repository: MathRepository,
    onNavigateBack: () -> Unit
) {
    val currentSettings by repository.aiSettings.collectAsState()

    var useRemoteAi by remember { mutableStateOf(currentSettings.useRemoteAi) }
    var apiKey by remember { mutableStateOf(currentSettings.apiKey) }
    var endpointUrl by remember { mutableStateOf(currentSettings.endpointUrl) }
    var modelName by remember { mutableStateOf(currentSettings.modelName) }
    var isSavedNoticeVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureOnyx)
    ) {
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
                Text(
                    text = "Настройки ИИ и игры",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Toggle
            NeonGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderBrush = SolidColor(if (useRemoteAi) PureWhite else GlassBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Генерация через LLM API",
                                    color = PureWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (useRemoteAi) "Внешний ИИ (OpenAI / Yandex)" else "Оффлайн-база (250+ терминов)",
                                    color = SilverMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Switch(
                            checked = useRemoteAi,
                            onCheckedChange = { useRemoteAi = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureOnyx,
                                checkedTrackColor = PureWhite,
                                uncheckedThumbColor = SilverMuted,
                                uncheckedTrackColor = OnyxSurface
                            )
                        )
                    }

                    if (!useRemoteAi) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "💡 Сейчас включена встроенная база: всё работает мгновенно без интернета и ключей!",
                            color = SoftWhite,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Remote AI settings form
            if (useRemoteAi) {
                Spacer(modifier = Modifier.height(18.dp))

                SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Параметры API (OpenAI / YandexGPT / Custom)",
                            color = PureWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Endpoint URL
                        OutlinedTextField(
                            value = endpointUrl,
                            onValueChange = { endpointUrl = it },
                            label = { Text("Base URL API", color = SilverMuted) },
                            placeholder = { Text("https://api.openai.com/v1/", color = SilverMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PureWhite,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // API Key
                        OutlinedTextField(
                            value = apiKey,
                            onValueChange = { apiKey = it },
                            label = { Text("API Key", color = SilverMuted) },
                            placeholder = { Text("sk-...", color = SilverMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PureWhite,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Model name
                        OutlinedTextField(
                            value = modelName,
                            onValueChange = { modelName = it },
                            label = { Text("Model Name", color = SilverMuted) },
                            placeholder = { Text("gpt-4o-mini", color = SilverMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PureWhite,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save button
            NeonGradientButton(
                text = "Сохранить настройки 💾",
                onClick = {
                    repository.updateSettings(
                        AiSettings(
                            useRemoteAi = useRemoteAi,
                            apiKey = apiKey,
                            endpointUrl = endpointUrl,
                            modelName = modelName
                        )
                    )
                    isSavedNoticeVisible = true
                }
            )

            if (isSavedNoticeVisible) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "✅ Настройки сохранены!",
                    color = PureWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // About MathParty
            SubtleGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "🦉 О MathParty Builder",
                        color = PureWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Архитектор веселой математики: стилизованная кибер-сова в стимпанк-очках со знаком интеграла ∫, геймпадом и S Pen стилусом.\n\nСоздано для студенческих вечеринок, тренировки математической интуиции и веселья без занудства!",
                        color = SilverMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
