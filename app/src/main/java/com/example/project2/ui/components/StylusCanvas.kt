package com.example.project2.ui.components

import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project2.data.model.DrawingStroke
import com.example.project2.data.model.StrokePoint
import com.example.project2.ui.theme.CyberCyan
import com.example.project2.ui.theme.CyberLime
import com.example.project2.ui.theme.DarkNavy
import com.example.project2.ui.theme.DeepNavy
import com.example.project2.ui.theme.DeepObsidian
import com.example.project2.ui.theme.ElectricAmber
import com.example.project2.ui.theme.GlassBorder
import com.example.project2.ui.theme.GlassSurface
import com.example.project2.ui.theme.NeonPurple
import com.example.project2.ui.theme.TextBrightWhite
import com.example.project2.ui.theme.TextMutedSlate
import com.example.project2.ui.theme.VibrantPink

val CYBER_PALETTE = listOf(
    CyberLime,
    NeonPurple,
    VibrantPink,
    CyberCyan,
    ElectricAmber,
    TextBrightWhite
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StylusCanvasView(
    modifier: Modifier = Modifier,
    strokes: MutableList<DrawingStroke>,
    onStrokesChanged: () -> Unit = {}
) {
    val view = LocalView.current
    var currentColor by remember { mutableStateOf(CyberLime) }
    var currentWidth by remember { mutableStateOf(8f) }
    var isEraserActive by remember { mutableStateOf(false) }
    var stylusHardwareEraser by remember { mutableStateOf(false) }

    // Current stroke while drawing
    var activeStrokePoints by remember { mutableStateOf<List<StrokePoint>?>(null) }
    var activeIsEraser by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Toolbar: Palette, Width, Eraser, Undo, Clear
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(DarkNavy)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Colors
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CYBER_PALETTE.forEach { color ->
                    val isSelected = !isEraserActive && currentColor == color
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                currentColor = color
                                isEraserActive = false
                            }
                    )
                }
            }

            // Tools: Pen / Eraser toggle, Undo, Clear
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Eraser Toggle
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        isEraserActive = !isEraserActive
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isEraserActive) Icons.Default.AutoFixHigh else Icons.Default.Brush,
                        contentDescription = "Режим ластика",
                        tint = if (isEraserActive) VibrantPink else TextMutedSlate
                    )
                }

                // Undo
                IconButton(
                    onClick = {
                        if (strokes.isNotEmpty()) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            strokes.removeAt(strokes.lastIndex)
                            onStrokesChanged()
                        }
                    },
                    enabled = strokes.isNotEmpty(),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Отмена",
                        tint = if (strokes.isNotEmpty()) TextBrightWhite else TextMutedSlate.copy(alpha = 0.4f)
                    )
                }

                // Clear
                IconButton(
                    onClick = {
                        if (strokes.isNotEmpty()) {
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            strokes.clear()
                            onStrokesChanged()
                        }
                    },
                    enabled = strokes.isNotEmpty(),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Очистить холст",
                        tint = if (strokes.isNotEmpty()) ElectricAmber else TextMutedSlate.copy(alpha = 0.4f)
                    )
                }
            }
        }

        // Active Canvas Area with MotionEvent tracking for S Pen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(DeepObsidian)
                .border(1.dp, GlassBorder, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .pointerInteropFilter { motionEvent ->
                    val toolType = motionEvent.getToolType(0)
                    val isStylus = toolType == MotionEvent.TOOL_TYPE_STYLUS
                    val isHardwareEraser = toolType == MotionEvent.TOOL_TYPE_ERASER ||
                            (motionEvent.buttonState and MotionEvent.BUTTON_STYLUS_PRIMARY != 0)

                    stylusHardwareEraser = isHardwareEraser

                    val effectiveEraser = isEraserActive || isHardwareEraser
                    activeIsEraser = effectiveEraser

                    when (motionEvent.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            val pressure = if (isStylus) motionEvent.pressure.coerceIn(0.2f, 1.5f) else 0.8f
                            activeStrokePoints = listOf(StrokePoint(motionEvent.x, motionEvent.y, pressure))
                            true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val currentList = activeStrokePoints?.toMutableList() ?: mutableListOf()
                            val historySize = motionEvent.historySize
                            for (h in 0 until historySize) {
                                val hPressure = if (isStylus) motionEvent.getHistoricalPressure(0, h).coerceIn(0.2f, 1.5f) else 0.8f
                                currentList.add(
                                    StrokePoint(
                                        motionEvent.getHistoricalX(0, h),
                                        motionEvent.getHistoricalY(0, h),
                                        hPressure
                                    )
                                )
                            }
                            val p = if (isStylus) motionEvent.pressure.coerceIn(0.2f, 1.5f) else 0.8f
                            currentList.add(StrokePoint(motionEvent.x, motionEvent.y, p))
                            activeStrokePoints = currentList
                            true
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            activeStrokePoints?.let { pts ->
                                if (pts.isNotEmpty()) {
                                    val stroke = DrawingStroke(
                                        points = pts,
                                        colorArgb = if (effectiveEraser) DeepObsidian.toArgb().toLong() else currentColor.toArgb().toLong(),
                                        baseWidth = if (effectiveEraser) currentWidth * 3.5f else currentWidth,
                                        isEraser = effectiveEraser
                                    )
                                    strokes.add(stroke)
                                    onStrokesChanged()
                                }
                            }
                            activeStrokePoints = null
                            true
                        }

                        else -> false
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Render committed strokes
                for (stroke in strokes) {
                    renderSmoothStroke(stroke, DeepObsidian)
                }

                // Render current active stroke in progress
                activeStrokePoints?.let { pts ->
                    if (pts.isNotEmpty()) {
                        val activeStroke = DrawingStroke(
                            points = pts,
                            colorArgb = if (activeIsEraser) DeepObsidian.toArgb().toLong() else currentColor.toArgb().toLong(),
                            baseWidth = if (activeIsEraser) currentWidth * 3.5f else currentWidth,
                            isEraser = activeIsEraser
                        )
                        renderSmoothStroke(activeStroke, DeepObsidian)
                    }
                }
            }

            // S Pen indicator badge if side button is pressed or stylus detected
            if (stylusHardwareEraser) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VibrantPink.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("S Pen: Ластик", color = TextBrightWhite, fontSize = 11.sp)
                }
            }
        }
    }
}

