package com.example.data.local

import com.example.model.PomodoroSessionLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PomodoroStatsRepository(
    private val pomodoroSessionDao: PomodoroSessionDao
) {
    val sessions: Flow<List<PomodoroSessionLog>> =
        pomodoroSessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun recordCompletedSession(
        subjectTitle: String,
        durationMinutes: Int,
        goal: String = "",
        completedAt: Long = System.currentTimeMillis()
    ): Long {
        val cleanSubject = subjectTitle.trim().ifEmpty { "عام — الشهادة السودانية" }
        val safeMinutes = durationMinutes.coerceAtLeast(1)
        return pomodoroSessionDao.insertSession(
            PomodoroSessionEntity(
                subjectTitle = cleanSubject,
                durationMinutes = safeMinutes,
                goal = goal.trim(),
                completedAt = completedAt
            )
        )
    }

    suspend fun deleteSession(id: Long) {
        if (id > 0L) {
            pomodoroSessionDao.deleteSessionById(id)
        }
    }

    suspend fun clearAll() {
        pomodoroSessionDao.clearAllSessions()
    }
}
