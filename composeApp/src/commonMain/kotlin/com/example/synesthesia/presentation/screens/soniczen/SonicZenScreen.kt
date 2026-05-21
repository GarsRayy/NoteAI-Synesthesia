package com.example.synesthesia.presentation.screens.soniczen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synesthesia.presentation.theme.RoyalBlue

@Composable
fun SonicZenScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "SONIC ZONE",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        )
        Text(
            "Immersive audio frequencies for your soul",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(audioTracks) { track ->
                AudioTrackCard(track)
            }
        }
    }
}

@Composable
fun AudioTrackCard(track: AudioTrack) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clickable { /* Play track */ },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = track.color.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(track.emoji, fontSize = 24.sp)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(track.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            
            IconButton(onClick = { /* Play */ }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = RoyalBlue)
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
    AudioTrack("Deep Nebula", "432Hz Binaural Beats for focus", "🌌", Color(0xFF7C3AED)),
    AudioTrack("Solar Flare", "High energy frequencies", "☀️", Color(0xFFF97316)),
    AudioTrack("Moonlight Sonata", "Serene piano & white noise", "🌙", Color(0xFF60A5FA)),
    AudioTrack("Forest Echoes", "Earthly grounding sounds", "🌿", Color(0xFF34D399)),
    AudioTrack("Stardust Rain", "Gentle ASMR for sleeping", "✨", Color(0xFFFFD700))
)
