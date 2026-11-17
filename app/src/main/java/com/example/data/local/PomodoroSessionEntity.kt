package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.PomodoroSessionLog

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val subjectTitle: String,
    val durationMinutes: Int,
    val goal: String = "",
    val completedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): PomodoroSessionLog = PomodoroSessionLog(
        id = id,
        subjectTitle = subjectTitle,
        durationMinutes = durationMinutes,
        goal = goal,
        completedAt = completedAt
    )

    companion object {
        fun fromDomain(log: PomodoroSessionLog): PomodoroSessionEntity = PomodoroSessionEntity(
            id = log.id,
            subjectTitle = log.subjectTitle,
            durationMinutes = log.durationMinutes,
            goal = log.goal,
            completedAt = log.completedAt
        )
    }
}
