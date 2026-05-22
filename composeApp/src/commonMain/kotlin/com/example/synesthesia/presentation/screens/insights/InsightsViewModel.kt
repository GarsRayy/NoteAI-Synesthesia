package com.example.synesthesia.presentation.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synesthesia.data.local.datastore.UserPreferences
import com.example.synesthesia.domain.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

class InsightsViewModel(
    private val repository: NoteRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isEditingProfile = MutableStateFlow(false)
    val isEditingProfile = _isEditingProfile.asStateFlow()

    val userName = userPreferences.userName.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Stargazer")
    val userBio = userPreferences.userBio.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val userPhotoUri = userPreferences.userPhotoUri.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val uiState: StateFlow<InsightsUiState> = repository.getAllNotes()
        .map { notes ->
            val total = notes.size
            if (total == 0) return@map InsightsUiState.Empty

            val distribution = notes.groupBy { it.emotion?.lowercase() ?: "calm" }
                .mapValues { (it.value.size.toFloat() / total * 100).toInt() }

            // Simplified weekly trend (last 7 days)
            val now = Clock.System.now()
            val tz = TimeZone.currentSystemDefault()
            val last7Days = (0..6).map { i ->
                val date = now.minus(i, DateTimeUnit.DAY, tz)
                // Count notes for this date (simplified)
                notes.count { it.createdAt.toString().split("T")[0] == date.toString().split("T")[0] }.toFloat()
            }.reversed()

            InsightsUiState.Success(
                totalMemories = total,
                emotionDistribution = distribution,
                weeklyTrend = last7Days
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightsUiState.Loading)

    fun toggleEditProfile() {
        _isEditingProfile.update { !it }
    }

    fun updateProfile(name: String, bio: String, photoUri: String?) {
        viewModelScope.launch {
            userPreferences.updateProfile(name, bio, photoUri)
            _isEditingProfile.value = false
        }
    }
}

sealed interface InsightsUiState {
    data object Loading : InsightsUiState
    data object Empty : InsightsUiState
    data class Success(
        val totalMemories: Int,
        val emotionDistribution: Map<String, Int>,
        val weeklyTrend: List<Float>
    ) : InsightsUiState
}
