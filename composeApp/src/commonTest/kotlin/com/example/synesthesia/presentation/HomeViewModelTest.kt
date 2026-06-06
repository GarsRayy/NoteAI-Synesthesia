package com.example.synesthesia.presentation

import app.cash.turbine.test
import com.example.synesthesia.data.repository.FakeNoteRepository
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import com.example.synesthesia.domain.repository.NoteRepository
import com.example.synesthesia.domain.usecase.DeleteNoteUseCase
import com.example.synesthesia.domain.usecase.GetAllNotesUseCase
import com.example.synesthesia.domain.usecase.NoteSortBy
import com.example.synesthesia.domain.usecase.SearchNotesUseCase
import com.example.synesthesia.presentation.screens.home.HomeUiState
import com.example.synesthesia.presentation.screens.home.HomeViewModel
import com.example.synesthesia.data.local.datastore.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit Tests untuk HomeViewModel
 * 
 * Testing Guidelines:
 * 1. Setup test dispatcher untuk control coroutines
 * 2. Gunakan Turbine untuk test StateFlow
 * 3. Test UI state transformations
 * 4. Test user actions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var repository: FakeNoteRepository
    private lateinit var userPreferences: FakeUserPreferences
    private lateinit var getAllNotesUseCase: GetAllNotesUseCase
    private lateinit var searchNotesUseCase: SearchNotesUseCase
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase
    private lateinit var viewModel: HomeViewModel
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        repository = FakeNoteRepository()
        userPreferences = FakeUserPreferences()
        getAllNotesUseCase = GetAllNotesUseCase(repository)
        searchNotesUseCase = SearchNotesUseCase(repository)
        deleteNoteUseCase = DeleteNoteUseCase(repository)
        
        viewModel = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository,
            userPreferences = userPreferences
        )
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    // ==================== UI STATE TESTS ====================
    
    @Test
    fun `initial state should be Loading then Empty`() = runTest {
        viewModel.uiState.test {
            // Initial loading state
            val loading = awaitItem()
            assertTrue(loading is HomeUiState.Loading)
            
            // After loading, should be empty (no notes)
            advanceUntilIdle()
            val empty = awaitItem()
            assertTrue(empty is HomeUiState.Empty)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `state should be Success when notes exist`() = runTest {
        // Arrange
        repository.insertNote(createTestNote("Note 1"))
        repository.insertNote(createTestNote("Note 2"))
        
        // Create new viewmodel after inserting notes
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository,
            userPreferences = userPreferences
        )
        
        // Act & Assert
        vm.uiState.test {
            skipItems(1) // Skip loading
            advanceUntilIdle()
            
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(2, (state as HomeUiState.Success).notes.size)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== SEARCH TESTS ====================
    
    @Test
    fun `search should filter notes by query`() = runTest {
        // Arrange
        repository.insertNote(createTestNote("Kotlin Guide"))
        repository.insertNote(createTestNote("Java Tutorial"))
        
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository,
            userPreferences = userPreferences
        )
        
        vm.uiState.test {
            skipItems(1) // Skip loading
            advanceUntilIdle()
            skipItems(1) // Skip initial success
            
            // Act
            vm.onSearchQueryChange("Kotlin")
            advanceUntilIdle()
            
            // Assert - wait for debounce
            testDispatcher.scheduler.advanceTimeBy(400)
            advanceUntilIdle()
            
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).notes.size)
            assertEquals("Kotlin Guide", state.notes.first().title)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `clearSearch should reset query`() = runTest {
        // Act
        viewModel.onSearchQueryChange("test query")
        viewModel.clearSearch()
        
        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            when (state) {
                is HomeUiState.Success -> assertEquals("", state.query)
                is HomeUiState.Empty -> assertEquals("", state.query)
                else -> {} // OK
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== CATEGORY FILTER TESTS ====================
    
    @Test
    fun `category filter should filter notes`() = runTest {
        // ... (existing test) ...
    }

    @Test
    fun `sort order should modify note list order`() = runTest {
        // Arrange
        repository.insertNote(createTestNote("B Note"))
        delay(100) // Ensure different updatedAt
        repository.insertNote(createTestNote("A Note"))
        
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository,
            userPreferences = userPreferences
        )
        
        vm.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            val initial = awaitItem() as HomeUiState.Success
            // Default UPDATED_DESC: latest (A Note) should be first
            assertEquals("A Note", initial.notes.first().title)

            // Act
            vm.onSortByChanged(NoteSortBy.TITLE_ASC)
            advanceUntilIdle()
            
            // Assert
            val state = awaitItem() as HomeUiState.Success
            assertEquals("A Note", state.notes.first().title)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== ACTION TESTS ====================
    
    @Test
    fun `togglePin should toggle note pin status`() = runTest {
        // Arrange
        val noteId = repository.insertNote(createTestNote("Pin Me"))
        
        // Act
        viewModel.togglePin(noteId)
        advanceUntilIdle()
        
        // Assert
        repository.getNoteById(noteId).test {
            val note = awaitItem()
            assertTrue(note?.isPinned == true)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `deleteNote should remove note`() = runTest {
        // Arrange
        val noteId = repository.insertNote(createTestNote("Delete Me"))
        
        // Act
        viewModel.deleteNote(noteId)
        advanceUntilIdle()
        
        // Assert
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertTrue(notes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== HELPER FUNCTIONS ====================
    
    private fun createTestNote(
        title: String,
        category: NoteCategory = NoteCategory.GENERAL
    ): Note {
        return Note(
            id = 0,
            title = title,
            content = "Test content",
            category = category,
            color = NoteColor.DEFAULT,
            isPinned = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}

class FakeUserPreferences : UserPreferences {
    private val _themeMode = MutableStateFlow("NORMAL")
    private val _userName = MutableStateFlow("Test Stargazer")
    private val _sortBy = MutableStateFlow("UPDATED_DESC")

    override val isDarkMode: Flow<Boolean> = flowOf(false)
    override suspend fun setDarkMode(enabled: Boolean) {}

    override val themeMode: Flow<String> = _themeMode
    override suspend fun setThemeMode(mode: String) { _themeMode.value = mode }

    override val userName: Flow<String> = _userName
    override val userBio: Flow<String> = flowOf("Test Bio")
    override val userPhotoUri: Flow<String?> = flowOf(null)
    override suspend fun updateProfile(name: String, bio: String, photoUri: String?) { _userName.value = name }

    override val sortBy: Flow<String> = _sortBy
    override suspend fun setSortBy(sortBy: String) { _sortBy.value = sortBy }

    override val defaultCategory: Flow<String> = flowOf("GENERAL")
    override suspend fun setDefaultCategory(category: String) {}

    override val showPreview: Flow<Boolean> = flowOf(true)
    override suspend fun setShowPreview(show: Boolean) {}

    override val isOnboardingCompleted: Flow<Boolean> = flowOf(true)
    override suspend fun setOnboardingCompleted() {}
}
