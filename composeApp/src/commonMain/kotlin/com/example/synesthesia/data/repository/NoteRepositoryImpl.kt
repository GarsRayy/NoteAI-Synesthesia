package com.example.synesthesia.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.synesthesia.data.local.NoteDatabase
import com.example.synesthesia.data.local.entity.toDomain
import com.example.synesthesia.data.local.entity.toDomainList
import com.example.synesthesia.data.local.entity.toEntityValues
import com.example.synesthesia.data.remote.api.GeminiService
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class NoteRepositoryImpl(private val database: NoteDatabase, private val geminiService: GeminiService) : NoteRepository {
    
    private val queries = database.noteQueries
    
    override fun getAllNotes(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }
    
    override fun getPinnedNotes(): Flow<List<Note>> {
        return queries.getPinnedNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }
    
    override fun getNotesByCategory(category: NoteCategory): Flow<List<Note>> {
        return queries.getNotesByCategory(category.name)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }
    
    override fun searchNotes(query: String): Flow<List<Note>> {
        return queries.searchNotes(query, query)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }
    
    override fun getNoteById(id: Long): Flow<Note?> {
        return queries.getNoteById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity -> entity?.toDomain() }
    }
    
    override suspend fun insertNote(note: Note): Long = withContext(Dispatchers.Default) {
        var values = note.toEntityValues()
        val aiResult = geminiService.analyzeEmotion(note.content)

        aiResult.onSuccess {
            response -> values = values.copy(emotion = response.emotion, artToken = response.artToken)
        }.onFailure { it.printStackTrace() }

        queries.insertNote(
            title = values.title,
            content = values.content,
            category = values.category,
            color = values.color,
            emotion = values.emotion,
            art_token = values.artToken,
            is_pinned = values.isPinned,
            created_at = values.createdAt,
            updated_at = values.updatedAt
        )
        queries.lastInsertId().executeAsOne()
    }
    
    override suspend fun updateNote(note: Note) = withContext(Dispatchers.Default) {
        var values = note.toEntityValues()
        val aiResult = geminiService.analyzeEmotion(note.content)

        aiResult.onSuccess {
            response -> values = values.copy(emotion = response.emotion, artToken = response.artToken)
        }.onFailure { it.printStackTrace() }

        queries.updateNote(
            title = values.title,
            content = values.content,
            category = values.category,
            color = values.color,
            emotion = values.emotion,
            art_token = values.artToken,
            is_pinned = values.isPinned,
            updated_at = Clock.System.now().toEpochMilliseconds(),
            id = note.id
        )
    }
    
    override suspend fun deleteNote(id: Long) = withContext(Dispatchers.Default) {
        queries.deleteNoteById(id)
    }
    
    override suspend fun togglePinNote(id: Long) = withContext(Dispatchers.Default) {
        queries.togglePin(
            id = id,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }
    
    override suspend fun deleteNotes(ids: List<Long>) = withContext(Dispatchers.Default) {
        queries.deleteNotesByIds(ids)
    }
}
