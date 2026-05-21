package com.example.synesthesia.presentation.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.synesthesia.presentation.components.*
import com.example.synesthesia.presentation.theme.CrispWhite
import com.example.synesthesia.presentation.theme.VibrantAmber
import com.example.synesthesia.presentation.theme.ObsidianBlack
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryDetailScreen(
    noteId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: NoteDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is NoteDetailEvent.NoteDeleted) {
                onNavigateBack()
            }
        }
    }

    val quadrant = (uiState as? NoteDetailUiState.Success)?.note?.let { 
        getQuadrantFromEmotion(it.emotion) 
    } ?: 3

    Box(modifier = Modifier.fillMaxSize().background(ObsidianBlack)) {
        // Generative Background
        EndelBackground(emotionQuadrant = quadrant)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = CrispWhite,
                        navigationIconContentColor = CrispWhite
                    ),
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (uiState is NoteDetailUiState.Success) {
                            IconButton(onClick = { viewModel.togglePin() }) {
                                val isPinned = (uiState as NoteDetailUiState.Success).note.isPinned
                                Icon(
                                    imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = "Pin",
                                    tint = if (isPinned) VibrantAmber else CrispWhite.copy(alpha = 0.6f)
                                )
                            }
                            IconButton(onClick = { viewModel.deleteNote() }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                if (uiState is NoteDetailUiState.Success) {
                    FloatingActionButton(
                        onClick = { onNavigateToEdit(noteId) },
                        containerColor = VibrantAmber,
                        contentColor = ObsidianBlack,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            }
        ) { paddingValues ->
            when (val state = uiState) {
                is NoteDetailUiState.Loading -> {
                    LoadingIndicator()
                }
                is NoteDetailUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Typography focused title
                        Text(
                            text = state.note.title.ifBlank { "Untitled Reflection" },
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = CrispWhite,
                                letterSpacing = (-1).sp
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Subtitle/Emotion tag
                        Text(
                            text = state.note.emotion?.uppercase() ?: "RESONANCE",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = CrispWhite.copy(alpha = 0.5f),
                                letterSpacing = 2.sp
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(48.dp))
                        
                        // Sleek Glassmorphic Content Card
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 24.dp
                        ) {
                            Text(
                                text = state.note.content,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 32.sp,
                                    color = CrispWhite
                                ),
                                modifier = Modifier.padding(24.dp)
                            )
                        }

                        state.note.aiResonance?.let { resonance ->
                            Spacer(modifier = Modifier.height(40.dp))
                            
                            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = VibrantAmber
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "AI RESONANCE",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            color = VibrantAmber,
                                            letterSpacing = 1.5.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = resonance,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontStyle = FontStyle.Italic,
                                        color = CrispWhite.copy(alpha = 0.8f),
                                        lineHeight = 26.sp
                                    )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(120.dp))
                    }
                }
                is NoteDetailUiState.NotFound -> {
                    EmptyState(title = "Fragment Lost", message = "This memory has returned to the void.")
                }
            }
        }
    }
}
