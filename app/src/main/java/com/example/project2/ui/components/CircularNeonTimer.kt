package com.example.project2.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project2.ui.theme.CyberLime
import com.example.project2.ui.theme.ElectricAmber
import com.example.project2.ui.theme.ElectricRed
import com.example.project2.ui.theme.TextBrightWhite
import com.example.project2.ui.theme.TextMutedSlate
import com.example.project2.ui.theme.VibrantPink
import java.util.Locale

@Composable
fun CircularNeonTimer(
    totalSeconds: Int,
    remainingSeconds: Int,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    strokeWidth: Dp = 14.dp,
    label: String? = null
) {
    val progress = if (totalSeconds > 0) {
        (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "timerProgress"
    )

    // Dynamic color transition: Green -> Yellow -> Vibrant Pink/Red
    val targetColor = when {
        remainingSeconds <= 3 || progress <= 0.15f -> ElectricRed
        progress <= 0.45f -> ElectricAmber
        else -> CyberLime
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 400),
        label = "timerColor"
    )

    // Heartbeat / Pulse animation on last 3-5 seconds
    val isUrgent = remainingSeconds in 1..4
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by if (isUrgent) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
    } else {
        animateFloatAsState(1.0f, label = "staticScale")
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Background Track
            drawArc(
                color = Color(0x22FFFFFF),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Active Glowing Neon Arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        animatedColor.copy(alpha = 0.7f),
                        animatedColor
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        // Timer Text Display
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            val timeString = if (totalSeconds >= 60) {
                String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            } else {
                String.format(Locale.getDefault(), "%d", remainingSeconds)
            }

            Text(
                text = timeString,
                fontSize = if (totalSeconds >= 60) 42.sp else 54.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = if (isUrgent) animatedColor else TextBrightWhite
            )

            if (label != null) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = TextMutedSlate,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
