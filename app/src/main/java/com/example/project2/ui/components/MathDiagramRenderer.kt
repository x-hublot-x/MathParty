package com.example.project2.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.project2.data.model.MathVisualDiagram
import com.example.project2.ui.theme.GlassBorder
import com.example.project2.ui.theme.PureOnyx
import com.example.project2.ui.theme.PureWhite
import com.example.project2.ui.theme.SilverMuted
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MathDiagramCard(
    diagram: MathVisualDiagram,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(PureOnyx)
            .border(1.5.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Source Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x22FFFFFF))
                .border(0.8.dp, Color(0x44FFFFFF), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = diagram.source.uppercase(),
                color = PureWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = diagram.title,
            color = PureWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = diagram.caption,
            color = SilverMuted,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Visual Display (Internet Image with Canvas Fallback)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F1014))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (!diagram.imageUrl.isNullOrBlank()) {
                val context = LocalContext.current
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(diagram.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = diagram.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = PureWhite,
                                    strokeWidth = 2.5.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Загрузка изображения из сети...",
                                    color = SilverMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    },
                    error = {
                        VectorFallbackCanvas(diagram)
                    }
                )
            } else {
                VectorFallbackCanvas(diagram)
            }
        }
    }
}

@Composable
private fun VectorFallbackCanvas(diagram: MathVisualDiagram) {
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val strokeColor = PureWhite
        val accentColor = Color(0x99FFFFFF)

        when (diagram.diagramType) {
            "TORUS" -> drawTorusDiagram(strokeColor, accentColor)
            "KLEIN_BOTTLE" -> drawKleinBottleDiagram(strokeColor, accentColor)
            "TESSERACT" -> drawTesseractDiagram(strokeColor, accentColor)
            "MOBIUS" -> drawMobiusDiagram(strokeColor, accentColor)
            "DRAGON" -> drawDragonDiagram(strokeColor, accentColor)
            "GAUSSIAN_SNAKE" -> drawGaussianSnakeDiagram(strokeColor, accentColor)
            "INTEGRAL_HOOK" -> drawIntegralHookDiagram(strokeColor, accentColor)
            "PENROSE_TRIANGLE" -> drawPenroseTriangle(strokeColor, accentColor)
            "GOLDEN_SPIRAL" -> drawGoldenSpiral(strokeColor, accentColor)
            "TREFOIL_KNOT" -> drawTrefoilKnot(strokeColor, accentColor)
            else -> drawEigenvectorDiagram(strokeColor, accentColor)
        }
    }
}

private fun DrawScope.drawTorusDiagram(mainColor: Color, subColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Outer ellipse of torus
    drawOval(
        color = mainColor,
        topLeft = Offset(cx - 130f, cy - 65f),
        size = Size(260f, 130f),
        style = Stroke(width = 4f)
    )

    // Inner hole of torus
    drawOval(
        color = mainColor,
        topLeft = Offset(cx - 55f, cy - 25f),
        size = Size(110f, 50f),
        style = Stroke(width = 3.5f)
    )

    // Intersecting Hyperplane (tilted transparent slice)
    val plane = Path().apply {
        moveTo(cx - 160f, cy + 20f)
        lineTo(cx + 80f, cy - 90f)
        lineTo(cx + 170f, cy - 50f)
        lineTo(cx - 70f, cy + 60f)
        close()
    }
    drawPath(path = plane, color = subColor, style = Stroke(width = 2.5f))

    // Cross-section circles
    drawArc(
        color = mainColor,
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = Offset(cx - 105f, cy - 35f),
        size = Size(40f, 70f),
        style = Stroke(width = 2.5f)
    )
}

private fun DrawScope.drawKleinBottleDiagram(mainColor: Color, subColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Base bulb
    drawOval(
        color = mainColor,
        topLeft = Offset(cx - 70f, cy - 10f),
        size = Size(140f, 95f),
        style = Stroke(width = 4f)
    )

    // Neck looping into bottom
    val neckPath = Path().apply {
        moveTo(cx - 20f, cy - 10f)
        cubicTo(cx - 30f, cy - 80f, cx + 50f, cy - 85f, cx + 50f, cy - 30f)
        cubicTo(cx + 50f, cy + 20f, cx + 10f, cy + 40f, cx + 10f, cy + 70f)
    }
    drawPath(path = neckPath, color = mainColor, style = Stroke(width = 4f, cap = StrokeCap.Round))

    // Intersection wall highlight
    drawCircle(color = subColor, radius = 18f, center = Offset(cx + 35f, cy + 15f), style = Stroke(width = 2.5f))
}

