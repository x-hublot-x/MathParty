package com.example.project2.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Onyx Backgrounds
val PureOnyx = Color(0xFF090A0C)
val DeepOnyx = Color(0xFF0E0F12)
val OnyxSurface = Color(0xFF141519)
val OnyxSurfaceElevated = Color(0xFF1B1C22)

// Card & Glass Surfaces
val GlassSurface = Color(0xF214151A)
val GlassSurfaceLight = Color(0x22FFFFFF)
val GlassBorder = Color(0x26FFFFFF) // 15% white crisp border
val GlassBorderStrong = Color(0x66FFFFFF) // 40% white border

// Pure White & Monochromatic Accents
val PureWhite = Color(0xFFFFFFFF)
val SoftWhite = Color(0xFFF1F5F9)
val SilverMuted = Color(0xFF94A3B8)
val CharcoalDark = Color(0xFF090A0C)

// Functional Minimalist Accents (for role / warning)
val SpyCrimson = Color(0xFFF43F5E)
val SuccessMint = Color(0xFF34D399)
val WarningAmber = Color(0xFFFBBF24)

// Backward compatibility aliases mapped to sleek Onyx & White
val DeepObsidian = PureOnyx
val DeepNavy = DeepOnyx
val DarkNavy = OnyxSurface
val DarkSlate = OnyxSurface
val TextBrightWhite = PureWhite
val TextMutedSlate = SilverMuted
val NeonPurple = PureWhite
val CyberLime = PureWhite
val VibrantPink = SpyCrimson
val CyberCyan = SoftWhite
val ElectricAmber = WarningAmber
val ElectricRed = SpyCrimson

// Gradients
val WhiteButtonGradient = Brush.linearGradient(
    colors = listOf(PureWhite, SoftWhite)
)

val WhiteOutlineGradient = Brush.linearGradient(
    colors = listOf(PureWhite, Color(0x44FFFFFF))
)

val OnyxCardGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF191A20),
        Color(0xFF111216)
    )
)

val NeonPurplePinkGradient = Brush.linearGradient(
    colors = listOf(PureWhite, Color(0x88FFFFFF))
)

val CyberCyanLimeGradient = Brush.linearGradient(
    colors = listOf(PureWhite, SoftWhite)
)