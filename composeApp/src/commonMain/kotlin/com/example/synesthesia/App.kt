package com.example.synesthesia

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.synesthesia.presentation.app.AppViewModel
import com.example.synesthesia.presentation.navigation.AppNavHost
import com.example.synesthesia.presentation.theme.NoteAITheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    viewModel: AppViewModel = koinViewModel()
) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    KoinContext {
        NoteAITheme(
            themeMode = themeMode
        ) {
            AppNavHost()
        }
    }
}
