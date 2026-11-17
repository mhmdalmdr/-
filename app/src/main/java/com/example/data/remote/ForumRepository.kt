package com.example.data.remote

import com.example.model.ForumPost
import com.example.model.ForumPostStatus
import com.example.model.ForumReply
import com.example.model.TeacherPostReviewStatus
import com.example.model.TeacherReplyBadge
import com.example.util.FirestoreErrorHandler
import com.example.util.OperationType
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ForumRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        const val COLLECTION_FORUM_POSTS = "forum_posts"
        const val SUBCOLLECTION_REPLIES = "replies"

        const val MIN_TITLE_LEN = 4
        const val MAX_TITLE_LEN = 140
        const val MIN_POST_BODY_LEN = 10
        const val MAX_POST_BODY_LEN = 2000
        const val MIN_REPLY_BODY_LEN = 3
        const val MAX_REPLY_BODY_LEN = 1500
        const val MIN_SUBJECT_LEN = 2
        const val MAX_SUBJECT_LEN = 60

        private val VALID_ID_REGEX = Regex("^[a-zA-Z0-9_\\-]{1,128}$")
    }

    private fun requireUid(): String {
        return auth.currentUser?.uid?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("يجب تسجيل الدخول بحساب Google أولاً للمشاركة في المنتدى الطلابي.")
    }

    private fun isValidDocId(id: String): Boolean = VALID_ID_REGEX.matches(id)

    fun observePublicPosts(): Flow<List<ForumPost>> {
        if (auth.currentUser == null) return flowOf(emptyList())
        return db.collection(COLLECTION_FORUM_POSTS)
            .whereEqualTo("visibility", "PUBLIC")
            .snapshots()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { doc ->
                        doc.toObject(ForumPost::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    }
                    .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
    }

    fun observeReplies(postId: String): Flow<List<ForumReply>> {
        if (auth.currentUser == null || !isValidDocId(postId)) return flowOf(emptyList())
        return db.collection(COLLECTION_FORUM_POSTS)
            .document(postId)
            .collection(SUBCOLLECTION_REPLIES)
            .whereEqualTo("postVisibility", "PUBLIC")
            .snapshots()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { doc ->
                        doc.toObject(ForumReply::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    }
                    .sortedBy { it.createdAt ?: Timestamp(0, 0) }
            }
    }

    suspend fun createPost(
        subjectTitle: String,
        title: String,
        body: String,
        authorName: String,
        authorTrack: String
    ): Result<String> {
        val cleanSubject = subjectTitle.trim()
        val cleanTitle = title.trim()
        val cleanBody = body.trim()
        val cleanAuthor = authorName.trim().ifBlank { "طالب الشهادة السودانية" }.take(80)
        val cleanTrack = authorTrack.trim().ifBlank { "القسم العلمي" }.take(40)

        if (cleanSubject.length !in MIN_SUBJECT_LEN..MAX_SUBJECT_LEN) {
            return Result.failure(IllegalArgumentException("يرجى اختيار مادة دراسية صالحة."))
        }
        if (cleanTitle.length !in MIN_TITLE_LEN..MAX_TITLE_LEN) {
            return Result.failure(
                IllegalArgumentException("عنوان الاستفسار يجب أن يكون بين $MIN_TITLE_LEN و$MAX_TITLE_LEN حرفاً.")
            )
        }
        if (cleanBody.length !in MIN_POST_BODY_LEN..MAX_POST_BODY_LEN) {
            return Result.failure(
                IllegalArgumentException("تفاصيل الاستفسار يجب أن تكون بين $MIN_POST_BODY_LEN و$MAX_POST_BODY_LEN حرفاً.")
            )
        }

        var path = COLLECTION_FORUM_POSTS
        return try {
            val uid = requireUid()
            val newDocRef = db.collection(COLLECTION_FORUM_POSTS).document()
            path = newDocRef.path

            val payload = hashMapOf<String, Any>(
                "userId" to uid,
                "authorName" to cleanAuthor,
                "authorTrack" to cleanTrack,
                "subjectTitle" to cleanSubject,
                "title" to cleanTitle,
                "body" to cleanBody,
                "status" to ForumPostStatus.OPEN.wireValue,
                "visibility" to "PUBLIC",
                "repliesCount" to 0L,
                "helpfulCount" to 0L,
                "teacherReviewStatus" to TeacherPostReviewStatus.PENDING.wireValue,
                "teacherReviewNote" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            newDocRef.set(payload).await()
            Result.success(newDocRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.CREATE, path, auth)
            Result.failure(e)
        }
    }

    suspend fun updatePostTeacherReview(
        postId: String,
        reviewStatus: TeacherPostReviewStatus,
        reviewNote: String
    ): Result<Unit> {
        if (!isValidDocId(postId)) {
            return Result.failure(IllegalArgumentException("معرّف المنشور غير صالح."))
        }
        val cleanNote = reviewNote.trim().take(2000)
        val docRef = db.collection(COLLECTION_FORUM_POSTS).document(postId)

        return try {
            requireUid()
            docRef.update(
                mapOf(
                    "teacherReviewStatus" to reviewStatus.wireValue,
                    "teacherReviewNote" to cleanNote,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, docRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun markPostSolved(postId: String): Result<Unit> {
        if (!isValidDocId(postId)) {
            return Result.failure(IllegalArgumentException("معرّف المنشور غير صالح."))
        }
        val docRef = db.collection(COLLECTION_FORUM_POSTS).document(postId)
        return try {
            requireUid()
            docRef.update(
                mapOf(
                    "status" to ForumPostStatus.SOLVED.wireValue,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, docRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun incrementPostHelpful(postId: String): Result<Unit> {
        if (!isValidDocId(postId)) {
            return Result.failure(IllegalArgumentException("معرّف المنشور غير صالح."))
        }
        val docRef = db.collection(COLLECTION_FORUM_POSTS).document(postId)
        return try {
            requireUid()
            docRef.update(
                mapOf(
                    "helpfulCount" to FieldValue.increment(1L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, docRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun deletePost(postId: String): Result<Unit> {
        if (!isValidDocId(postId)) {
            return Result.failure(IllegalArgumentException("معرّف المنشور غير صالح."))
        }
        val docRef = db.collection(COLLECTION_FORUM_POSTS).document(postId)
        return try {
            requireUid()
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.DELETE, docRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun addReplyAndIncrementCount(
        postId: String,
        body: String,
        authorName: String
    ): Result<String> {
        if (!isValidDocId(postId)) {
            return Result.failure(IllegalArgumentException("معرّف المنشور غير صالح."))
        }
        val cleanBody = body.trim()
        if (cleanBody.length !in MIN_REPLY_BODY_LEN..MAX_REPLY_BODY_LEN) {
            return Result.failure(
                IllegalArgumentException("الرد يجب أن يكون بين $MIN_REPLY_BODY_LEN و$MAX_REPLY_BODY_LEN حرفاً.")
            )
        }
        val cleanAuthor = authorName.trim().ifBlank { "طالب الشهادة السودانية" }.take(80)

        val postRef = db.collection(COLLECTION_FORUM_POSTS).document(postId)
        val replyRef = postRef.collection(SUBCOLLECTION_REPLIES).document()

        return try {
            val uid = requireUid()
            val replyPayload = hashMapOf<String, Any>(
                "postId" to postId,
                "userId" to uid,
                "authorName" to cleanAuthor,
                "body" to cleanBody,
                "postVisibility" to "PUBLIC",
                "teacherBadge" to TeacherReplyBadge.NONE.wireValue,
                "teacherFeedback" to "",
                "markedHelpfulByAuthor" to false,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            val batch = db.batch()
            batch.set(replyRef, replyPayload)
            batch.update(
                postRef,
                mapOf(
                    "repliesCount" to FieldValue.increment(1L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(replyRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.WRITE, replyRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun updateReplyTeacherFeedback(
        postId: String,
        replyId: String,
        badge: TeacherReplyBadge,
        feedback: String
    ): Result<Unit> {
        if (!isValidDocId(postId) || !isValidDocId(replyId)) {
            return Result.failure(IllegalArgumentException("معرّف الرد أو المنشور غير صالح."))
        }
        val cleanFeedback = feedback.trim().take(1500)
        val replyRef = db.collection(COLLECTION_FORUM_POSTS)
            .document(postId)
            .collection(SUBCOLLECTION_REPLIES)
            .document(replyId)

        return try {
            requireUid()
            replyRef.update(
                mapOf(
                    "teacherBadge" to badge.wireValue,
                    "teacherFeedback" to cleanFeedback,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, replyRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun toggleReplyMarkedHelpful(
        postId: String,
        replyId: String,
        markedHelpful: Boolean
    ): Result<Unit> {
        if (!isValidDocId(postId) || !isValidDocId(replyId)) {
            return Result.failure(IllegalArgumentException("معرّف الرد أو المنشور غير صالح."))
        }
        val replyRef = db.collection(COLLECTION_FORUM_POSTS)
            .document(postId)
            .collection(SUBCOLLECTION_REPLIES)
            .document(replyId)

        return try {
            requireUid()
            replyRef.update(
                mapOf(
                    "markedHelpfulByAuthor" to markedHelpful,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, replyRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun deleteReply(postId: String, replyId: String): Result<Unit> {
        if (!isValidDocId(postId) || !isValidDocId(replyId)) {
            return Result.failure(IllegalArgumentException("معرّف الرد أو المنشور غير صالح."))
        }
        val replyRef = db.collection(COLLECTION_FORUM_POSTS)
            .document(postId)
            .collection(SUBCOLLECTION_REPLIES)
            .document(replyId)

        return try {
            requireUid()
            replyRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.DELETE, replyRef.path, auth)
            Result.failure(e)
        }
    }
}
