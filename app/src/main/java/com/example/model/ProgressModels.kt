package com.example.model

enum class MasteryLevel(
    val titleAr: String,
    val rangeLabelAr: String
) {
    NEEDS_REVIEW("يحتاج مراجعة عاجلة", "أقل من 60%"),
    DEVELOPING("في طور التحسن", "60% - 79%"),
    MASTERED("متقن ومتمكن", "80% فأكثر")
}

data class QuizResultRecord(
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
    val solvedCount: Int
        get() = (firstTryCorrectCount + guidedCorrectCount).coerceIn(0, totalQuestions)

    val masteryPercent: Int
        get() {
            val safeTotal = totalQuestions.coerceAtLeast(1)
            val weighted = firstTryCorrectCount * 100.0 + guidedCorrectCount * 60.0
            return kotlin.math.round(weighted / safeTotal).toInt().coerceIn(0, 100)
        }
}

data class TopicProgressAssessment(
    val subjectTitle: String,
    val topic: String,
    val attemptsCount: Int,
    val totalQuestionsAnswered: Int,
    val firstTryCorrectTotal: Int,
    val guidedCorrectTotal: Int,
    val revealedTotal: Int,
    val wrongAttemptsTotal: Int,
    val masteryPercent: Int,
    val masteryLevel: MasteryLevel,
    val lastAttemptAt: Long
)

data class SubjectProgressSummary(
    val subjectTitle: String,
    val subjectId: String?,
    val quizzesCompleted: Int,
    val totalQuestions: Int,
    val averageMasteryPercent: Int,
    val masteryLevel: MasteryLevel,
    val weakTopicsCount: Int
)

data class RecommendedStudyMaterial(
    val id: String,
    val subjectTitle: String,
    val topic: String,
    val masteryPercent: Int,
    val masteryLevel: MasteryLevel,
    val priorityLabel: String,
    val weaknessReason: String,
    val suggestedActionText: String,
    val matchedSubjectId: String? = null,
    val matchedUnitId: String? = null,
    val matchedUnitTitle: String? = null,
    val matchedTextbookId: String? = null,
    val matchedTextbookTitle: String? = null
)

sealed interface AiStudyPlanUiState {
    data object Idle : AiStudyPlanUiState
    data object Loading : AiStudyPlanUiState
    data class Streaming(val planText: String) : AiStudyPlanUiState
    data class Ready(val planText: String) : AiStudyPlanUiState
    data class Error(val message: String) : AiStudyPlanUiState
}
