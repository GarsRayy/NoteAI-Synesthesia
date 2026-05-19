package com.example.synesthesia.data.local.entity

import com.example.synesthesia.data.local.NoteEntity
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import kotlinx.datetime.Instant

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        category = NoteCategory.fromString(category),
        color = NoteColor.fromString(color),
        emotion = emotion,
        artToken = art_token,
        aiResonance = ai_resonance,
        isPinned = is_pinned == 1L,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

data class NoteEntityValues(
    val title: String,
    val content: String,
    val category: String,
    val color: String,
    val emotion: String?,
    val artToken: String?,
    val aiResonance: String?,
    val isPinned: Long,
    val createdAt: Long,
    val updatedAt: Long
)

fun Note.toEntityValues(): NoteEntityValues {
    return NoteEntityValues(
        title = title,
        content = content,
        category = category.name,
        color = color.name,
        emotion = emotion,
        artToken = artToken,
        aiResonance = aiResonance,
        isPinned = if (isPinned) 1L else 0L,
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = updatedAt.toEpochMilliseconds()
    )
}

fun List<NoteEntity>.toDomainList(): List<Note> {
    return map { it.toDomain() }
}
