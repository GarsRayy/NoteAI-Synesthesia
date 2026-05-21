package com.example.synesthesia.presentation.screens.sanctuary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synesthesia.presentation.theme.CalmColor

@Composable
fun SanctuaryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "SANCTUARY",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        )
        Text(
            "Guided rituals to calm your universe",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Featured Ritual (3D Card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .shadow(16.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CalmColor)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Text("Daily Breathing", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    Text("5 minutes of clarity", color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("START", color = CalmColor, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    "🧘",
                    fontSize = 80.sp,
                    modifier = Modifier.align(Alignment.CenterEnd).offset(x = 20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("RITUAL CATEGORIES", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(rituals) { ritual ->
                RitualCard(ritual)
            }
        }
    }
}

@Composable
fun RitualCard(ritual: Ritual) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clickable { },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(ritual.emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(ritual.name, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

data class Ritual(val name: String, val emoji: String)

val rituals = listOf(
    Ritual("Meditation", "🧘"),
    Ritual("Sleep Well", "💤"),
    Ritual("Focus Flow", "🎯"),
    Ritual("Gratitude", "🙏"),
    Ritual("Anxiety Relief", "🍃"),
    Ritual("Energy Boost", "⚡")
)
