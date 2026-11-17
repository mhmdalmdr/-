package com.example.util

import com.example.model.DailyStudyStat
import com.example.model.PomodoroSessionLog
import com.example.model.StatsTimeFilter
import com.example.model.SubjectStudyStat
import java.util.Calendar
import java.util.Locale

object StudyStatsAggregator {

    fun filterByTime(
        sessions: List<PomodoroSessionLog>,
        filter: StatsTimeFilter,
        nowMillis: Long = System.currentTimeMillis()
    ): List<PomodoroSessionLog> {
        if (filter == StatsTimeFilter.ALL) return sessions

        val startOfToday = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val cutoff = when (filter) {
            StatsTimeFilter.TODAY -> startOfToday
            StatsTimeFilter.LAST_7_DAYS -> startOfToday - (6L * 24L * 60L * 60L * 1000L)
            StatsTimeFilter.ALL -> 0L
        }
        return sessions.filter { it.completedAt >= cutoff }
    }

    fun aggregateBySubject(sessions: List<PomodoroSessionLog>): List<SubjectStudyStat> {
        if (sessions.isEmpty()) return emptyList()
        val totalMinutesAll = sessions.sumOf { it.durationMinutes }.coerceAtLeast(1)

        return sessions
            .groupBy { it.subjectTitle.ifBlank { "عام — الشهادة السودانية" } }
            .map { (subject, logs) ->
                val minutes = logs.sumOf { it.durationMinutes }
                SubjectStudyStat(
                    subjectTitle = subject,
                    totalMinutes = minutes,
                    sessionsCount = logs.size,
                    shareFraction = minutes.toFloat() / totalMinutesAll.toFloat()
                )
            }
            .sortedByDescending { it.totalMinutes }
    }

    fun aggregateLast7Days(
        sessions: List<PomodoroSessionLog>,
        nowMillis: Long = System.currentTimeMillis()
    ): List<DailyStudyStat> {
        val result = mutableListOf<DailyStudyStat>()
        val cal = Calendar.getInstance()

        for (daysAgo in 6 downTo 0) {
            cal.timeInMillis = nowMillis
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val dayStart = cal.timeInMillis
            val dayEnd = dayStart + 24L * 60L * 60L * 1000L

            val dayMinutes = sessions
                .filter { it.completedAt in dayStart until dayEnd }
                .sumOf { it.durationMinutes }

            val dayName = arabicShortDayName(cal.get(Calendar.DAY_OF_WEEK))
            result.add(DailyStudyStat(dayLabel = dayName, totalMinutes = dayMinutes))
        }
        return result
    }

    fun formatDurationAr(totalMinutes: Int): String {
        val safe = totalMinutes.coerceAtLeast(0)
        val hours = safe / 60
        val mins = safe % 60
        return when {
            hours == 0 -> "$mins دقيقة"
            mins == 0 -> "$hours ساعة"
            else -> "$hours س $mins د"
        }
    }

    fun formatHoursDecimal(totalMinutes: Int): String {
        val hours = totalMinutes.coerceAtLeast(0) / 60.0
        return String.format(Locale.US, "%.1f", hours)
    }

    fun shortSubjectLabel(title: String): String {
        val cleaned = title
            .replace("عام — الشهادة السودانية", "عام")
            .replace("الرياضيات المتخصصة", "رياضيات م")
            .replace("الرياضيات الأساسية", "رياضيات أ")
            .replace("النحو والصرف والبلاغة", "النحو")
            .replace("الأدب والنصوص", "الأدب")
            .replace("اللغة الإنجليزية (Spine 6)", "إنجليزي")
            .replace("التربية الإسلامية", "إسلامية")
            .replace("الدراسات الإسلامية", "د. إسلامية")
            .replace("الجغرافيا والدراسات البيئية", "الجغرافيا")
            .replace("العلوم التجارية والمحاسبة", "التجارية")
            .replace("العلوم الهندسية", "الهندسية")
            .replace("علوم الحاسوب", "الحاسوب")
        return if (cleaned.length > 10) cleaned.take(9) + "…" else cleaned
    }

    private fun arabicShortDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SATURDAY -> "السبت"
            Calendar.SUNDAY -> "الأحد"
            Calendar.MONDAY -> "الاثنين"
            Calendar.TUESDAY -> "الثلاثاء"
            Calendar.WEDNESDAY -> "الأربعاء"
            Calendar.THURSDAY -> "الخميس"
            Calendar.FRIDAY -> "الجمعة"
            else -> ""
        }
    }
}
