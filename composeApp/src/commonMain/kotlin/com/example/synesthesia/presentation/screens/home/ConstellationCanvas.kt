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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.synesthesia.domain.model.Note
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
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val twinkleAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Group notes by emotion for "Super Nodes"
    val groupedNotes = remember(notes) {
        notes.groupBy { it.emotion ?: "Unknown" }
    }

    // Positions of super nodes
    val nodePositions = remember(groupedNotes) {
        mutableMapOf<String, Offset>()
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
            .pointerInput(groupedNotes) {
                detectTapGestures { tapOffset ->
                    // Adjust tap offset by current transform
                    val adjustedTap = (tapOffset - offset) / scale
                    
                    // Check if any node was clicked
                    nodePositions.forEach { (emotion, pos) ->
                        val count = groupedNotes[emotion]?.size ?: 0
                        val nodeRadius = 40f + (count * 15f)
                        val dx = adjustedTap.x - pos.x
                        val dy = adjustedTap.y - pos.y
                        if (sqrt(dx * dx + dy * dy) <= nodeRadius) {
                            // If clicked, navigate to the first note of this emotion
                            // (Or we could show a list, but MIP says navigate to detail)
                            groupedNotes[emotion]?.firstOrNull()?.let { onNoteClick(it.id) }
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
            // Draw background "space dust" or distant stars
            repeat(50) { i ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.2f * twinkleAnim),
                    radius = 1f + (i % 3),
                    center = Offset(
                        x = (i * 12345f % size.width),
                        y = (i * 54321f % size.height)
                    )
                )
            }

            // Draw connecting lines (Constellation Lines)
            val positionsList = nodePositions.values.toList()
            if (positionsList.size > 1) {
                for (i in 0 until positionsList.size - 1) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.15f),
                        start = positionsList[i],
                        end = positionsList[i+1],
                        strokeWidth = 1.dp.toPx()
                    )
                }
                // Close the loop if many
                if (positionsList.size > 2) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.15f),
                        start = positionsList.last(),
                        end = positionsList.first(),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            groupedNotes.entries.forEachIndexed { index, entry ->
                val emotion = entry.key
                val noteList = entry.value
                val count = noteList.size
                
                // Calculate base position in a circle (larger radius for interest)
                val angle = (index.toFloat() / groupedNotes.size) * 2f * kotlin.math.PI.toFloat()
                val radius = 400f
                val baseX = centerX + cos(angle) * radius
                val baseY = centerY + sin(angle) * radius

                // Add floating effect
                val floatX = baseX + sin(floatAnim + index) * 30f
                val floatY = baseY + sin(floatAnim * 0.8f + index) * 30f
                
                val currentPos = Offset(floatX, floatY)
                nodePositions[emotion] = currentPos

                val starBaseRadius = 15f + (count * 5f)
                val color = parseHexColor(noteList.firstOrNull()?.artToken) ?: Color.Blue

                // 1. Draw Outer Glow (Large, soft)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.3f * twinkleAnim), Color.Transparent),
                        center = currentPos,
                        radius = starBaseRadius * 4f
                    ),
                    radius = starBaseRadius * 4f,
                    center = currentPos
                )

                // 2. Draw Secondary Glow (Medium)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.6f), Color.Transparent),
                        center = currentPos,
                        radius = starBaseRadius * 2f
                    ),
                    radius = starBaseRadius * 2f,
                    center = currentPos
                )

                // 3. Draw Core Star
                drawCircle(
                    color = Color.White,
                    radius = starBaseRadius * 0.6f,
                    center = currentPos
                )
                
                // 4. Draw Flare / Cross effect for larger stars
                if (count > 2) {
                    val flareLen = starBaseRadius * 3f
                    drawLine(
                        color = Color.White.copy(alpha = 0.8f * twinkleAnim),
                        start = Offset(currentPos.x - flareLen, currentPos.y),
                        end = Offset(currentPos.x + flareLen, currentPos.y),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.8f * twinkleAnim),
                        start = Offset(currentPos.x, currentPos.y - flareLen),
                        end = Offset(currentPos.x, currentPos.y + flareLen),
                        strokeWidth = 2f
                    )
                }
            }
        }
    }
}

private fun parseHexColor(hex: String?): Color? {
    if (hex == null || !hex.startsWith("#")) return null
    return try {
        Color(hex.removePrefix("#").toLong(16) or 0xFF000000)
    } catch (e: Exception) {
        null
    }
}
