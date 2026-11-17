package com.example.model

enum class QuizDifficulty(
    val titleAr: String,
    val badgeAr: String,
    val promptDescription: String
) {
    FOUNDATION(
        titleAr = "تأسيسي",
        badgeAr = "تثبيت المفاهيم",
        promptDescription = "أسئلة تأسيسية واضحة تركز على الفهم المباشر للمفاهيم والقوانين الأساسية"
    ),
    EXAM_LEVEL(
        titleAr = "مستوى الشهادة",
        badgeAr = "قياسي",
        promptDescription = "أسئلة متوازنة تحاكي نمط ومستوى امتحانات الشهادة الثانوية السودانية"
    ),
    CHALLENGE(
        titleAr = "تحدي المتفوقين",
        badgeAr = "تحليلي",
        promptDescription = "أسئلة استنتاجية عميقة تقيس الربط بين المفاهيم وتجنب الأخطاء الشائعة"
    )
}

sealed interface QuizGeneratorUiState {
    data object Idle : QuizGeneratorUiState
    data object Generating : QuizGeneratorUiState
    data class Ready(val subjectTitle: String, val topic: String) : QuizGeneratorUiState
    data class Error(val message: String) : QuizGeneratorUiState
}

sealed interface GuidedHintUiState {
    data object Idle : GuidedHintUiState
    data object Loading : GuidedHintUiState
    data class Streaming(val text: String) : GuidedHintUiState
    data class Ready(val text: String) : GuidedHintUiState
    data class Error(val message: String) : GuidedHintUiState
}

data class QuizAttemptUiState(
    val attemptedWrongIndices: Set<Int> = emptySet(),
    val isSolvedOrRevealed: Boolean = false,
    val wasRevealedWithoutSolving: Boolean = false,
    val activeGuidanceText: String? = null,
    val isGuidanceCorrect: Boolean = false,
    val hasRequestedPreHint: Boolean = false,
    val firstTryCorrectCount: Int = 0,
    val guidedCorrectCount: Int = 0,
    val revealedCount: Int = 0,
    val totalWrongAttempts: Int = 0,
    val hasRecordedResult: Boolean = false,
    val isQuizFinished: Boolean = false
)
