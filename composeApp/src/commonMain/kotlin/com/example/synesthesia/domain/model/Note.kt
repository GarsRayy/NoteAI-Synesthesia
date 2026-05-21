package com.example.synesthesia.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: NoteCategory = NoteCategory.GENERAL,
    val color: NoteColor = NoteColor.DEFAULT,
    val emotion: String? = null,
    val artToken: String? = null,
    val aiResonance: String? = null,
    val isPinned: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    val preview: String
        get() = if (content.length > 100) content.take(100) + "..." else content
    
    val isEmpty: Boolean
        get() = title.isBlank() && content.isBlank()
}

enum class NoteCategory(val displayName: String) {
    GENERAL("Umum"),
    WORK("Pekerjaan"),
    PERSONAL("Pribadi"),
    IDEAS("Ide"),
    TODO("To-Do"),
    STUDY("Belajar");
    
    companion object {
        fun fromString(value: String): NoteCategory {
            return entries.find { it.name == value } ?: GENERAL
        }
    }
}

enum class NoteColor(val hexValue: Long) {
    DEFAULT(0xFFFFFFFF),
    JOY(0xFFF4A44A),
    MELANCHOLY(0xFF3B82C4),
    CALM(0xFF2EC9A0),
    ANGER(0xFFE05FA0),
    REFLECTIVE(0xFF7B5EA7);
    
    companion object {
        fun fromString(value: String): NoteColor {
            return entries.find { it.name == value } ?: DEFAULT
        }
    }
}
