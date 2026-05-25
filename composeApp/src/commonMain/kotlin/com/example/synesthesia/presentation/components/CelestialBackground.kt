package com.example.synesthesia.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import com.example.synesthesia.presentation.theme.DeepIndigo
import com.example.synesthesia.presentation.theme.SpaceBlack
import com.example.synesthesia.presentation.theme.StarWhite
import kotlin.math.sin
import kotlin.random.Random

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun CelestialBackground(
    isAstronomyMode: Boolean,
    content: @Composable BoxScope.() -> Unit
) {
    val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    
    val skyGradient = remember(hour) {
        when {
            hour in 5..8 -> listOf(Color(0xFFFF8A65), Color(0xFFFFE082)) // Sunrise
            hour in 17..20 -> listOf(Color(0xFFFFB74D), Color(0xFFEF9A9A)) // Golden Hour
            else -> listOf(Color(0xFF87CEEB), Color(0xFFF0F9FF)) // Daylight
        }
    }

    val bgColor = if (isAstronomyMode) SpaceBlack else skyGradient.last()
    val contentColor = if (isAstronomyMode) StarWhite else DeepIndigo

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor,
        contentColor = contentColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isAstronomyMode) {
                StarField()
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(skyGradient)))
                DaylightSky()
            }
            content()
        }
    }
}

@Composable
private fun DaylightSky() {
    val infiniteTransition = rememberInfiniteTransition()
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.85f
        val centerY = size.height * 0.15f

        // Sunrays
        translate(centerX, centerY) {
            rotate(sunRotation) {
                repeat(8) { i ->
                    rotate(i * 45f) {
                        drawRect(
                            color = Color(0xFFFEF08A).copy(alpha = 0.1f),
                            topLeft = Offset(-20f, 100f),
                            size = androidx.compose.ui.geometry.Size(40f, 1000f)
                        )
                    }
                }
            }
        }

        // Sun Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFEF08A).copy(alpha = 0.4f), Color.Transparent),
                center = Offset(centerX, centerY),
                radius = 350f * sunPulse
            ),
            radius = 350f * sunPulse,
            center = Offset(centerX, centerY)
        )
        
        // Clouds
        repeat(5) { i ->
            val cloudX = (size.width * (0.1f + i * 0.25f) + (sin(sunRotation * 0.05f + i) * 100f)) % size.width
            drawCircle(
                color = Color.White.copy(alpha = 0.35f),
                radius = 120f + (i * 20),
                center = Offset(cloudX, size.height * (0.2f + (i % 2) * 0.1f))
            )
        }
    }
}

@Composable
private fun StarField() {
    val infiniteTransition = rememberInfiniteTransition()
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val stars = remember {
        List(100) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2f + 1f,
                alpha = Random.nextFloat() * 0.7f + 0.3f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { star ->
            drawCircle(
                color = StarWhite.copy(alpha = star.alpha * twinkle),
                radius = star.size,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }
    }
}

private data class Star(val x: Float, val y: Float, val size: Float, val alpha: Float)
