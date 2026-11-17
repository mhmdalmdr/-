package com.example.util

import com.example.data.CurriculumData
import com.example.model.MasteryLevel
import com.example.model.QuizResultRecord
import com.example.model.RecommendedStudyMaterial
import com.example.model.Subject
import com.example.model.SubjectProgressSummary
import com.example.model.Textbook
import com.example.model.TopicProgressAssessment
import kotlin.math.roundToInt

object ProgressAssessmentEngine {

    fun calculateMasteryPercent(
        firstTryCorrect: Int,
        guidedCorrect: Int,
        totalQuestions: Int
    ): Int {
        val safeTotal = totalQuestions.coerceAtLeast(1)
        val weightedScore = (firstTryCorrect.coerceAtLeast(0) * 100.0) +
            (guidedCorrect.coerceAtLeast(0) * 60.0)
        return (weightedScore / safeTotal).roundToInt().coerceIn(0, 100)
    }

    fun masteryLevelFor(percent: Int): MasteryLevel {
        return when {
            percent < 60 -> MasteryLevel.NEEDS_REVIEW
            percent < 80 -> MasteryLevel.DEVELOPING
            else -> MasteryLevel.MASTERED
        }
    }

    fun assessOverallMastery(results: List<QuizResultRecord>): Int {
        if (results.isEmpty()) return 0
        val totalQ = results.sumOf { it.totalQuestions }.coerceAtLeast(1)
        val firstTry = results.sumOf { it.firstTryCorrectCount }
        val guided = results.sumOf { it.guidedCorrectCount }
        return calculateMasteryPercent(firstTry, guided, totalQ)
    }

    fun assessByTopic(results: List<QuizResultRecord>): List<TopicProgressAssessment> {
        if (results.isEmpty()) return emptyList()
        return results
            .groupBy {
                val subj = it.subjectTitle.trim().ifBlank { "عام — الشهادة السودانية" }
                val top = it.topic.trim().ifBlank { it.quizTitle.trim().ifBlank { "مراجعة عامة" } }
                subj to top
            }
            .map { (key, group) ->
                val (subjectTitle, topic) = key
                val totalQ = group.sumOf { it.totalQuestions }.coerceAtLeast(1)
                val firstTry = group.sumOf { it.firstTryCorrectCount }
                val guided = group.sumOf { it.guidedCorrectCount }
                val revealed = group.sumOf { it.revealedCount }
                val wrongTotal = group.sumOf { it.totalWrongAttempts }
                val mastery = calculateMasteryPercent(firstTry, guided, totalQ)
                TopicProgressAssessment(
                    subjectTitle = subjectTitle,
                    topic = topic,
                    attemptsCount = group.size,
                    totalQuestionsAnswered = totalQ,
                    firstTryCorrectTotal = firstTry,
                    guidedCorrectTotal = guided,
                    revealedTotal = revealed,
                    wrongAttemptsTotal = wrongTotal,
                    masteryPercent = mastery,
                    masteryLevel = masteryLevelFor(mastery),
                    lastAttemptAt = group.maxOf { it.completedAt }
                )
            }
            .sortedWith(compareBy<TopicProgressAssessment> { it.masteryPercent }.thenByDescending { it.wrongAttemptsTotal })
    }

    fun assessBySubject(
        results: List<QuizResultRecord>,
        subjects: List<Subject> = CurriculumData.subjects
    ): List<SubjectProgressSummary> {
        if (results.isEmpty()) return emptyList()
        val topicAssessments = assessByTopic(results)

        return results
            .groupBy { it.subjectTitle.trim().ifBlank { "عام — الشهادة السودانية" } }
            .map { (subjectTitle, group) ->
                val totalQ = group.sumOf { it.totalQuestions }.coerceAtLeast(1)
                val firstTry = group.sumOf { it.firstTryCorrectCount }
                val guided = group.sumOf { it.guidedCorrectCount }
                val mastery = calculateMasteryPercent(firstTry, guided, totalQ)
                val matchedSubject = findMatchingSubject(subjectTitle, subjects)
                val weakCount = topicAssessments.count {
                    it.subjectTitle == subjectTitle && it.masteryLevel != MasteryLevel.MASTERED
                }
                SubjectProgressSummary(
                    subjectTitle = subjectTitle,
                    subjectId = matchedSubject?.id,
                    quizzesCompleted = group.size,
                    totalQuestions = totalQ,
                    averageMasteryPercent = mastery,
                    masteryLevel = masteryLevelFor(mastery),
                    weakTopicsCount = weakCount
                )
            }
            .sortedBy { it.averageMasteryPercent }
    }

