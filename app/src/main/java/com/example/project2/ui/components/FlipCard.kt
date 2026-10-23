package com.example.project2.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import kotlin.math.sin

@Composable
fun FlipCard(
    isFlipped: Boolean,
    onFlipToggle: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    frontContent: @Composable () -> Unit,
    backContent: @Composable () -> Unit
) {
    val view = LocalView.current
    val density = LocalDensity.current.density

    // Ultra smooth cinematic 3D easing
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 650,
            easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
        ),
        label = "cardFlipAnimation"
    )

    // Subtle lifting effect during the 90-degree midpoint
    val scale = 1f - (sin(Math.toRadians(rotation.toDouble())) * 0.06).toFloat()

    val interactionSource = remember { MutableInteractionSource() }

    val baseModifier = modifier.graphicsLayer {
        rotationY = rotation
        scaleX = scale
        scaleY = scale
        cameraDistance = 18f * density
    }

    val finalModifier = if (enabled) {
        baseModifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                onFlipToggle()
            }
        )
    } else baseModifier

    Box(modifier = finalModifier) {
        if (rotation <= 90f) {
            Box(modifier = Modifier.fillMaxSize()) {
                frontContent()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Invert horizontal orientation so back face doesn't appear mirrored
                        rotationY = 180f
                    }
            ) {
                backContent()
            }
        }
    }
}
