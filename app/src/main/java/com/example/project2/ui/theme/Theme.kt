package com.example.project2.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MathPartyColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = CharcoalDark,
    primaryContainer = OnyxSurfaceElevated,
    onPrimaryContainer = PureWhite,
    secondary = PureWhite,
    onSecondary = CharcoalDark,
    secondaryContainer = OnyxSurface,
    onSecondaryContainer = PureWhite,
    tertiary = PureWhite,
    onTertiary = CharcoalDark,
    background = PureOnyx,
    onBackground = PureWhite,
    surface = OnyxSurface,
    onSurface = PureWhite,
    surfaceVariant = DeepOnyx,
    onSurfaceVariant = SilverMuted,
    error = SpyCrimson,
    onError = PureWhite
)

@Composable
fun MathPartyTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DeepObsidian.toArgb()
                window.navigationBarColor = DeepObsidian.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = MathPartyColorScheme,
        typography = Typography,
        content = content
    )
}