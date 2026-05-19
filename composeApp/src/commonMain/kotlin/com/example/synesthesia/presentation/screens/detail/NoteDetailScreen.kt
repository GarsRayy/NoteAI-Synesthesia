package com.example.synesthesia.presentation.screens.detail

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Share
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

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is NoteDetailEvent.NoteDeleted) {
                onNavigateBack()
            }
        }
    }

    AuroraBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
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
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurface)
                                }
                                IconButton(onClick = { viewModel.deleteNote() }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
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
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        CategoryBadge(category = state.note.category.displayName)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        GlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.note.content,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        state.note.aiResonance?.let { resonance ->
                            Spacer(modifier = Modifier.height(24.dp))
                            Surface(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, Color(0xFF0235AC))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color(0xFF0235AC)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = resonance,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF0235AC),
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }
                        }
                        
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
