package com.example.synesthesia.presentation.screens.addnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import com.example.synesthesia.domain.model.EmotionSystem
import com.example.synesthesia.domain.model.EmotionCategory
import com.example.synesthesia.domain.repository.NoteRepository
import com.example.synesthesia.domain.usecase.SaveNoteUseCase
import com.example.synesthesia.domain.usecase.AnalyzeEmotionUseCase
import com.example.synesthesia.data.remote.api.GeminiService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class AddNoteViewModel(
    private val repository: NoteRepository,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val analyzeEmotionUseCase: AnalyzeEmotionUseCase,
    private val geminiService: GeminiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddNoteUiState())
    val uiState: StateFlow<AddNoteUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<AddNoteEvent>()
    val events: SharedFlow<AddNoteEvent> = _events.asSharedFlow()
    
    private var currentNoteId: Long? = null
    
    fun loadNote(noteId: Long) {
        currentNoteId = noteId
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            repository.getNoteById(noteId).collect { note ->
                note?.let {
                    _uiState.update { state ->
                        state.copy(
                            title = note.title,
                            content = note.content,
                            category = note.category,
                            color = note.color,
                            emotion = note.emotion,
                            artToken = note.artToken,
                            aiResonance = note.aiResonance,
                            isLoading = false,
                            isEditMode = true,
                            createdAt = note.createdAt,
                            selectedMainCategory = EmotionSystem.categories.find { it.name == note.artToken },
                            selectedSubEmotion = note.emotion
                        )
                    }
                }
            }
        }
    }
    
    // ==================== USER ACTIONS ====================
    
    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }
    
    fun onContentChange(content: String) {
        _uiState.update { it.copy(content = content) }
    }
    
    fun onMainCategorySelected(category: EmotionCategory?) {
        _uiState.update { it.copy(
            selectedMainCategory = category,
            selectedSubEmotion = null,
            artToken = category?.name // Using artToken to store main category name
        ) }
    }
    
    fun onSubEmotionSelected(subEmotion: String?) {
        _uiState.update { it.copy(
            selectedSubEmotion = subEmotion,
            emotion = subEmotion
        ) }
    }

    fun analyzeEmotion() {
        val state = _uiState.value
        if (state.title.isBlank() && state.content.isBlank()) return

        _uiState.update { it.copy(isAnalyzing = true) }

        viewModelScope.launch {
            analyzeEmotionUseCase(state.title, state.content)
                .onSuccess { response ->
                    val mainCategory = EmotionSystem.categories.find { it.id == response.mainCategoryId }
                    _uiState.update { state ->
                        state.copy(
                            selectedMainCategory = mainCategory,
                            selectedSubEmotion = response.subEmotion,
                            emotion = response.subEmotion,
                            artToken = mainCategory?.name,
                            aiResonance = response.aiResonance,
                            isAnalyzing = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isAnalyzing = false) }
                    _events.emit(AddNoteEvent.Error(error.message ?: "Gagal menganalisis emosi"))
                }
        }
    }
    
    fun saveNote() {
        val state = _uiState.value
        
        if (state.title.isBlank() && state.content.isBlank()) {
            _uiState.update { it.copy(titleError = "Judul atau konten harus diisi") }
            return
        }

        if (state.selectedMainCategory == null || state.selectedSubEmotion == null) {
            viewModelScope.launch {
                _events.emit(AddNoteEvent.Error("Pilih emosi terlebih dahulu"))
            }
            return
        }
        
        _uiState.update { it.copy(isSaving = true) }
        
        viewModelScope.launch {
            val note = Note(
                id = currentNoteId ?: 0,
                title = state.title.trim(),
                content = state.content.trim(),
                category = state.category,
                color = state.color,
                emotion = state.selectedSubEmotion,
                artToken = state.selectedMainCategory.name, // Storing category name
                aiResonance = state.aiResonance,
                createdAt = if (currentNoteId == null) Clock.System.now() else state.createdAt,
                updatedAt = Clock.System.now()
            )
            
            saveNoteUseCase(note)
                .onSuccess {
                    _events.emit(AddNoteEvent.NoteSaved)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    _events.emit(AddNoteEvent.Error(error.message ?: "Gagal menyimpan"))
                }
        }
    }
}

data class AddNoteUiState(
    val title: String = "",
    val content: String = "",
    val category: NoteCategory = NoteCategory.GENERAL,
    val color: NoteColor = NoteColor.DEFAULT,
    val emotion: String? = null,
    val artToken: String? = null,
    val aiResonance: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isAnalyzing: Boolean = false,
    val isEditMode: Boolean = false,
    val titleError: String? = null,
    val createdAt: Instant = Clock.System.now(),
    val selectedMainCategory: EmotionCategory? = null,
    val selectedSubEmotion: String? = null
) {
    val isValid: Boolean
        get() = title.isNotBlank() || content.isNotBlank()
    
    val canSave: Boolean
        get() = isValid && !isSaving && !isAnalyzing
}

sealed interface AddNoteEvent {
    data object NoteSaved : AddNoteEvent
    data class Error(val message: String) : AddNoteEvent
}
