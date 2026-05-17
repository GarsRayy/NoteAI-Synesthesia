package com.example.synesthesia.presentation.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.synesthesia.presentation.components.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onShare: (String) -> Unit,
    viewModel: NoteDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Generative Art Layer
        val emotion = (uiState as? NoteDetailUiState.Success)?.note?.emotion
        EmotionArtCanvas(
            emotion = emotion,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                    },
                    actions = {
                        if (uiState is NoteDetailUiState.Success) {
                            val note = (uiState as NoteDetailUiState.Success).note
                            IconButton(onClick = { viewModel.togglePin() }) {
                                Icon(
                                    imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = "Pin"
                                )
                            }
                            IconButton(onClick = { 
                                viewModel.getShareContent()?.let { onShare(it) }
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "Share")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (uiState is NoteDetailUiState.Success) {
                    val noteId = (uiState as NoteDetailUiState.Success).note.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassCard(
                            cornerRadius = 32.dp,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(onClick = { onNavigateToEdit(noteId) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                                }
                                IconButton(onClick = { viewModel.deleteNote() }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.White.copy(alpha = 0.7f))
                                }
                            }
                        }
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
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            text = state.note.title.ifBlank { "Tanpa Judul" },
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        CategoryBadge(category = state.note.category.displayName)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = state.note.content,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
                        )
                        
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
                is NoteDetailUiState.NotFound -> {
                    EmptyState(title = "Tidak Ditemukan", message = "Catatan ini mungkin sudah dihapus.")
                }
            }
        }
    }
}
