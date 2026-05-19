package com.example.synesthesia.presentation.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.EmotionSystem
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ConstellationCanvas(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableStateOf(1f) }

    val infiniteTransition = rememberInfiniteTransition()
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * kotlin.math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val twinkleAnim by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Positions of individual note nodes
    val notePositions = remember(notes) {
        mutableMapOf<Long, Offset>()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset += pan
                }
            }
            .pointerInput(notes) {
                detectTapGestures { tapOffset ->
                    val adjustedTap = (tapOffset - offset) / scale
                    notePositions.forEach { (id, pos) ->
                        val dx = adjustedTap.x - pos.x
                        val dy = adjustedTap.y - pos.y
                        if (sqrt(dx * dx + dy * dy) <= 40f) {
                            onNoteClick(id)
                        }
                    }
                }
            }
    ) {
        val centerX = constraints.maxWidth / 2f
        val centerY = constraints.maxHeight / 2f

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        ) {
            // Background stars
            repeat(100) { i ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.15f * twinkleAnim),
                    radius = 1f + (i % 2),
                    center = Offset(
                        x = (i * 777f % size.width),
                        y = (i * 333f % size.height)
                    )
                )
            }

            // Define Hub positions (Diamond layout)
            val hubDistance = 800f
            val hubs = EmotionSystem.categories.mapIndexed { index, category ->
                val angle = (index.toFloat() / EmotionSystem.categories.size) * 2f * kotlin.math.PI.toFloat()
                val hubPos = Offset(
                    centerX + cos(angle) * hubDistance,
                    centerY + sin(angle) * hubDistance
                )
                category to hubPos
            }

            // Draw Hubs and Notes
            hubs.forEachIndexed { hubIndex, (category, hubBasePos) ->
                val hubColor = parseHexColor(category.color)
                val hubCurrentPos = Offset(
                    hubBasePos.x + sin(floatAnim + hubIndex) * 15f,
                    hubBasePos.y + cos(floatAnim * 0.5f + hubIndex) * 15f
                )

                // 1. Draw Hub Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(hubColor.copy(alpha = 0.4f), Color.Transparent),
                        center = hubCurrentPos,
                        radius = 150f
                    ),
                    radius = 150f,
                    center = hubCurrentPos
                )

                // 2. Draw Hub Core
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = 20f,
                    center = hubCurrentPos
                )
                drawCircle(
                    color = hubColor,
                    radius = 20f,
                    center = hubCurrentPos,
                    style = Stroke(width = 4f)
                )

                // 3. Draw Notes belonging to this Hub
                // Notes are grouped by their main category name stored in artToken
                val notesInCategory = notes.filter { it.artToken == category.name }
                notesInCategory.forEachIndexed { noteIndex, note ->
                    val noteAngle = (noteIndex.toFloat() / (notesInCategory.size.coerceAtLeast(1))) * 2f * kotlin.math.PI.toFloat()
                    val noteRadius = 200f + (noteIndex * 20f % 100f)
                    val noteBasePos = Offset(
                        hubCurrentPos.x + cos(noteAngle) * noteRadius,
                        hubCurrentPos.y + sin(noteAngle) * noteRadius
                    )
                    
                    val noteCurrentPos = Offset(
                        noteBasePos.x + sin(floatAnim * 1.2f + noteIndex) * 10f,
                        noteBasePos.y + cos(floatAnim * 0.8f + noteIndex) * 10f
                    )
                    notePositions[note.id] = noteCurrentPos

                    // Draw connection to Hub
                    drawLine(
                        color = hubColor.copy(alpha = 0.3f),
                        start = hubCurrentPos,
                        end = noteCurrentPos,
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw Note Dot
                    drawCircle(
                        color = hubColor,
                        radius = 8f,
                        center = noteCurrentPos
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.6f * twinkleAnim),
                        radius = 12f,
                        center = noteCurrentPos,
                        style = Stroke(width = 2f)
                    )
                }
            }
        }
    }
}

private fun parseHexColor(hex: String?): Color {
    if (hex == null || !hex.startsWith("#")) return Color.Gray
    return try {
        Color(hex.removePrefix("#").toLong(16) or 0xFF000000)
    } catch (e: Exception) {
        Color.Gray
    }
}
