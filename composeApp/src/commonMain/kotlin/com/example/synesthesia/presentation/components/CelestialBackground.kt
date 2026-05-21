package com.example.synesthesia.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.synesthesia.presentation.theme.SpaceBlack
import com.example.synesthesia.presentation.theme.StarWhite
import kotlin.random.Random

@Composable
fun CelestialBackground(
    isAstronomyMode: Boolean,
    content: @Composable BoxScope.() -> Unit
) {
    val bgColor = if (isAstronomyMode) SpaceBlack else Color(0xFFF0F9FF) // Sky Blue for Light Mode
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        if (isAstronomyMode) {
            StarField()
        } else {
            DaylightSky()
        }
        content()
    }
}

@Composable
private fun DaylightSky() {
    val infiniteTransition = rememberInfiniteTransition()
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Soft Sun Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFEF08A).copy(alpha = 0.3f), Color.Transparent),
                center = Offset(size.width * 0.8f, size.height * 0.15f),
                radius = 400f * sunPulse
            ),
            radius = 400f * sunPulse,
            center = Offset(size.width * 0.8f, size.height * 0.15f)
        )
        
        // Subtle Clouds (drawn as soft ovals)
        repeat(3) { i ->
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = 150f,
                center = Offset(
                    size.width * (0.2f + i * 0.3f),
                    size.height * (0.1f + i * 0.05f)
                )
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
