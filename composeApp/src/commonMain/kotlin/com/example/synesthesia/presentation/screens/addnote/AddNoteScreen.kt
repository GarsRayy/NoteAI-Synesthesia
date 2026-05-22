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
    var currentStep by remember { mutableStateOf(1) }
    
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
                            if (currentStep == 1) "HOW WE FEEL" else "THE JOURNALING",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentStep > 1) currentStep-- else onNavigateBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (currentStep == 2) {
                            TextButton(onClick = { viewModel.saveNote() }) {
                                Text("FINISH", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                )
            }
        ) { padding ->
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn() + slideInHorizontally { it } togetherWith fadeOut() + slideOutHorizontally { -it }
                },
                modifier = Modifier.padding(padding)
            ) { step ->
                when (step) {
                    1 -> EmotionSelectionStep(onCategorySelect = { 
                        viewModel.onMainCategorySelected(it)
                        currentStep = 2
                    })
                    2 -> JournalingStep(
                        content = uiState.content,
                        onContentChange = viewModel::onContentChange,
                        isAnalyzing = uiState.isAnalyzing,
                        isSaving = uiState.isSaving
                    )
                }
            }
        }
    }
}

@Composable
fun EmotionSelectionStep(onCategorySelect: (EmotionCategory) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "What's your emotional state?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(EmotionSystem.categories) { category ->
                EmotionCard3D(category) { onCategorySelect(category) }
            }
        }
    }
}

@Composable
fun EmotionCard3D(category: EmotionCategory, onClick: () -> Unit) {
    val color = parseHexColor(category.color)
    val emoji = when(category.id) {
        "HEP" -> "🌞"
        "HEU" -> "🔥"
        "LEP" -> "🌿"
        "LEU" -> "🌊"
        else -> "✨"
    }
    val label = when(category.id) {
        "HEP" -> "JOY"
        "HEU" -> "ANGER"
        "LEP" -> "CALM"
        "LEU" -> "MELANCHOLY"
        else -> category.name
    }
    val description = when(category.id) {
        "HEP" -> "High Energy, Pleasant"
        "HEU" -> "High Energy, Unpleasant"
        "LEP" -> "Low Energy, Pleasant"
        "LEU" -> "Low Energy, Unpleasant"
        else -> ""
    }

    Card(
        modifier = Modifier
            .aspectRatio(0.7f)
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.9f))
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(emoji, fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    label,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    description,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
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

private fun parseHexColor(hex: String?): Color {
    if (hex == null || !hex.startsWith("#")) return Color.Gray
    return try {
        Color(hex.removePrefix("#").toLong(16) or 0xFF000000)
    } catch (e: Exception) {
        Color.Gray
    }
}