private fun DrawScope.drawTesseractDiagram(mainColor: Color, subColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    val outerSize = 130f
    val innerSize = 65f

    // Outer cube square
    val oLeft = cx - outerSize / 2f
    val oTop = cy - outerSize / 2f
    drawRect(color = mainColor, topLeft = Offset(oLeft, oTop), size = Size(outerSize, outerSize), style = Stroke(width = 3.5f))

    // Inner cube square
    val iLeft = cx - innerSize / 2f
    val iTop = cy - innerSize / 2f
    drawRect(color = subColor, topLeft = Offset(iLeft, iTop), size = Size(innerSize, innerSize), style = Stroke(width = 2.5f))

    // Connecting 4D projection rays
    drawLine(color = mainColor, start = Offset(oLeft, oTop), end = Offset(iLeft, iTop), strokeWidth = 2.5f)
    drawLine(color = mainColor, start = Offset(oLeft + outerSize, oTop), end = Offset(iLeft + innerSize, iTop), strokeWidth = 2.5f)
    drawLine(color = mainColor, start = Offset(oLeft, oTop + outerSize), end = Offset(iLeft, iTop + innerSize), strokeWidth = 2.5f)
    drawLine(color = mainColor, start = Offset(oLeft + outerSize, oTop + outerSize), end = Offset(iLeft + innerSize, iTop + innerSize), strokeWidth = 2.5f)
}

private fun DrawScope.drawMobiusDiagram(mainColor: Color, subColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Figure-8 twist path
    val path = Path().apply {
        moveTo(cx - 90f, cy)
        cubicTo(cx - 90f, cy - 60f, cx - 15f, cy - 60f, cx, cy)
        cubicTo(cx + 15f, cy + 60f, cx + 90f, cy + 60f, cx + 90f, cy)
        cubicTo(cx + 90f, cy - 60f, cx + 15f, cy - 60f, cx, cy)
        cubicTo(cx - 15f, cy + 60f, cx - 90f, cy + 60f, cx - 90f, cy)
    }
    drawPath(path = path, color = mainColor, style = Stroke(width = 4f, cap = StrokeCap.Round))

    // Orientation arrow
    drawLine(color = subColor, start = Offset(cx + 80f, cy - 5f), end = Offset(cx + 95f, cy + 10f), strokeWidth = 3f)
    drawLine(color = subColor, start = Offset(cx + 95f, cy + 10f), end = Offset(cx + 78f, cy + 20f), strokeWidth = 3f)
}

private fun DrawScope.drawDragonDiagram(mainColor: Color, subColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    val dragonPath = Path().apply {
        moveTo(cx - 70f, cy + 40f)
        lineTo(cx - 70f, cy - 30f)
        lineTo(cx, cy - 30f)
        lineTo(cx, cy + 20f)
        lineTo(cx + 40f, cy + 20f)
        lineTo(cx + 40f, cy - 40f)
        lineTo(cx + 80f, cy - 40f)
        lineTo(cx + 80f, cy + 50f)
    }
    drawPath(path = dragonPath, color = mainColor, style = Stroke(width = 4f, cap = StrokeCap.Square))

    // Geometric grid dots
    for (i in -2..2) {
        for (j in -2..2) {
            drawCircle(color = subColor, radius = 2f, center = Offset(cx + i * 35f, cy + j * 30f))
        }
    }
}

private fun DrawScope.drawGaussianSnakeDiagram(mainColor: Color, subColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Coordinate axis
    drawLine(color = subColor, start = Offset(cx - 120f, cy + 50f), end = Offset(cx + 120f, cy + 50f), strokeWidth = 2f)

    // Gaussian bell curve
    val bellPath = Path().apply {
        moveTo(cx - 110f, cy + 48f)
        cubicTo(cx - 60f, cy + 48f, cx - 40f, cy - 65f, cx, cy - 65f)
        cubicTo(cx + 40f, cy - 65f, cx + 60f, cy + 48f, cx + 110f, cy + 48f)
    }
    drawPath(path = bellPath, color = mainColor, style = Stroke(width = 4f, cap = StrokeCap.Round))

    // Funny snake eyes at top
    drawCircle(color = mainColor, radius = 5f, center = Offset(cx - 8f, cy - 75f))
    drawCircle(color = mainColor, radius = 5f, center = Offset(cx + 8f, cy - 75f))
}

