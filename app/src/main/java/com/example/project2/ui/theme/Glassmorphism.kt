package com.example.project2.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a semi-transparent glassmorphism look with an optional neon border.
 */
fun Modifier.glassmorphism(
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    backgroundColor: Color = GlassSurface,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(backgroundColor, shape)
    .border(borderWidth, borderColor, shape)

/**
 * Applies a glassmorphism look with a gradient neon border.
 */
fun Modifier.neonGlassmorphism(
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    backgroundColor: Color = GlassSurface,
    borderBrush: Brush = NeonPurplePinkGradient,
    borderWidth: Dp = 1.5.dp
): Modifier = this
    .clip(shape)
    .background(backgroundColor, shape)
    .border(borderWidth, borderBrush, shape)

/**
 * Creates a soft neon glow around the element.
 */
fun Modifier.neonGlow(
    color: Color = NeonPurple,
    radius: Dp = 12.dp,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp)
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                this.color = color.copy(alpha = 0.35f).toArgb()
                setShadowLayer(
                    radius.toPx(),
                    0f,
                    0f,
                    color.toArgb()
                )
            }
        }
        canvas.drawRoundRect(
            0f,
            0f,
            size.width,
            size.height,
            shape.topStart.toPx(size, this),
            shape.topEnd.toPx(size, this),
            paint
        )
    }
}
