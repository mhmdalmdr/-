package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.StudyNote

@Entity(tableName = "study_notes")
data class StudyNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val content: String,
    val subjectTitle: String,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toStudyNote(): StudyNote {
        return StudyNote(
            id = id,
            title = title,
            content = content,
            subjectTitle = subjectTitle,
            isPinned = isPinned,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromStudyNote(note: StudyNote): StudyNoteEntity {
            return StudyNoteEntity(
                id = note.id,
                title = note.title,
                content = note.content,
                subjectTitle = note.subjectTitle,
                isPinned = note.isPinned,
                createdAt = note.createdAt,
                updatedAt = note.updatedAt
            )
        }
    }
}
