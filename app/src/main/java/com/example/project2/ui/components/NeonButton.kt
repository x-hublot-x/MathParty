package com.example.project2.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project2.ui.theme.CharcoalDark
import com.example.project2.ui.theme.GlassBorder
import com.example.project2.ui.theme.OnyxSurface
import com.example.project2.ui.theme.PureWhite
import com.example.project2.ui.theme.SoftWhite
import com.example.project2.ui.theme.WhiteButtonGradient
import com.example.project2.ui.theme.neonGlow

@Composable
fun NeonGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = WhiteButtonGradient,
    textColor: Color = CharcoalDark,
    fontSize: TextUnit = 16.sp,
    height: Dp = 54.dp,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    glowColor: Color = PureWhite,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .then(if (enabled) Modifier.neonGlow(color = glowColor.copy(alpha = 0.25f), radius = 8.dp, shape = shape) else Modifier)
            .clip(shape)
            .background(if (enabled) gradient else Brush.linearGradient(listOf(Color(0xFF333338), Color(0xFF222226))))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = Color.Black.copy(alpha = 0.2f)),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (icon != null) {
                icon()
                Box(modifier = Modifier.padding(end = 10.dp))
            }
            Text(
                text = text,
                color = if (enabled) textColor else Color(0xFF777780),
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        }
    }
}

@Composable
fun NeonOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = PureWhite,
    backgroundColor: Color = OnyxSurface.copy(alpha = 0.7f),
    textColor: Color = PureWhite,
    height: Dp = 50.dp,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    icon: (@Composable () -> Unit)? = null
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(backgroundColor, shape)
            .border(1.2.dp, if (enabled) borderColor else Color(0x33FFFFFF), shape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = PureWhite.copy(alpha = 0.15f)),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 18.dp)
        ) {
            if (icon != null) {
                icon()
                Box(modifier = Modifier.padding(end = 8.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