    fun buildStudyRecommendations(
        results: List<QuizResultRecord>,
        subjects: List<Subject> = CurriculumData.subjects,
        textbooks: List<Textbook> = CurriculumData.textbooks
    ): List<RecommendedStudyMaterial> {
        val weakTopics = assessByTopic(results).filter { it.masteryLevel != MasteryLevel.MASTERED }
        if (weakTopics.isEmpty()) return emptyList()

        return weakTopics.mapIndexed { idx, assessment ->
            val matchedSubject = findMatchingSubject(assessment.subjectTitle, subjects)
                ?: findMatchingSubjectByTopic(assessment.topic, subjects)

            val matchedUnit = matchedSubject?.units?.firstOrNull { unit ->
                hasKeywordOverlap(assessment.topic, unit.title) ||
                    hasKeywordOverlap(assessment.topic, unit.subtitle) ||
                    unit.keyPoints.any { hasKeywordOverlap(assessment.topic, it) }
            } ?: matchedSubject?.units?.firstOrNull()

            val matchedTextbook = textbooks.firstOrNull { book ->
                (matchedSubject != null && book.subjectId == matchedSubject.id) ||
                    book.subjectTitle.contains(assessment.subjectTitle, ignoreCase = true) ||
                    assessment.subjectTitle.contains(book.subjectTitle, ignoreCase = true)
            }

            val priorityLabel = if (assessment.masteryLevel == MasteryLevel.NEEDS_REVIEW) {
                "أولوية قصوى للمراجعة"
            } else {
                "مراجعة تعزيزية مقترحة"
            }

            val weaknessReason = buildString {
                append("نسبة الإتقان ${assessment.masteryPercent}% في ${assessment.totalQuestionsAnswered} أسئلة")
                if (assessment.revealedTotal > 0) {
                    append(" • تم كشف ${assessment.revealedTotal} إجابة")
                } else if (assessment.wrongAttemptsTotal > 0) {
                    append(" • ${assessment.wrongAttemptsTotal} محاولات غير دقيقة")
                }
            }

            val suggestedActionText = when {
                matchedUnit != null && matchedTextbook != null ->
                    "راجع درس «${matchedUnit.title}» واستكمل التمارين في كتاب «${matchedTextbook.title}»، ثم أعد الاختبار بالمستوى التأسيسي."
                matchedUnit != null ->
                    "راجع درس «${matchedUnit.title}» والنقاط المحورية للوحدة، ثم تدرب باختبار تأسيسي موجّه."
                matchedTextbook != null ->
                    "اطلع على ملخصات الفصول في كتاب «${matchedTextbook.title}» قبل خوض اختبار مراجعة جديد."
                else ->
                    "اطلب شرحاً مبسطاً من المعلم الذكي حول «${assessment.topic}»، ثم تدرب باختبار تأسيسي لتثبيت المفهوم."
            }

            RecommendedStudyMaterial(
                id = "rec_${idx}_${assessment.subjectTitle}_${assessment.topic}",
                subjectTitle = matchedSubject?.title ?: assessment.subjectTitle,
                topic = assessment.topic,
                masteryPercent = assessment.masteryPercent,
                masteryLevel = assessment.masteryLevel,
                priorityLabel = priorityLabel,
                weaknessReason = weaknessReason,
                suggestedActionText = suggestedActionText,
                matchedSubjectId = matchedSubject?.id,
                matchedUnitId = matchedUnit?.id,
                matchedUnitTitle = matchedUnit?.title,
                matchedTextbookId = matchedTextbook?.id,
                matchedTextbookTitle = matchedTextbook?.title
            )
        }
    }

    private fun findMatchingSubject(subjectTitle: String, subjects: List<Subject>): Subject? {
        val clean = subjectTitle.trim()
        if (clean.isEmpty()) return null
        return subjects.firstOrNull { sub ->
            sub.title.equals(clean, ignoreCase = true) ||
                sub.title.contains(clean, ignoreCase = true) ||
                clean.contains(sub.title, ignoreCase = true) ||
                clean.contains(sub.category.titleAr, ignoreCase = true)
        }
    }

    private fun findMatchingSubjectByTopic(topic: String, subjects: List<Subject>): Subject? {
        return subjects.firstOrNull { sub ->
            sub.units.any { unit ->
                hasKeywordOverlap(topic, unit.title) || hasKeywordOverlap(topic, unit.subtitle)
            }
        }
    }

    private fun hasKeywordOverlap(query: String, target: String): Boolean {
        val tokens = query.split(" ", "،", "-", ":", "(", ")")
            .map { it.trim() }
            .filter { it.length >= 3 }
        if (tokens.isEmpty()) return false
        return tokens.any { token -> target.contains(token, ignoreCase = true) }
    }
}
