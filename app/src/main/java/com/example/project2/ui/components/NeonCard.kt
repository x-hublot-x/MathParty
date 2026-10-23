package com.example.project2.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.project2.ui.theme.GlassBorder
import com.example.project2.ui.theme.GlassSurface
import com.example.project2.ui.theme.NeonPurplePinkGradient
import com.example.project2.ui.theme.glassmorphism
import com.example.project2.ui.theme.neonGlassmorphism
import com.example.project2.ui.theme.neonGlow

@Composable
fun NeonGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    backgroundColor: Color = GlassSurface,
    borderBrush: Brush = NeonPurplePinkGradient,
    borderWidth: Dp = 1.5.dp,
    glowColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val baseModifier = if (glowColor != null) {
        modifier.neonGlow(color = glowColor, shape = shape)
    } else {
        modifier
    }

    val finalModifier = if (onClick != null) {
        baseModifier
            .neonGlassmorphism(shape = shape, backgroundColor = backgroundColor, borderBrush = borderBrush, borderWidth = borderWidth)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                onClick = onClick
            )
    } else {
        baseModifier.neonGlassmorphism(shape = shape, backgroundColor = backgroundColor, borderBrush = borderBrush, borderWidth = borderWidth)
    }

    Box(
        modifier = finalModifier.padding(1.dp),
        content = content
    )
}

@Composable
fun SubtleGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    backgroundColor: Color = GlassSurface,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val cardModifier = if (onClick != null) {
        modifier
            .glassmorphism(shape = shape, backgroundColor = backgroundColor, borderColor = borderColor, borderWidth = borderWidth)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.15f)),
                onClick = onClick
            )
    } else {
        modifier.glassmorphism(shape = shape, backgroundColor = backgroundColor, borderColor = borderColor, borderWidth = borderWidth)
    }

    Box(
        modifier = cardModifier,
        content = content
    )
}
