package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.ForumRepository
import com.example.model.ForumMutationState
import com.example.model.ForumPost
import com.example.model.ForumPostStatus
import com.example.model.ForumReply
import com.example.model.ForumUiState
import com.example.model.TeacherPostReviewStatus
import com.example.model.TeacherReplyBadge
import com.example.util.FirestoreErrorHandler
import com.example.util.ForumAiSupervisorParser
import com.example.util.OperationType
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "ForumViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class ForumViewModel(
    private val repository: ForumRepository,
    val currentUserId: String,
    val currentUserName: String,
    val currentUserTrack: String
) : ViewModel() {

    private val _selectedSubjectFilter = MutableStateFlow("الكل")
    val selectedSubjectFilter: StateFlow<String> = _selectedSubjectFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<ForumPostStatus?>(null)
    val selectedStatusFilter: StateFlow<ForumPostStatus?> = _selectedStatusFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPostId = MutableStateFlow<String?>(null)
    val selectedPostId: StateFlow<String?> = _selectedPostId.asStateFlow()

    private val _postMutationState = MutableStateFlow<ForumMutationState>(ForumMutationState.Idle)
    val postMutationState: StateFlow<ForumMutationState> = _postMutationState.asStateFlow()

    private val _replyMutationState = MutableStateFlow<ForumMutationState>(ForumMutationState.Idle)
    val replyMutationState: StateFlow<ForumMutationState> = _replyMutationState.asStateFlow()

    private val _aiSupervisingPostIds = MutableStateFlow<Set<String>>(emptySet())
    val aiSupervisingPostIds: StateFlow<Set<String>> = _aiSupervisingPostIds.asStateFlow()

    private val _aiSupervisingReplyIds = MutableStateFlow<Set<String>>(emptySet())
    val aiSupervisingReplyIds: StateFlow<Set<String>> = _aiSupervisingReplyIds.asStateFlow()

    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    private val _errorBannerMessage = MutableStateFlow<String?>(null)
    val errorBannerMessage: StateFlow<String?> = _errorBannerMessage.asStateFlow()

    val postsState: StateFlow<ForumUiState<List<ForumPost>>> =
        repository.observePublicPosts()
            .map<List<ForumPost>, ForumUiState<List<ForumPost>>> { ForumUiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (error is CancellationException && firestoreError == null) {
                    throw error
                }
                FirestoreErrorHandler.handleFirestoreError(
                    exception = error,
                    operationType = OperationType.LIST,
                    path = ForumRepository.COLLECTION_FORUM_POSTS
                )
                emit(ForumUiState.Error(FirestoreErrorHandler.toFriendlyArabicMessage(error)))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ForumUiState.Loading
            )

    val filteredPostsState: StateFlow<ForumUiState<List<ForumPost>>> = combine(
        postsState,
        _selectedSubjectFilter,
        _selectedStatusFilter,
        _searchQuery
    ) { state, subject, status, query ->
        when (state) {
            is ForumUiState.Loading -> ForumUiState.Loading
            is ForumUiState.Error -> state
            is ForumUiState.Success -> {
                val trimmedQuery = query.trim()
                val filtered = state.data.filter { post ->
                    val matchesSubject = subject == "الكل" || post.subjectTitle == subject
                    val matchesStatus = status == null || post.parsedStatus == status
                    val matchesQuery = trimmedQuery.isEmpty() ||
                        post.title.contains(trimmedQuery, ignoreCase = true) ||
                        post.body.contains(trimmedQuery, ignoreCase = true) ||
                        post.subjectTitle.contains(trimmedQuery, ignoreCase = true) ||
                        post.authorName.contains(trimmedQuery, ignoreCase = true)
                    matchesSubject && matchesStatus && matchesQuery
                }
                ForumUiState.Success(filtered)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ForumUiState.Loading
    )

    val selectedPost: StateFlow<ForumPost?> = combine(postsState, _selectedPostId) { state, postId ->
        if (postId == null || state !is ForumUiState.Success) null
        else state.data.firstOrNull { it.id == postId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val repliesState: StateFlow<ForumUiState<List<ForumReply>>> = _selectedPostId
        .flatMapLatest { postId ->
            if (postId.isNullOrBlank()) {
                flowOf<ForumUiState<List<ForumReply>>>(ForumUiState.Success(emptyList()))
            } else {
                repository.observeReplies(postId)
                    .map<List<ForumReply>, ForumUiState<List<ForumReply>>> { ForumUiState.Success(it) }
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (error is CancellationException && firestoreError == null) {
                            throw error
                        }
                        FirestoreErrorHandler.handleFirestoreError(
                            exception = error,
                            operationType = OperationType.LIST,
                            path = "${ForumRepository.COLLECTION_FORUM_POSTS}/$postId/${ForumRepository.SUBCOLLECTION_REPLIES}"
                        )
                        emit(ForumUiState.Error(FirestoreErrorHandler.toFriendlyArabicMessage(error)))
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ForumUiState.Success(emptyList())
        )

    private val postSupervisionSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "reviewStatus" to Schema.string(
                    description = "Must be strictly APPROVED if question is clear or GUIDED if it benefits from Socratic direction"
                ),
                "keyConcept" to Schema.string(
                    description = "Short Arabic curriculum concept name related to the student question"
                ),
                "reviewNote" to Schema.string(
                    description = "Supportive Socratic guidance in Arabic from the AI Teacher without giving away the final raw answer"
                )
            )
        )
    }

    private val replySupervisionSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "badge" to Schema.string(
                    description = "Must be strictly VERIFIED if scientifically accurate, NEEDS_HINT if incomplete/partially right, or CONTAINS_ERROR if scientifically wrong"
                ),
                "feedback" to Schema.string(
                    description = "Concise Arabic AI Teacher supervision note evaluating the peer explanation respectfully and constructively"
                )
            )
        )
    }

    private val postSupervisorModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.4f
                maxOutputTokens = 700
                responseMimeType = "application/json"
                responseSchema = postSupervisionSchema
            },
            systemInstruction = content {
                text(
                    "أنت المعلم الذكي المشرف على المنتدى الطلابي لطلاب الشهادة الثانوية السودانية. " +
                        "عندما يطرح طالب استفساراً دراسياً، قدّم إشرافاً تربوياً سقراطياً موجزاً باللغة العربية: " +
                        "حدّد المفهوم العلمي/الأدبي الأساسي، ووجّه الطلاب المشاركين إلى خطوة التفكير الأولى دون حرق الإجابة النهائية."
                )
            }
        )
    }

    private val postSupervisorFallbackModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            generationConfig = generationConfig {
                temperature = 0.4f
                maxOutputTokens = 700
                responseMimeType = "application/json"
                responseSchema = postSupervisionSchema
            },
            systemInstruction = content {
                text("أنت المعلم الذكي المشرف على المنتدى الطلابي للشهادة السودانية. أجب بصيغة JSON مطابقة للمطلوب باللغة العربية.")
            }
        )
    }

    private val replySupervisorModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.35f
                maxOutputTokens = 600
                responseMimeType = "application/json"
                responseSchema = replySupervisionSchema
            },
            systemInstruction = content {
                text(
                    "أنت المعلم الذكي المشرف على ردود الطلاب في المنتدى الدراسي للشهادة السودانية. " +
                        "مهمتك تدقيق مساعدة الطالب لزميله: إذا كانت صحيحة ودقيقة فاختر VERIFIED وأثنِ عليها مع إضافة لمسة تثبيت، " +
                        "وإذا كانت ناقصة فاختر NEEDS_HINT وذكّر بنقطة التوضيح، وإذا احتوت على خطأ علمي أو منهجي فاختر CONTAINS_ERROR وصحّح المسار بلطف وتوجيه تربوي."
                )
            }
        )
    }

    private val replySupervisorFallbackModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            generationConfig = generationConfig {
                temperature = 0.35f
                maxOutputTokens = 600
                responseMimeType = "application/json"
                responseSchema = replySupervisionSchema
            },
            systemInstruction = content {
                text("أنت المعلم الذكي المدقق لردود المنتدى الطلابي للشهادة السودانية. أجب بصيغة JSON فقط.")
            }
        )
    }

    fun selectSubjectFilter(subject: String) {
        _selectedSubjectFilter.value = subject
    }

    fun selectStatusFilter(status: ForumPostStatus?) {
        _selectedStatusFilter.value = status
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openPostThread(postId: String) {
        _selectedPostId.value = postId
    }

    fun closePostThread() {
        _selectedPostId.value = null
        _replyMutationState.value = ForumMutationState.Idle
    }

    fun clearBanners() {
        _bannerMessage.value = null
        _errorBannerMessage.value = null
    }

    fun resetPostMutationError() {
        if (_postMutationState.value is ForumMutationState.Error) {
            _postMutationState.value = ForumMutationState.Idle
        }
    }

    fun resetReplyMutationError() {
        if (_replyMutationState.value is ForumMutationState.Error) {
            _replyMutationState.value = ForumMutationState.Idle
        }
    }

    fun submitNewPost(
        subjectTitle: String,
        title: String,
        body: String,
        onCreated: (String) -> Unit = {}
    ) {
        if (_postMutationState.value is ForumMutationState.Submitting) return

        val cleanTitle = title.trim()
        val cleanBody = body.trim()
        if (cleanTitle.length !in ForumRepository.MIN_TITLE_LEN..ForumRepository.MAX_TITLE_LEN) {
            _postMutationState.value = ForumMutationState.Error(
                "عنوان الاستفسار يجب أن يكون بين ${ForumRepository.MIN_TITLE_LEN} و${ForumRepository.MAX_TITLE_LEN} حرفاً."
            )
            return
        }
        if (cleanBody.length !in ForumRepository.MIN_POST_BODY_LEN..ForumRepository.MAX_POST_BODY_LEN) {
            _postMutationState.value = ForumMutationState.Error(
                "تفاصيل الاستفسار يجب أن تكون بين ${ForumRepository.MIN_POST_BODY_LEN} و${ForumRepository.MAX_POST_BODY_LEN} حرفاً."
            )
            return
        }

        _postMutationState.value = ForumMutationState.Submitting
        viewModelScope.launch {
            val result = repository.createPost(
                subjectTitle = subjectTitle,
                title = cleanTitle,
                body = cleanBody,
                authorName = currentUserName,
                authorTrack = currentUserTrack
            )
            result.fold(
                onSuccess = { newPostId ->
                    _postMutationState.value = ForumMutationState.Idle
                    _bannerMessage.value = "تم نشر استفسارك بنجاح، ويقوم المعلم الذكي الآن بإضافة التوجيه الإشرافي..."
                    onCreated(newPostId)
                    launchPostSupervision(newPostId, subjectTitle, cleanTitle, cleanBody)
                },
                onFailure = { error ->
                    _postMutationState.value = ForumMutationState.Error(
                        FirestoreErrorHandler.toFriendlyArabicMessage(error)
                    )
                }
            )
        }
    }

    fun requestPostAiReview(post: ForumPost) {
        if (post.userId != currentUserId || post.parsedStatus != ForumPostStatus.OPEN) return
        launchPostSupervision(post.id, post.subjectTitle, post.title, post.body)
    }

    private fun launchPostSupervision(
        postId: String,
        subjectTitle: String,
        title: String,
        body: String
    ) {
        if (_aiSupervisingPostIds.value.contains(postId)) return
        _aiSupervisingPostIds.update { it + postId }

        viewModelScope.launch {
            try {
                val prompt = """
                    استفسار طالب جديد في المنتدى الطلابي للشهادة السودانية:
                    المادة: $subjectTitle
                    عنوان الاستفسار: $title
                    التفاصيل: $body
                    
                    اكتب إشراف المعلم الذكي بصيغة JSON:
                    - reviewStatus: اختر APPROVED إذا كان السؤال واضحاً ومصاغاً بدقة، أو GUIDED لتقديم توجيه مبدئي.
                    - keyConcept: اسم الدرس أو القانون المرتبط بالسؤال.
                    - reviewNote: توجيه تربوي سقراطي يشجع الزملاء على النقاش ويلمح لخطوة البداية دون كشف الحل الكامل.
                """.trimIndent()

                val rawJson = withTimeout(60_000L) {
                    runCatching { postSupervisorModel.generateContent(prompt).text }
                        .getOrNull()
                        ?.takeIf { it.isNotBlank() }
                        ?: postSupervisorFallbackModel.generateContent(prompt).text
                } ?: throw IllegalStateException("لم يرد نموذج الإشراف الذكي بنص JSON.")

                val parsed = ForumAiSupervisorParser.parsePostReview(rawJson)
                val updateResult = repository.updatePostTeacherReview(
                    postId = postId,
                    reviewStatus = parsed.reviewStatus,
                    reviewNote = parsed.reviewNote
                )
                updateResult.onFailure { error ->
                    _errorBannerMessage.value = FirestoreErrorHandler.toFriendlyArabicMessage(error)
                }
            } catch (e: TimeoutCancellationException) {
                Log.w(TAG, "AI post supervision timed out for $postId", e)
                _errorBannerMessage.value = "استغرق المعلم الذكي وقتاً أطول لمراجعة المنشور. يمكنك الضغط على «تحديث إشراف المعلم» لاحقاً."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI post supervision failed for $postId", e)
                _errorBannerMessage.value = "تعذر تشغيل إشراف المعلم الذكي تلقائياً لهذا المنشور: ${e.localizedMessage ?: "خطأ اتصال"}"
            } finally {
                _aiSupervisingPostIds.update { it - postId }
            }
        }
    }

    fun submitReply(post: ForumPost, body: String, onSubmitted: () -> Unit = {}) {
        if (_replyMutationState.value is ForumMutationState.Submitting) return
        val cleanBody = body.trim()
        if (cleanBody.length !in ForumRepository.MIN_REPLY_BODY_LEN..ForumRepository.MAX_REPLY_BODY_LEN) {
            _replyMutationState.value = ForumMutationState.Error(
                "الرد يجب أن يكون بين ${ForumRepository.MIN_REPLY_BODY_LEN} و${ForumRepository.MAX_REPLY_BODY_LEN} حرفاً."
            )
            return
        }

        _replyMutationState.value = ForumMutationState.Submitting
        viewModelScope.launch {
            val result = repository.addReplyAndIncrementCount(
                postId = post.id,
                body = cleanBody,
                authorName = currentUserName
            )
            result.fold(
                onSuccess = { replyId ->
                    _replyMutationState.value = ForumMutationState.Idle
                    _bannerMessage.value = "شكراً لمساعدتك لزميلك! يراجع المعلم الذكي دقة الرد الآن..."
                    onSubmitted()
                    launchReplySupervision(post, replyId, cleanBody)
                },
                onFailure = { error ->
                    _replyMutationState.value = ForumMutationState.Error(
                        FirestoreErrorHandler.toFriendlyArabicMessage(error)
                    )
                }
            )
        }
    }

    fun requestReplyAiCheck(post: ForumPost, reply: ForumReply) {
        if (reply.userId != currentUserId) return
        launchReplySupervision(post, reply.id, reply.body)
    }

    private fun launchReplySupervision(post: ForumPost, replyId: String, replyBody: String) {
        if (_aiSupervisingReplyIds.value.contains(replyId)) return
        _aiSupervisingReplyIds.update { it + replyId }

        viewModelScope.launch {
            try {
                val prompt = """
                    المادة: ${post.subjectTitle}
                    استفسار الطالب الأصلي (${post.title}):
                    ${post.body}
                    
                    رد الطالب المساعِد:
                    $replyBody
                    
                    دقّق هذا الرد كمعلم خبير بالشهادة السودانية وأرجع JSON:
                    - badge: واحدة فقط من VERIFIED أو NEEDS_HINT أو CONTAINS_ERROR
                    - feedback: تعليق تربوي مختصر وواضح يقيّم صحة الرد أو يصحح أي خلل علمي فيه بأسلوب مشجع.
                """.trimIndent()

                val rawJson = withTimeout(60_000L) {
                    runCatching { replySupervisorModel.generateContent(prompt).text }
                        .getOrNull()
                        ?.takeIf { it.isNotBlank() }
                        ?: replySupervisorFallbackModel.generateContent(prompt).text
                } ?: throw IllegalStateException("لم يرد نموذج تدقيق الردود بنص JSON.")

                val parsed = ForumAiSupervisorParser.parseReplyEvaluation(rawJson)
                val updateResult = repository.updateReplyTeacherFeedback(
                    postId = post.id,
                    replyId = replyId,
                    badge = parsed.badge,
                    feedback = parsed.feedback
                )
                updateResult.onFailure { error ->
                    _errorBannerMessage.value = FirestoreErrorHandler.toFriendlyArabicMessage(error)
                }
            } catch (e: TimeoutCancellationException) {
                Log.w(TAG, "AI reply supervision timed out for $replyId", e)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI reply supervision failed for $replyId", e)
                _errorBannerMessage.value = "تعذر إتمام تدقيق المعلم الذكي للرد: ${e.localizedMessage ?: "خطأ اتصال"}"
            } finally {
                _aiSupervisingReplyIds.update { it - replyId }
            }
        }
    }

    fun markPostAsSolved(post: ForumPost) {
        if (post.userId != currentUserId || post.parsedStatus == ForumPostStatus.SOLVED) return
        viewModelScope.launch {
            repository.markPostSolved(post.id).fold(
                onSuccess = {
                    _bannerMessage.value = "تم تعليم الاستفسار كـ «تم الحل». نسأل الله لك دوام التفوق!"
                },
                onFailure = { error ->
                    _errorBannerMessage.value = FirestoreErrorHandler.toFriendlyArabicMessage(error)
                }
            )
        }
    }

    fun upvotePostHelpful(post: ForumPost) {
        viewModelScope.launch {
            repository.incrementPostHelpful(post.id).fold(
                onSuccess = {
                    _bannerMessage.value = "تم تسجيل تصويتك بأن هذا الاستفسار مفيد."
                },
                onFailure = { error ->
                    _errorBannerMessage.value = FirestoreErrorHandler.toFriendlyArabicMessage(error)
                }
            )
        }
    }

    fun toggleReplyHelpfulByAuthor(post: ForumPost, reply: ForumReply) {
        if (post.userId != currentUserId) return
        viewModelScope.launch {
            repository.toggleReplyMarkedHelpful(
                postId = post.id,
                replyId = reply.id,
                markedHelpful = !reply.markedHelpfulByAuthor
            ).onFailure { error ->
                _errorBannerMessage.value = FirestoreErrorHandler.toFriendlyArabicMessage(error)
            }
        }
    }

    fun deleteOwnPost(post: ForumPost) {
        if (post.userId != currentUserId) return
        viewModelScope.launch {
            repository.deletePost(post.id).fold(
                onSuccess = {
                    if (_selectedPostId.value == post.id) {
                        _selectedPostId.value = null
                    }
                    _bannerMessage.value = "تم حذف استفسارك من المنتدى."
                },
                onFailure = { error ->
                    _errorBannerMessage.value = FirestoreErrorHandler.toFriendlyArabicMessage(error)
                }
            )
        }
    }

    fun deleteOwnReply(post: ForumPost, reply: ForumReply) {
        if (reply.userId != currentUserId) return
        viewModelScope.launch {
            repository.deleteReply(post.id, reply.id).fold(
                onSuccess = {
                    _bannerMessage.value = "تم حذف ردك."
                },
                onFailure = { error ->
                    _errorBannerMessage.value = FirestoreErrorHandler.toFriendlyArabicMessage(error)
                }
            )
        }
    }
}
