package com.example.synesthesia.presentation.screens.addnote

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.synesthesia.domain.model.EmotionCategory
import com.example.synesthesia.domain.model.EmotionSystem
import com.example.synesthesia.presentation.components.CelestialBackground
import com.example.synesthesia.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    noteId: Long?,
    aiResult: String? = null,
    onResultConsumed: () -> Unit = {},
    onNavigateBack: () -> Unit,
    onNavigateToAI: (String) -> Unit,
    viewModel: AddNoteViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // We assume the theme mode is handled by the parent, but for background we check if it's dark
    val isAstronomy = MaterialTheme.colorScheme.background == SpaceBlack

    LaunchedEffect(noteId) {
        noteId?.let { viewModel.loadNote(it) }
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddNoteEvent.NoteSaved -> onNavigateBack()
                is AddNoteEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }
    
    CelestialBackground(isAstronomyMode = isAstronomy) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    title = { 
                        Text(
                            "THE JOURNALING",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        TextButton(onClick = { viewModel.saveNote() }) {
                            Text("FINISH", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                JournalingStep(
                    content = uiState.content,
                    onContentChange = viewModel::onContentChange,
                    isAnalyzing = uiState.isAnalyzing,
                    isSaving = uiState.isSaving
                )
            }
        }
    }
}

@Composable
fun JournalingStep(
    content: String,
    onContentChange: (String) -> Unit,
    isAnalyzing: Boolean,
    isSaving: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isAnalyzing || isSaving) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(strokeWidth = 4.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    if (isAnalyzing) "AI is sensing your emotions..." else "Saving to your galaxy...",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                placeholder = { Text("Write your soul here...") },
                modifier = Modifier.fillMaxSize().padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
