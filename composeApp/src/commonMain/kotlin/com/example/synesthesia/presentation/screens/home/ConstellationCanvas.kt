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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
            groupedNotes.entries.forEachIndexed { index, entry ->
                val emotion = entry.key
                val noteList = entry.value
                val count = noteList.size
                
                // Calculate base position in a circle
                val angle = (index.toFloat() / groupedNotes.size) * 2f * kotlin.math.PI.toFloat()
                val radius = 300f
                val baseX = centerX + cos(angle) * radius
                val baseY = centerY + sin(angle) * radius

                // Add floating effect
                val floatX = baseX + sin(floatAnim + index) * 20f
                val floatY = baseY + sin(floatAnim * 0.8f + index) * 20f
                
                val currentPos = Offset(floatX, floatY)
                nodePositions[emotion] = currentPos

                val nodeRadius = 40f + (count * 15f)
                val color = parseHexColor(noteList.firstOrNull()?.artToken) ?: Color.Blue

                drawCircle(
                    color = color.copy(alpha = 0.6f),
                    radius = nodeRadius,
                    center = currentPos
                )
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
