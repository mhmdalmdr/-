package com.example.data.local

import com.example.model.QuizResultRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuizProgressRepository(
    private val quizResultDao: QuizResultDao
) {
    val results: Flow<List<QuizResultRecord>> =
        quizResultDao.getAllResults().map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun recordQuizResult(record: QuizResultRecord): Long {
        val cleanSubject = record.subjectTitle.trim().ifEmpty { "عام — الشهادة السودانية" }
        val cleanTopic = record.topic.trim().ifEmpty { record.quizTitle.trim().ifEmpty { "مراجعة عامة" } }
        val safeTotal = record.totalQuestions.coerceAtLeast(1)
        return quizResultDao.insertResult(
            QuizResultEntity.fromDomain(
                record.copy(
                    subjectTitle = cleanSubject,
                    topic = cleanTopic,
                    totalQuestions = safeTotal
                )
            )
        )
    }

    suspend fun deleteResult(id: Long) {
        if (id > 0L) {
            quizResultDao.deleteResultById(id)
        }
    }

    suspend fun clearAll() {
        quizResultDao.clearAllResults()
    }
}
