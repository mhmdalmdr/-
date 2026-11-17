package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.QuizResultRecord

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val subjectTitle: String,
    val topic: String,
    val quizTitle: String,
    val totalQuestions: Int,
    val firstTryCorrectCount: Int,
    val guidedCorrectCount: Int,
    val revealedCount: Int,
    val totalWrongAttempts: Int,
    val completedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): QuizResultRecord = QuizResultRecord(
        id = id,
        subjectTitle = subjectTitle,
        topic = topic,
        quizTitle = quizTitle,
        totalQuestions = totalQuestions,
        firstTryCorrectCount = firstTryCorrectCount,
        guidedCorrectCount = guidedCorrectCount,
        revealedCount = revealedCount,
        totalWrongAttempts = totalWrongAttempts,
        completedAt = completedAt
    )

    companion object {
        fun fromDomain(record: QuizResultRecord): QuizResultEntity = QuizResultEntity(
            id = record.id,
            subjectTitle = record.subjectTitle,
            topic = record.topic,
            quizTitle = record.quizTitle,
            totalQuestions = record.totalQuestions,
            firstTryCorrectCount = record.firstTryCorrectCount,
            guidedCorrectCount = record.guidedCorrectCount,
            revealedCount = record.revealedCount,
            totalWrongAttempts = record.totalWrongAttempts,
            completedAt = record.completedAt
        )
    }
}
