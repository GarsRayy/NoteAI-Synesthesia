package com.example.synesthesia.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun AuroraCanvas(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF0B0D17),
    accentColors: List<Color> = listOf(
        Color(0xFF4A90E2),
        Color(0xFF9013FE),
        Color(0xFF50E3C2)
    ),
    speed: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000 / speed.toInt().coerceAtLeast(1), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(color = baseColor)

        accentColors.forEachIndexed { index, color ->
            val xOffset = sin(time + index) * size.width * 0.2f
            val yOffset = sin(time * 0.5f + index) * size.height * 0.2f
            
            val center = Offset(
                x = size.width * (0.3f + index * 0.3f) + xOffset,
                y = size.height * (0.3f + (index % 2) * 0.4f) + yOffset
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.4f), Color.Transparent),
                    center = center,
                    radius = size.maxDimension * 0.6f
                ),
                center = center,
                radius = size.maxDimension * 0.6f
            )
        }
    }
}

@Composable
fun EmotionArtCanvas(
    emotion: String?,
    modifier: Modifier = Modifier
) {
    val palette = remember(emotion) {
        when (emotion?.lowercase()) {
            "joy" -> listOf(Color(0xFFFFD700), Color(0xFFFF8C00), Color(0xFFFF69B4))
            "calm" -> listOf(Color(0xFF00CED1), Color(0xFF48D1CC), Color(0xFFB0E0E6))
            "anger" -> listOf(Color(0xFFFF4500), Color(0xFFFF0000), Color(0xFF8B0000))
            "melancholy" -> listOf(Color(0xFF483D8B), Color(0xFF4B0082), Color(0xFF191970))
            else -> listOf(Color(0xFF4A90E2), Color(0xFF9013FE), Color(0xFF50E3C2))
        }
    }

    AuroraCanvas(
        modifier = modifier,
        accentColors = palette,
        speed = if (emotion == "anger") 2f else 1f
    )
}
