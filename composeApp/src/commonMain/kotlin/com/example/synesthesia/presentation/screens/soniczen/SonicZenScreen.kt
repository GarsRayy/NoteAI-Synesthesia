package com.example.synesthesia.presentation.screens.soniczen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            
            // AI Playlist Trigger
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
        
        // Featured Spotify AI Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1DB954)) // Spotify Green
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Black.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("AI Personalized Playlist", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Based on your current galaxy mood", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GET", color = Color(0xFF1DB954), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(audioTracks) { track ->
                ModernAudioCard(track)
            }
        }
    }
}

@Composable
fun ModernAudioCard(track: AudioTrack) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clickable { },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradient Overlay for "Beyond Expectation" feel
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("432Hz • 15:00", style = MaterialTheme.typography.labelSmall, color = RoyalBlue, fontWeight = FontWeight.Bold)
                }
                
                Surface(
                    onClick = {},
                    shape = CircleShape,
                    color = RoyalBlue.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow, 
                        contentDescription = "Play", 
                        tint = RoyalBlue, 
                        modifier = Modifier.padding(8.dp)
                    )
                }
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
