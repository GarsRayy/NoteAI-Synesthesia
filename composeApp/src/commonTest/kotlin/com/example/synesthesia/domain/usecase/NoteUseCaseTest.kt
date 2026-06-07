package com.example.synesthesia.domain.usecase

import app.cash.turbine.test
import com.example.synesthesia.data.repository.FakeNoteRepository
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoteUseCaseTest {

    private lateinit var repository: FakeNoteRepository
    private lateinit var getAllNotesUseCase: GetAllNotesUseCase
    private lateinit var saveNoteUseCase: SaveNoteUseCase
    private lateinit var searchNotesUseCase: SearchNotesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeNoteRepository()
        getAllNotesUseCase = GetAllNotesUseCase(repository)
        saveNoteUseCase = SaveNoteUseCase(repository)
        searchNotesUseCase = SearchNotesUseCase(repository)
    }

    @Test
    fun `GetAllNotesUseCase should return pinned notes first`() = runTest {
        // Arrange
        repository.insertNote(createNote("N1", isPinned = false))
        repository.insertNote(createNote("N2", isPinned = true))

        // Act & Assert
        getAllNotesUseCase().test {
            val notes = awaitItem()
            assertEquals(2, notes.size)
            assertTrue(notes[0].isPinned)
            assertEquals("N2", notes[0].title)
        }
    }

    @Test
    fun `SaveNoteUseCase should fail for blank note`() = runTest {
        // Arrange
        val note = createNote("", isPinned = false).copy(content = "")

        // Act
        val result = saveNoteUseCase(note)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `SearchNotesUseCase should filter by category correctly`() = runTest {
        // Arrange
        repository.insertNote(createNote("Work Note", category = NoteCategory.WORK))
        repository.insertNote(createNote("Personal Note", category = NoteCategory.PERSONAL))

        // Act & Assert
        searchNotesUseCase("", NoteCategory.WORK).test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals(NoteCategory.WORK, notes.first().category)
        }
    }

    private fun createNote(title: String, isPinned: Boolean = false, category: NoteCategory = NoteCategory.GENERAL): Note {
        return Note(
            id = 0,
            title = title,
            content = "Test content",
            category = category,
            color = NoteColor.DEFAULT,
            isPinned = isPinned,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}
