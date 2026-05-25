package com.example.synesthesia.presentation.screens.sanctuary

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synesthesia.domain.model.EmotionSystem
import com.example.synesthesia.domain.repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

class SanctuaryViewModel(
    private val repository: NoteRepository
) : ViewModel() {
    
    private val tz = TimeZone.currentSystemDefault()

    // Ambil emosi paling dominan dari 3 hari terakhir
    val moodRecommendation: StateFlow<MoodRecommendation?> = repository.getAllNotes()
        .map { notes ->
            val recent = notes.filter { 
                it.createdAt > Clock.System.now().minus(3, DateTimeUnit.DAY, tz) 
            }
            if (recent.isEmpty()) return@map null
            
            val dominant = recent.groupBy { it.emotion }
                .maxByOrNull { it.value.size }?.key
            
            val categoryId = EmotionSystem.getCategoryBySubEmotion(dominant)?.id
            
            when (categoryId) {
                "HEU" -> MoodRecommendation(
                    title = "AI Mendeteksi Energi Tinggi",
                    message = "Emosi kamu cukup intens akhir-akhir ini. Kami rekomendasikan sesi pernapasan atau meditasi untuk menenangkan pikiran.",
                    suggestedRituals = listOf("breathing", "meditation"),
                    accentColor = Color(0xFFF97316)
                )
                "LEU" -> MoodRecommendation(
                    title = "AI Mendeteksi Energi Rendah",
                    message = "Kamu mungkin butuh sedikit dorongan energi. Coba Energy Boost atau Gratitude ritual untuk mengangkat semangatmu.",
                    suggestedRituals = listOf("energy", "gratitude"),
                    accentColor = Color(0xFF60A5FA)
                )
                else -> MoodRecommendation(
                    title = "Kondisi Stabil",
                    message = "Energimu terjaga dengan baik. Pertahankan dengan Daily Breathing untuk menjaga keseimbangan.",
                    suggestedRituals = listOf("breathing", "focus"),
                    accentColor = Color(0xFF34D399)
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}

data class MoodRecommendation(
    val title: String,
    val message: String,
    val suggestedRituals: List<String>,
    val accentColor: Color
)