/**
 * Renders static strokes without interaction (used for the 5-sec preview and final gallery)
 */
@Composable
fun RenderStrokesCanvas(
    strokes: List<DrawingStroke>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = DeepObsidian
) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
    ) {
        for (stroke in strokes) {
            renderSmoothStroke(stroke, backgroundColor)
        }
    }
}

/**
 * Smooth Bézier curve rendering with pressure modulation
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.renderSmoothStroke(
    stroke: DrawingStroke,
    canvasBg: Color
) {
    val points = stroke.points
    if (points.isEmpty()) return

    val strokeColor = if (stroke.isEraser) canvasBg else Color(stroke.colorArgb.toInt())

    if (points.size == 1) {
        val p = points[0]
        val radius = (stroke.baseWidth * p.pressure).coerceAtLeast(3f)
        drawCircle(
            color = strokeColor,
            radius = radius,
            center = androidx.compose.ui.geometry.Offset(p.x, p.y)
        )
        return
    }

    // Bézier curve smoothing
    val path = Path()
    path.moveTo(points[0].x, points[0].y)

    var avgPressure = 0f
    for (i in 0 until points.size - 1) {
        val pCurrent = points[i]
        val pNext = points[i + 1]
        avgPressure += pCurrent.pressure

        val midX = (pCurrent.x + pNext.x) / 2f
        val midY = (pCurrent.y + pNext.y) / 2f
        path.quadraticTo(pCurrent.x, pCurrent.y, midX, midY)
    }
    avgPressure = (avgPressure / points.size).coerceIn(0.3f, 1.8f)
    val last = points.last()
    path.lineTo(last.x, last.y)

    val effectiveWidth = (stroke.baseWidth * (0.5f + 0.8f * avgPressure)).coerceAtLeast(2f)

    drawPath(
        path = path,
        color = strokeColor,
        style = Stroke(
            width = effectiveWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}
