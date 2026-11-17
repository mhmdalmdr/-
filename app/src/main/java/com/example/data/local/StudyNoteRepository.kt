package com.example.data.local

import com.example.model.StudyNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudyNoteRepository(private val studyNoteDao: StudyNoteDao) {

    val notes: Flow<List<StudyNote>> = studyNoteDao.getAllNotes().map { entities ->
        entities.map { it.toStudyNote() }
    }

    suspend fun saveNote(note: StudyNote): Long {
        return studyNoteDao.insertNote(StudyNoteEntity.fromStudyNote(note))
    }

    suspend fun updateNote(note: StudyNote) {
        studyNoteDao.updateNote(StudyNoteEntity.fromStudyNote(note))
    }

    suspend fun togglePin(id: Long, isPinned: Boolean) {
        studyNoteDao.updatePinnedStatus(
            id = id,
            isPinned = isPinned,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun deleteNote(id: Long) {
        studyNoteDao.deleteNoteById(id)
    }

    suspend fun getNotesCount(): Int {
        return studyNoteDao.getNotesCount()
    }
}
