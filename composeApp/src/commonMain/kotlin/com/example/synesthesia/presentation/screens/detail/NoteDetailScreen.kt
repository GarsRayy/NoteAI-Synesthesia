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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.synesthesia.presentation.components.*
import com.example.synesthesia.presentation.theme.RoyalBlue
import com.example.synesthesia.presentation.theme.DeepIndigo
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

    val emotionColor = (uiState as? NoteDetailUiState.Success)?.note?.artToken?.let { parseHexColor(it) } ?: RoyalBlue

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Dynamic Radial Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(emotionColor.copy(alpha = 0.15f), Color.Transparent),
                        radius = 2000f
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = RoyalBlue
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
                                    tint = RoyalBlue
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
                        containerColor = RoyalBlue,
                        contentColor = Color.White,
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
                            .padding(horizontal = 28.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = state.note.title.ifBlank { "Untitled Reflection" },
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Surface(
                            color = emotionColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = state.note.emotion?.uppercase() ?: "RESONANCE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = emotionColor,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Sharp Content Card
                        GlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.note.content,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 30.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(20.dp)
                            )
                        }

                        state.note.aiResonance?.let { resonance ->
                            Spacer(modifier = Modifier.height(32.dp))
                            Surface(
                                color = RoyalBlue.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = RoyalBlue
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "AI RESONANCE",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Black,
                                                color = RoyalBlue,
                                                letterSpacing = 1.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = resonance,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontStyle = FontStyle.Italic,
                                            color = RoyalBlue.copy(alpha = 0.8f)
                                        )
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
                is NoteDetailUiState.NotFound -> {
                    EmptyState(title = "Fragment Lost", message = "This memory has returned to the void.")
                }
            }
        }
    }
}

private fun parseHexColor(hex: String?): Color? {
    if (hex == null || !hex.startsWith("#")) return null
    return try {
        Color(hex.removePrefix("#").toLong(16) or 0xFF000000)
    } catch (e: Exception) {
        null
    }
}
