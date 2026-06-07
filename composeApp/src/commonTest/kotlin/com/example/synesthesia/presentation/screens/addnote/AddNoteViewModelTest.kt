package com.example.synesthesia.presentation.screens.addnote

import app.cash.turbine.test
import com.example.synesthesia.data.repository.FakeNoteRepository
import com.example.synesthesia.domain.usecase.SaveNoteUseCase
import com.example.synesthesia.presentation.screens.addnote.AddNoteViewModel
import com.example.synesthesia.presentation.screens.addnote.AddNoteEvent
import com.example.synesthesia.data.remote.api.GeminiService
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddNoteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeNoteRepository
    private lateinit var saveNoteUseCase: SaveNoteUseCase
    private lateinit var geminiService: GeminiService
    private lateinit var viewModel: AddNoteViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeNoteRepository()
        saveNoteUseCase = SaveNoteUseCase(repository)
        geminiService = GeminiService(HttpClient()) // Dummy client
        
        viewModel = AddNoteViewModel(
            repository = repository,
            saveNoteUseCase = saveNoteUseCase,
            geminiService = geminiService
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty content and isAnalyzing false`() = runTest {
        assertEquals("", viewModel.uiState.value.content)
        assertTrue(!viewModel.uiState.value.isAnalyzing)
    }

    @Test
    fun `saveNote with content less than 5 should emit error event`() = runTest {
        viewModel.events.test {
            // Act
            viewModel.onContentChange("abc")
            viewModel.saveNote()
            
            // Assert
            val event = awaitItem()
            assertTrue(event is AddNoteEvent.Error)
            assertEquals("Tuliskan minimal 5 karakter curhatanmu", event.message)
        }
    }
}
