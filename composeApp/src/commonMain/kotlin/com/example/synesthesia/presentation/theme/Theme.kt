package com.example.synesthesia.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ==================== COLORS (MASTER PLAN) ====================

val RoyalBlue = Color(0xFF0235AC)
val BrightYellow = Color(0xFFF3E21B)
val DeepIndigo = Color(0xFF01153B)
val CrispWhite = Color(0xFFFBFBFB)

// Emotion Tokens
val JoyColor = Color(0xFFF4A44A)
val MelancholyColor = Color(0xFF3B82C4)
val CalmColor = Color(0xFF2EC9A0)
val AngerColor = Color(0xFFE05FA0)
val ReflectiveColor = Color(0xFF7B5EA7)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9E2FF),
    onPrimaryContainer = RoyalBlue,
    secondary = BrightYellow,
    onSecondary = Color.Black,
    background = CrispWhite,
    onBackground = DeepIndigo,
    surface = Color.White,
    onSurface = DeepIndigo,
    error = Color(0xFFBA1A1A),
    outline = Color(0xFF74777F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFADC6FF),
    onPrimary = RoyalBlue,
    primaryContainer = Color(0xFF004494),
    onPrimaryContainer = Color(0xFFD9E2FF),
    secondary = BrightYellow,
    onSecondary = Color.Black,
    background = DeepIndigo,
    onBackground = CrispWhite,
    surface = DeepIndigo,
    onSurface = CrispWhite,
    error = Color(0xFFFFB4AB),
    outline = Color(0xFF8E9099)
)

// ==================== TYPOGRAPHY ====================

private val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    )
)

@Composable
fun NoteAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
