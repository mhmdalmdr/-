package com.example.util

import com.example.model.ExamTip
import com.example.model.TeacherTipCategory
import org.json.JSONObject
import java.util.UUID

object TeacherTipsJsonParser {

    private val allowedCategories = setOf(
        TeacherTipCategory.EXAM_ANXIETY.titleAr,
        TeacherTipCategory.SCIENCE_STRATEGIES.titleAr,
        TeacherTipCategory.LITERARY_STRATEGIES.titleAr,
        TeacherTipCategory.TIME_MANAGEMENT.titleAr,
        TeacherTipCategory.EXAM_HALL.titleAr
    )

    fun parse(
        rawJson: String,
        fallbackCategory: TeacherTipCategory = TeacherTipCategory.ALL,
        defaultStreamTag: String = "عام"
    ): List<ExamTip> {
        val cleaned = rawJson.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val root = JSONObject(cleaned)
        val tipsArray = root.getJSONArray("tips")
        val parsed = mutableListOf<ExamTip>()

        for (i in 0 until tipsArray.length()) {
            val item = tipsArray.optJSONObject(i) ?: continue
            val title = item.optString("title").trim()
            val content = item.optString("content").trim()
            val importantRule = item.optString("importantRule").trim()
            if (title.isBlank() || content.isBlank()) continue

            val rawCategory = item.optString("category").trim()
            val normalizedCategory = normalizeCategory(rawCategory, fallbackCategory)

            val stepsArray = item.optJSONArray("actionSteps")
            val steps = mutableListOf<String>()
            if (stepsArray != null) {
                for (j in 0 until stepsArray.length()) {
                    val step = stepsArray.optString(j).trim()
                    if (step.isNotBlank()) {
                        steps.add(step)
                    }
                }
            }

            val streamTag = item.optString("streamTag").trim().ifBlank { defaultStreamTag }

            parsed.add(
                ExamTip(
                    id = "ai_tip_${UUID.randomUUID()}",
                    title = title,
                    category = normalizedCategory,
                    content = content,
                    importantRule = importantRule.ifBlank {
                        "المثابرة الهادئة والتركيز على الفهم قبل الحفظ هما سر التفوق."
                    },
                    actionSteps = steps,
                    isAiGenerated = true,
                    streamTag = streamTag
                )
            )
        }

        require(parsed.isNotEmpty()) {
            "لم يولّد المعلم الذكي نصائح جديدة صالحة. يرجى المحاولة مرة أخرى."
        }

        return parsed
    }

    private fun normalizeCategory(raw: String, fallback: TeacherTipCategory): String {
        if (raw in allowedCategories) return raw
        return when {
            raw.contains("قلق") || raw.contains("توتر") || raw.contains("نفسي") ->
                TeacherTipCategory.EXAM_ANXIETY.titleAr
            raw.contains("علم") || raw.contains("فيزياء") || raw.contains("رياض") || raw.contains("كيمياء") || raw.contains("أحياء") ->
                TeacherTipCategory.SCIENCE_STRATEGIES.titleAr
            raw.contains("أدب") || raw.contains("تاريخ") || raw.contains("جغراف") || raw.contains("عرب") ->
                TeacherTipCategory.LITERARY_STRATEGIES.titleAr
            raw.contains("وقت") || raw.contains("جدول") || raw.contains("تنظيم") ->
                TeacherTipCategory.TIME_MANAGEMENT.titleAr
            raw.contains("قاعة") || raw.contains("امتحان") || raw.contains("لجنة") ->
                TeacherTipCategory.EXAM_HALL.titleAr
            fallback != TeacherTipCategory.ALL -> fallback.titleAr
            else -> TeacherTipCategory.SCIENCE_STRATEGIES.titleAr
        }
    }
}
