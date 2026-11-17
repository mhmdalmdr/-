package com.example.util

import com.example.model.TeacherPostReviewStatus
import com.example.model.TeacherReplyBadge
import org.json.JSONObject

data class AiPostSupervisionResult(
    val reviewStatus: TeacherPostReviewStatus,
    val reviewNote: String,
    val keyConcept: String
)

data class AiReplySupervisionResult(
    val badge: TeacherReplyBadge,
    val feedback: String
)

object ForumAiSupervisorParser {

    fun parsePostReview(rawJson: String): AiPostSupervisionResult {
        val json = JSONObject(rawJson)
        val rawStatus = json.optString("reviewStatus").trim().uppercase()
        val status = when (rawStatus) {
            TeacherPostReviewStatus.APPROVED.wireValue -> TeacherPostReviewStatus.APPROVED
            else -> TeacherPostReviewStatus.GUIDED
        }
        val keyConcept = json.optString("keyConcept").trim().take(120)
        val note = json.optString("reviewNote").trim()
        require(note.isNotBlank()) { "لم يولّد المعلم الذكي ملاحظة إشرافية صالحة." }

        val formattedNote = if (keyConcept.isNotBlank() && !note.contains(keyConcept)) {
            "المفهوم الأساسي: $keyConcept\n$note"
        } else {
            note
        }.take(1900)

        return AiPostSupervisionResult(
            reviewStatus = status,
            reviewNote = formattedNote,
            keyConcept = keyConcept
        )
    }

    fun parseReplyEvaluation(rawJson: String): AiReplySupervisionResult {
        val json = JSONObject(rawJson)
        val rawBadge = json.optString("badge").trim().uppercase()
        val badge = when (rawBadge) {
            TeacherReplyBadge.VERIFIED.wireValue -> TeacherReplyBadge.VERIFIED
            TeacherReplyBadge.CONTAINS_ERROR.wireValue -> TeacherReplyBadge.CONTAINS_ERROR
            else -> TeacherReplyBadge.NEEDS_HINT
        }
        val feedback = json.optString("feedback").trim().take(1400)
        require(feedback.isNotBlank()) { "لم يولّد المعلم الذكي تقييماً صالحاً للرد." }

        return AiReplySupervisionResult(
            badge = badge,
            feedback = feedback
        )
    }
}