private fun DrawScope.drawIntegralHookDiagram(mainColor: Color, subColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Big graceful integral sign
    val integralPath = Path().apply {
        moveTo(cx - 20f, cy - 75f)
        cubicTo(cx + 25f, cy - 85f, cx + 25f, cy - 45f, cx, cy)
        cubicTo(cx - 25f, cy + 45f, cx - 25f, cy + 85f, cx + 20f, cy + 75f)
    }
    drawPath(path = integralPath, color = mainColor, style = Stroke(width = 5f, cap = StrokeCap.Round))

    // Fishing line from top to a singularity point
    drawLine(color = subColor, start = Offset(cx + 20f, cy - 75f), end = Offset(cx + 70f, cy + 20f), strokeWidth = 2f)
    drawCircle(color = mainColor, radius = 7f, center = Offset(cx + 70f, cy + 20f))
}

private fun DrawScope.drawPenroseTriangle(mainColor: Color, subColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    val path = Path().apply {
        moveTo(cx, cy - 70f)
        lineTo(cx + 70f, cy + 50f)
        lineTo(cx - 70f, cy + 50f)
        close()
    }
    drawPath(path = path, color = mainColor, style = Stroke(width = 5f, cap = StrokeCap.Round))

    val inner = Path().apply {
        moveTo(cx, cy - 35f)
        lineTo(cx + 40f, cy + 35f)
        lineTo(cx - 40f, cy + 35f)
        close()
    }
    drawPath(path = inner, color = subColor, style = Stroke(width = 3f))
}

private fun DrawScope.drawGoldenSpiral(mainColor: Color, subColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Fibonacci rects outline
    drawRect(color = subColor, topLeft = Offset(cx - 80f, cy - 50f), size = Size(160f, 100f), style = Stroke(width = 2f))

    // Spiral curve
    val spiral = Path().apply {
        moveTo(cx - 80f, cy + 50f)
        cubicTo(cx - 80f, cy - 50f, cx + 80f, cy - 50f, cx + 80f, cy + 20f)
        cubicTo(cx + 80f, cy + 50f, cx + 20f, cy + 50f, cx + 20f, cy)
    }
    drawPath(path = spiral, color = mainColor, style = Stroke(width = 4f, cap = StrokeCap.Round))
}

private fun DrawScope.drawTrefoilKnot(mainColor: Color, subColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    for (i in 0..2) {
        val angle = Math.toRadians((i * 120.0) - 90.0)
        val loopX = cx + (cos(angle) * 35.0).toFloat()
        val loopY = cy + (sin(angle) * 35.0).toFloat()
        drawCircle(color = mainColor, radius = 30f, center = Offset(loopX, loopY), style = Stroke(width = 4f))
    }
    drawCircle(color = subColor, radius = 12f, center = Offset(cx, cy), style = Stroke(width = 2f))
}

private fun DrawScope.drawEigenvectorDiagram(mainColor: Color, subColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Axes
    drawLine(color = subColor, start = Offset(cx - 100f, cy), end = Offset(cx + 100f, cy), strokeWidth = 2f)
    drawLine(color = subColor, start = Offset(cx, cy + 80f), end = Offset(cx, cy - 80f), strokeWidth = 2f)

    // Tilted ellipse
    drawOval(
        color = subColor,
        topLeft = Offset(cx - 65f, cy - 45f),
        size = Size(130f, 90f),
        style = Stroke(width = 2.5f)
    )

    // Eigenvector arrow
    val vecEnd = Offset(cx + 75f, cy - 55f)
    drawLine(color = mainColor, start = Offset(cx, cy), end = vecEnd, strokeWidth = 4f, cap = StrokeCap.Round)
    drawCircle(color = mainColor, radius = 5f, center = vecEnd)
}
