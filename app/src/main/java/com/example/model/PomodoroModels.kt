package com.example.model

enum class PomodoroPhase(
    val titleAr: String,
    val shortTitleAr: String,
    val subtitleAr: String
) {
    FOCUS(
        titleAr = "جلسة تركيز عميق",
        shortTitleAr = "تركيز",
        subtitleAr = "أبعد المشتتات وركز على هدفك الآن"
    ),
    SHORT_BREAK(
        titleAr = "استراحة قصيرة",
        shortTitleAr = "راحة قصيرة",
        subtitleAr = "أرح عينيك واشرب كوب ماء لتجديد نشاطك"
    ),
    LONG_BREAK(
        titleAr = "استراحة طويلة",
        shortTitleAr = "راحة طويلة",
        subtitleAr = "أتممت 4 جولات تركيز! استراحة مستحقة"
    )
}

enum class PomodoroPreset(
    val titleAr: String,
    val badgeText: String,
    val focusMinutes: Int,
    val shortBreakMinutes: Int,
    val longBreakMinutes: Int
) {
    CLASSIC("بومودورو الكلاسيكي", "25 / 5", 25, 5, 15),
    DEEP_STUDY("مذاكرة مكثفة", "45 / 10", 45, 10, 20),
    QUICK_REVIEW("مراجعة سريعة", "15 / 3", 15, 3, 10)
}

data class PomodoroUiState(
    val phase: PomodoroPhase = PomodoroPhase.FOCUS,
    val isRunning: Boolean = false,
    val remainingSeconds: Int = 25 * 60,
    val totalPhaseSeconds: Int = 25 * 60,
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val cycleFocusCount: Int = 0,
    val completedSessionsTotal: Int = 0,
    val totalFocusMinutes: Int = 0,
    val selectedSubjectTitle: String = "عام — الشهادة السودانية",
    val sessionGoal: String = "",
    val soundEnabled: Boolean = true,
    val autoStartNext: Boolean = false,
    val lastCompletedMessage: String? = null
) {
    val progressFraction: Float
        get() = if (totalPhaseSeconds <= 0) 0f
        else ((totalPhaseSeconds - remainingSeconds).toFloat() / totalPhaseSeconds.toFloat()).coerceIn(0f, 1f)

    val formattedTime: String
        get() {
            val safeSeconds = remainingSeconds.coerceAtLeast(0)
            val mins = safeSeconds / 60
            val secs = safeSeconds % 60
            return String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
        }
}
