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

    // Group notes by emotion and sort for stability
    val groupedNotes = remember(notes) {
        notes.groupBy { it.emotion ?: "Unknown" }.toList().sortedBy { it.first }
    }

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
                    // Adjust tap offset by current transform
                    val adjustedTap = (tapOffset - offset) / scale
                    
                    // Check if any note was clicked
                    notePositions.forEach { (id, pos) ->
                        val nodeRadius = 40f 
                        val dx = adjustedTap.x - pos.x
                        val dy = adjustedTap.y - pos.y
                        if (sqrt(dx * dx + dy * dy) <= nodeRadius) {
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

            val groupPositions = mutableListOf<Offset>()

            // First pass: calculate all positions and draw intra-group lines
            groupedNotes.forEachIndexed { groupIndex, entry ->
                val notesInGroup = entry.second.sortedBy { it.id }
                
                // Calculate base position for the group
                val groupAngle = (groupIndex.toFloat() / groupedNotes.size) * 2f * kotlin.math.PI.toFloat()
                val groupRadius = 500f
                val groupBaseX = centerX + cos(groupAngle) * groupRadius
                val groupBaseY = centerY + sin(groupAngle) * groupRadius

                val groupCurrentCenter = Offset(
                    groupBaseX + sin(floatAnim + groupIndex) * 20f,
                    groupBaseY + cos(floatAnim * 0.7f + groupIndex) * 20f
                )
                groupPositions.add(groupCurrentCenter)

                // Calculate and draw individual note positions and lines within group
                val clusterRadius = 120f
                var prevNotePos: Offset? = null
                
                notesInGroup.forEachIndexed { noteIndex, note ->
                    val noteAngle = (noteIndex.toFloat() / notesInGroup.size) * 2f * kotlin.math.PI.toFloat()
                    val notePos = Offset(
                        groupCurrentCenter.x + cos(noteAngle) * clusterRadius,
                        groupCurrentCenter.y + sin(noteAngle) * clusterRadius
                    )
                    notePositions[note.id] = notePos

                    // Draw line to previous note in group
                    prevNotePos?.let {
                        drawLine(
                            color = Color.White.copy(alpha = 0.2f),
                            start = it,
                            end = notePos,
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    prevNotePos = notePos
                }
                
                // Close the loop within the group if more than 2 notes
                if (notesInGroup.size > 2) {
                    val firstPos = notePositions[notesInGroup.first().id]
                    val lastPos = notePositions[notesInGroup.last().id]
                    if (firstPos != null && lastPos != null) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.2f),
                            start = lastPos,
                            end = firstPos,
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
            }

            // Draw inter-group lines (connect centers of groups)
            if (groupPositions.size > 1) {
                for (i in 0 until groupPositions.size - 1) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.1f),
                        start = groupPositions[i],
                        end = groupPositions[i+1],
                        strokeWidth = 0.5.dp.toPx()
                    )
                }
                if (groupPositions.size > 2) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.1f),
                        start = groupPositions.last(),
                        end = groupPositions.first(),
                        strokeWidth = 0.5.dp.toPx()
                    )
                }
            }

            // Second pass: Draw the notes themselves
            notes.forEach { note ->
                val currentPos = notePositions[note.id] ?: return@forEach
                val starBaseRadius = 15f
                val color = parseHexColor(note.artToken) ?: Color.Blue

                // 1. Draw Outer Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.3f * twinkleAnim), Color.Transparent),
                        center = currentPos,
                        radius = starBaseRadius * 4f
                    ),
                    radius = starBaseRadius * 4f,
                    center = currentPos
                )

                // 2. Draw Core Star
                drawCircle(
                    color = Color.White,
                    radius = starBaseRadius * 0.6f,
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
