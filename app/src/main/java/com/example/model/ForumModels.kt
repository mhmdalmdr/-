package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class ForumPostStatus(val wireValue: String, val labelAr: String) {
    OPEN("OPEN", "مفتوح للنقاش"),
    SOLVED("SOLVED", "تم الحل");

    companion object {
        fun fromWire(raw: String?): ForumPostStatus =
            entries.firstOrNull { it.wireValue.equals(raw, ignoreCase = true) } ?: OPEN
    }
}

enum class TeacherPostReviewStatus(val wireValue: String, val labelAr: String) {
    PENDING("PENDING", "بانتظار إشراف المعلم الذكي"),
    GUIDED("GUIDED", "توجيه إرشادي من المعلم"),
    APPROVED("APPROVED", "معتمد ومصنّف من المعلم");

    companion object {
        fun fromWire(raw: String?): TeacherPostReviewStatus =
            entries.firstOrNull { it.wireValue.equals(raw, ignoreCase = true) } ?: PENDING
    }
}

enum class TeacherReplyBadge(val wireValue: String, val labelAr: String) {
    NONE("NONE", "قيد المراجعة"),
    VERIFIED("VERIFIED", "إجابة موثّقة من المعلم"),
    NEEDS_HINT("NEEDS_HINT", "إجابة جيدة تحتاج لتوضيح"),
    CONTAINS_ERROR("CONTAINS_ERROR", "تنبيه تصحيحي من المعلم");

    companion object {
        fun fromWire(raw: String?): TeacherReplyBadge =
            entries.firstOrNull { it.wireValue.equals(raw, ignoreCase = true) } ?: NONE
    }
}

@Keep
@IgnoreExtraProperties
data class ForumPost(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorTrack: String = "",
    val subjectTitle: String = "",
    val title: String = "",
    val body: String = "",
    val status: String = ForumPostStatus.OPEN.wireValue,
    val visibility: String = "PUBLIC",
    val repliesCount: Long = 0L,
    val helpfulCount: Long = 0L,
    val teacherReviewStatus: String = TeacherPostReviewStatus.PENDING.wireValue,
    val teacherReviewNote: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    val parsedStatus: ForumPostStatus
        get() = ForumPostStatus.fromWire(status)

    val parsedTeacherReviewStatus: TeacherPostReviewStatus
        get() = TeacherPostReviewStatus.fromWire(teacherReviewStatus)
}

@Keep
@IgnoreExtraProperties
data class ForumReply(
    @DocumentId
    val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val authorName: String = "",
    val body: String = "",
    val postVisibility: String = "PUBLIC",
    val teacherBadge: String = TeacherReplyBadge.NONE.wireValue,
    val teacherFeedback: String = "",
    val markedHelpfulByAuthor: Boolean = false,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    val parsedTeacherBadge: TeacherReplyBadge
        get() = TeacherReplyBadge.fromWire(teacherBadge)
}

sealed interface ForumUiState<out T> {
    data object Loading : ForumUiState<Nothing>
    data class Success<T>(val data: T) : ForumUiState<T>
    data class Error(val message: String) : ForumUiState<Nothing>
}

sealed interface ForumMutationState {
    data object Idle : ForumMutationState
    data object Submitting : ForumMutationState
    data class Error(val message: String) : ForumMutationState
}
