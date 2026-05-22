package com.example.synesthesia.presentation.screens.soniczen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synesthesia.presentation.theme.RoyalBlue
import kotlinx.coroutines.delay

@Composable
fun SonicZenScreen() {
    var playingTrack by remember { mutableStateOf<AudioTrack?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(isPlaying, playingTrack) {
        if (isPlaying && playingTrack != null) {
            while (progress < 1f) {
                delay(1000)
                progress += 0.01f
            }
            isPlaying = false
            progress = 0f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "SONIC ZONE",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                )
                Text(
                    "Frequencies that resonate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
            
            IconButton(
                onClick = { /* Trigger AI Spotify Recommendation */ },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Playlist", tint = RoyalBlue)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Active Track Player
        AnimatedVisibility(
            visible = playingTrack != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            playingTrack?.let { track ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .shadow(16.dp, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = track.color.copy(alpha = 0.9f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(track.emoji, fontSize = 40.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(track.title, color = Color.White, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                                Text("Now Resonating...", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { isPlaying = !isPlaying }) {
                                Icon(
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(audioTracks) { track ->
                ModernAudioCard(
                    track = track,
                    isActive = playingTrack == track,
                    onClick = {
                        if (playingTrack == track) {
                            isPlaying = !isPlaying
                        } else {
                            playingTrack = track
                            isPlaying = true
                            progress = 0f
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ModernAudioCard(track: AudioTrack, isActive: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(if (isActive) 12.dp else 4.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) track.color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(track.color.copy(alpha = 0.15f), Color.Transparent)
                        )
                    )
            )
            
            Row(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(76.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = track.color.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(track.emoji, fontSize = 32.sp)
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(track.title, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                    Text(track.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                
                Icon(
                    if (isActive) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = track.color,
                    modifier = Modifier.padding(8.dp).size(24.dp)
                )
            }
        }
    }
}

data class AudioTrack(
    val title: String,
    val description: String,
    val emoji: String,
    val color: Color
)

val audioTracks = listOf(
    AudioTrack("Deep Nebula", "Pure binaural focus", "🌌", Color(0xFF7C3AED)),
    AudioTrack("Solar Flare", "Vibrant energy boost", "☀️", Color(0xFFF97316)),
    AudioTrack("Moonlight Sonata", "Serene sleeping aid", "🌙", Color(0xFF60A5FA)),
    AudioTrack("Forest Echoes", "Earthly grounding", "🌿", Color(0xFF34D399))
)
