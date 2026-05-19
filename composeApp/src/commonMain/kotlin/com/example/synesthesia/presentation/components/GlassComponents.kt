package com.example.synesthesia.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun GlassCardPreview() {
    AuroraBackground {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            GlassCard(
                modifier = Modifier.size(300.dp, 200.dp).padding(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Glass Card")
                }
            }
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 16.dp,
    cornerRadius: Dp = 24.dp,
    borderOpacity: Float = 0.15f,
    glowColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val baseColor = if (isDark) Color.White else Color.Black
    val backgroundOpacity = 0.05f

    Box(modifier = modifier) {
        // Blur Background Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(cornerRadius))
                .blur(blurRadius)
                .background(baseColor.copy(alpha = backgroundOpacity))
        )
        
        // Content and Border Layer (Sharp)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    if (glowColor != null) {
                        drawCircle(
                            color = glowColor.copy(alpha = 0.15f),
                            radius = size.maxDimension / 2,
                            center = center,
                            style = Stroke(width = 40.dp.toPx())
                        )
                    }
                }
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            baseColor.copy(alpha = borderOpacity),
                            baseColor.copy(alpha = borderOpacity * 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .padding(4.dp), // Small padding to keep content away from border
            content = content
        )
    }
}

@Composable
fun GlassShimmer(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val baseColor = if (isDark) Color.White else Color.Black

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(baseColor.copy(alpha = alpha))
            .border(
                width = 1.dp,
                color = baseColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .blur(16.dp)
    )
}

@Composable
fun GlowOrb(
    modifier: Modifier = Modifier,
    color: Color,
    size: Dp = 200.dp,
    blurRadius: Dp = 80.dp,
    durationMillis: Int = 5000
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateValue(
        initialValue = (-20).dp,
        targetValue = 20.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .offset(y = animatedOffset)
            .size(size)
            .background(color, shape = CircleShape)
            .blur(blurRadius)
    )
}

@Composable
fun AuroraBackground(
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    
    // Adjust colors based on theme for better contrast
    val color1 = if (isDark) Color(0xFF4A90E2).copy(alpha = 0.4f) else Color(0xFF4A90E2).copy(alpha = 0.15f)
    val color2 = if (isDark) Color(0xFF9013FE).copy(alpha = 0.3f) else Color(0xFF9013FE).copy(alpha = 0.12f)
    val color3 = if (isDark) Color(0xFF50E3C2).copy(alpha = 0.25f) else Color(0xFF50E3C2).copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Glow Orbs
        GlowOrb(
            modifier = Modifier.align(Alignment.TopStart).offset(x = (-50).dp, y = 100.dp),
            color = color1,
            size = 300.dp
        )
        GlowOrb(
            modifier = Modifier.align(Alignment.CenterEnd).offset(x = 50.dp, y = (-100).dp),
            color = color2,
            size = 350.dp
        )
        GlowOrb(
            modifier = Modifier.align(Alignment.BottomStart).offset(x = (-20).dp, y = 50.dp),
            color = color3,
            size = 250.dp
        )

        content()
    }
}
