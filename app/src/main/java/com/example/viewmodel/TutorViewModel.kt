package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CurriculumData
import com.example.data.local.AcademicStream
import com.example.data.local.AppDatabase
import com.example.data.local.AppThemeMode
import com.example.data.local.ChatRepository
import com.example.data.local.PomodoroStatsRepository
import com.example.data.local.QuizProgressRepository
import com.example.data.local.SettingsRepository
import com.example.data.local.StudentSettings
import com.example.data.local.StudyNoteRepository
import com.example.model.AiStudyPlanUiState
import com.example.model.ChatMessage
import com.example.model.GroundingSource
import com.example.model.PomodoroPhase
import com.example.model.PomodoroPreset
import com.example.model.PomodoroSessionLog
import com.example.model.PomodoroUiState
import com.example.model.QuizQuestion
import com.example.model.QuizAttemptUiState
import com.example.model.QuizDifficulty
import com.example.model.QuizGeneratorUiState
import com.example.model.QuizResultRecord
import com.example.model.ExamTip
import com.example.model.GuidedHintUiState
import com.example.model.StudyNote
import com.example.model.TeacherTipCategory
import com.example.model.TeacherTipsRefreshState
import com.example.model.TutorRole
import com.example.util.AiQuizJsonParser
import com.example.util.AuthResult
import com.example.util.GoogleAuthHelper
import com.example.util.PomodoroFeedbackUtil
import com.example.util.ProgressAssessmentEngine
import com.example.util.StudentAccount
import com.example.util.TeacherTipsJsonParser
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.UUID

private const val MAX_ATTACHMENT_BYTES = 18 * 1024 * 1024

private const val TAG = "TutorViewModel"

sealed interface TutorUiState {
    data object Idle : TutorUiState
    data object Loading : TutorUiState
    data class Streaming(
        val partialResponse: String,
        val groundingSources: List<GroundingSource> = emptyList(),
        val searchQueries: List<String> = emptyList()
    ) : TutorUiState
    data class Success(val message: String) : TutorUiState
    data class Error(val errorMessage: String) : TutorUiState
}

class TutorViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val chatRepository: ChatRepository by lazy {
        ChatRepository(AppDatabase.getInstance(application).chatDao())
    }

    private val studyNoteRepository: StudyNoteRepository by lazy {
        StudyNoteRepository(AppDatabase.getInstance(application).studyNoteDao())
    }

    private val pomodoroStatsRepository: PomodoroStatsRepository by lazy {
        PomodoroStatsRepository(AppDatabase.getInstance(application).pomodoroSessionDao())
    }

    private val quizProgressRepository: QuizProgressRepository by lazy {
        QuizProgressRepository(AppDatabase.getInstance(application).quizResultDao())
    }

    private val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(application)
    }

    val settings: StateFlow<StudentSettings> = settingsRepository.settings
        .catch { e ->
            Log.e(TAG, "Failed to collect student settings from DataStore", e)
            emit(StudentSettings())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StudentSettings()
        )

    val currentStudent: StateFlow<StudentAccount?> = GoogleAuthHelper.observeAuthState(application)
        .catch { e ->
            Log.e(TAG, "Failed to observe Firebase auth state", e)
            emit(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GoogleAuthHelper.safeAuthOrNull(application)?.currentUser?.let {
                StudentAccount(
                    uid = it.uid,
                    displayName = it.displayName?.takeIf { n -> n.isNotBlank() } ?: "طالب الشهادة السودانية",
                    email = it.email.orEmpty(),
                    photoUrl = it.photoUrl?.toString()
                )
            }
        )

    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authSuccessMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage: StateFlow<String?> = _authSuccessMessage.asStateFlow()

    val studyNotes: StateFlow<List<StudyNote>> = studyNoteRepository.notes
        .catch { e ->
            Log.e(TAG, "Failed to collect study notes from Room DB", e)
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pomodoroSessions: StateFlow<List<PomodoroSessionLog>> = pomodoroStatsRepository.sessions
        .catch { e ->
            Log.e(TAG, "Failed to collect Pomodoro sessions from Room DB", e)
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val quizResults: StateFlow<List<QuizResultRecord>> = quizProgressRepository.results
        .catch { e ->
            Log.e(TAG, "Failed to collect quiz progress results from Room DB", e)
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _aiStudyPlanState = MutableStateFlow<AiStudyPlanUiState>(AiStudyPlanUiState.Idle)
    val aiStudyPlanState: StateFlow<AiStudyPlanUiState> = _aiStudyPlanState.asStateFlow()

    private var activeQuizSubjectTitle: String = "التاريخ"
    private var activeQuizTopic: String = "أسباب الثورة المهدية"
    private var studyPlanJob: Job? = null

    private val tutorGenerationConfig = generationConfig {
        temperature = 0.7f
        maxOutputTokens = 900
    }

    // Lazy cache of GenerativeModel instances per (TutorRole, grounding) with role-specific system instructions
    private val roleModelCache = mutableMapOf<Pair<TutorRole, Boolean>, GenerativeModel>()

    private fun getModelForRole(role: TutorRole, withSearchGrounding: Boolean): GenerativeModel {
        return roleModelCache.getOrPut(role to withSearchGrounding) {
            if (withSearchGrounding) {
                Firebase.ai.generativeModel(
                    modelName = role.modelName,
                    generationConfig = tutorGenerationConfig,
                    tools = listOf(Tool.googleSearch()),
                    systemInstruction = content { text(role.systemInstruction) }
                )
            } else {
                Firebase.ai.generativeModel(
                    modelName = role.modelName,
                    generationConfig = tutorGenerationConfig,
                    systemInstruction = content { text(role.systemInstruction) }
                )
            }
        }
    }

    // Resilient Gemini 3 fallback models using the active role's system instruction
    private fun getFallbackModel(role: TutorRole, useSearchGrounding: Boolean, fast: Boolean): GenerativeModel {
        val modelName = if (fast) "gemini-3.5-flash-lite" else "gemini-3.8-flash"
        return if (useSearchGrounding && !fast) {
            Firebase.ai.generativeModel(
                modelName = modelName,
                generationConfig = tutorGenerationConfig,
                tools = listOf(Tool.googleSearch()),
                systemInstruction = content { text(role.systemInstruction) }
            )
        } else {
            Firebase.ai.generativeModel(
                modelName = modelName,
                generationConfig = tutorGenerationConfig,
                systemInstruction = content { text(role.systemInstruction) }
            )
        }
    }

    private var activeGenerationJob: Job? = null
    private var lastUserPrompt: String? = null
    private var lastUserAttachment: PendingAttachment? = null

    data class PendingAttachment(
        val fileName: String,
        val mimeType: String,
        val bytes: ByteArray,
        val previewBitmap: android.graphics.Bitmap? = null
    )

    private val _pendingAttachment = MutableStateFlow<PendingAttachment?>(null)
    val pendingAttachment: StateFlow<PendingAttachment?> = _pendingAttachment.asStateFlow()

    private val _selectedTutorRole = MutableStateFlow(TutorRole.GENERAL)
    val selectedTutorRole: StateFlow<TutorRole> = _selectedTutorRole.asStateFlow()

    private val _isSearchGroundingEnabled = MutableStateFlow(true)
    val isSearchGroundingEnabled: StateFlow<Boolean> = _isSearchGroundingEnabled.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(getInitialSeedMessages())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _tutorState = MutableStateFlow<TutorUiState>(TutorUiState.Idle)
    val tutorState: StateFlow<TutorUiState> = _tutorState.asStateFlow()

    // Current interactive quiz state for practice & AI Socratic quiz generator
    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()

    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(CurriculumData.mahdistRevolutionLesson.sampleQuestions)
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    private val _quizTitle = MutableStateFlow("مقرر التاريخ: الثورة المهدية")
    val quizTitle: StateFlow<String> = _quizTitle.asStateFlow()

    private val _quizAttemptState = MutableStateFlow(QuizAttemptUiState())
    val quizAttemptState: StateFlow<QuizAttemptUiState> = _quizAttemptState.asStateFlow()

    private val _quizGeneratorState = MutableStateFlow<QuizGeneratorUiState>(QuizGeneratorUiState.Idle)
    val quizGeneratorState: StateFlow<QuizGeneratorUiState> = _quizGeneratorState.asStateFlow()

    private val _guidedHintState = MutableStateFlow<GuidedHintUiState>(GuidedHintUiState.Idle)
    val guidedHintState: StateFlow<GuidedHintUiState> = _guidedHintState.asStateFlow()

    private val _isConfiguringAiQuiz = MutableStateFlow(false)
    val isConfiguringAiQuiz: StateFlow<Boolean> = _isConfiguringAiQuiz.asStateFlow()

    private val _quizSelectedSubject = MutableStateFlow(CurriculumData.subjects.firstOrNull()?.title ?: "الرياضيات المتخصصة")
    val quizSelectedSubject: StateFlow<String> = _quizSelectedSubject.asStateFlow()

    private val _quizTopicInput = MutableStateFlow("")
    val quizTopicInput: StateFlow<String> = _quizTopicInput.asStateFlow()

    private val _quizSelectedDifficulty = MutableStateFlow(QuizDifficulty.EXAM_LEVEL)
    val quizSelectedDifficulty: StateFlow<QuizDifficulty> = _quizSelectedDifficulty.asStateFlow()

    private val _quizQuestionCount = MutableStateFlow(5)
    val quizQuestionCount: StateFlow<Int> = _quizQuestionCount.asStateFlow()

    private var quizGenerationJob: Job? = null
    private var guidedHintJob: Job? = null

    private val quizResponseSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "quizTitle" to Schema.string(description = "A concise title combining the subject and topic"),
                "questions" to Schema.array(
                    items = Schema.obj(
                        properties = mapOf(
                            "question" to Schema.string(description = "Clear multiple-choice question tailored to the subject and topic"),
                            "options" to Schema.array(
                                items = Schema.string(),
                                description = "Exactly 4 distinct plausible answer options"
                            ),
                            "correctIndex" to Schema.integer(description = "0-based index (0, 1, 2, or 3) of the single correct answer"),
                            "socraticHint" to Schema.string(description = "A guiding question or clue pointing toward the concept WITHOUT revealing or naming the correct option"),
                            "optionHints" to Schema.array(
                                items = Schema.string(),
                                description = "Exactly 4 short guiding feedback messages matching each option index. For wrong options, explain what makes that option inaccurate and nudge the student toward the right rule WITHOUT stating the answer. For the correct option, affirm the student's reasoning."
                            ),
                            "explanation" to Schema.string(description = "Complete conceptual explanation revealed only after the student discovers the answer"),
                            "teacherGuidance" to Schema.string(description = "Encouraging exam-taking tip related to this concept")
                        )
                    ),
                    description = "List of generated multiple-choice questions"
                )
            )
        )
    }

    private val quizJsonConfig by lazy {
        generationConfig {
            temperature = 0.7f
            responseMimeType = "application/json"
            responseSchema = quizResponseSchema
        }
    }

    private val quizGeneratorSystemInstruction = """
        أنت معلم خبير ومؤلف اختبارات ذكي لطلاب الشهادة الثانوية السودانية والمواد الدراسية المختلفة.
        مهمتك: توليد أسئلة اختيار من متعدد دقيقة تعليمياً وفق المادة والموضوع ومستوى الصعوبة المطلوب.
        قواعد صارمة:
        1. كل سؤال يجب أن يحتوي على 4 خيارات منطقية ومتقاربة في الصياغة، مع إجابة واحدة صحيحة فقط.
        2. في الحقل socraticHint وفي رسائل التغذية الراجعة للخيارات الخاطئة ضمن optionHints: لا تذكر الإجابة الصحيحة أبداً ولا تلمّح لرقمها أو نصها مباشرة؛ بل وجّه الطالب بسؤال إرشادي أو تذكير بالقانون أو المفهوم الذي يكشف الخطأ في اختياره ويدفعه للتفكير والوصول للحل بنفسه.
        3. إذا كان الموضوع باللغة الإنجليزية أو مادة اللغة الإنجليزية، اكتب الأسئلة والخيارات بالإنجليزية، واجعل التوجيه الإرشادي واضحاً ومفيداً (بالعربية المبسطة أو الإنجليزية المناسبة لمستوى الطالب). وفي باقي المواد اكتب كل شيء بلغة عربية فصحى واضحة ومباشرة.
    """.trimIndent()

    private val quizGeneratorModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = quizJsonConfig,
            systemInstruction = content { text(quizGeneratorSystemInstruction) }
        )
    }

    private val quizGeneratorFallbackModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            generationConfig = quizJsonConfig,
            systemInstruction = content { text(quizGeneratorSystemInstruction) }
        )
    }

    private val socraticCoachSystemInstruction = """
        أنت معلم سقراطي ذكي لطلاب الشهادة الثانوية.
        عندما يخطئ الطالب في سؤال اختيار من متعدد أو يطلب تلميحاً:
        - ممنوع منعاً باتاً ذكر الإجابة الصحيحة أو حرفها أو نصها أو الإشارة المباشرة إليها.
        - خاطب الطالب بأسلوب مشجع ومختصر (30 إلى 75 كلمة).
        - وضّح بلطف سبب عدم كفاية الخيار الذي جربه، ثم اطرح سؤالاً موجِّهاً أو تلميحاً منهجياً يقوده لاكتشاف الإجابة الصحيحة بنفسه في المحاولة التالية.
    """.trimIndent()

    private val socraticCoachModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.65f
                maxOutputTokens = 320
            },
            systemInstruction = content { text(socraticCoachSystemInstruction) }
        )
    }

    private val socraticCoachFallbackModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            generationConfig = generationConfig {
                temperature = 0.65f
                maxOutputTokens = 320
            },
            systemInstruction = content { text(socraticCoachSystemInstruction) }
        )
    }

    // --- Renewable Teacher's Tips (نصائح المعلم المتجددة) ---
    private val _teacherTips = MutableStateFlow(CurriculumData.examTips)
    val teacherTips: StateFlow<List<ExamTip>> = _teacherTips.asStateFlow()

    private val _teacherTipsRefreshState = MutableStateFlow<TeacherTipsRefreshState>(TeacherTipsRefreshState.Idle)
    val teacherTipsRefreshState: StateFlow<TeacherTipsRefreshState> = _teacherTipsRefreshState.asStateFlow()

    private val _featuredTipIndex = MutableStateFlow(
        if (CurriculumData.examTips.isNotEmpty()) {
            Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CurriculumData.examTips.size
        } else {
            0
        }
    )
    val featuredTipIndex: StateFlow<Int> = _featuredTipIndex.asStateFlow()

    private var teacherTipsJob: Job? = null

    private val teacherTipsResponseSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "tips" to Schema.array(
                    items = Schema.obj(
                        properties = mapOf(
                            "title" to Schema.string(description = "A warm, compelling Arabic title for the study or exam tip"),
                            "category" to Schema.string(description = "Exact Arabic category: 'قلق الاختبارات' or 'المواد العلمية' or 'المواد الأدبية' or 'تنظيم الوقت' or 'قاعة الامتحان'"),
                            "content" to Schema.string(description = "Clear, encouraging, step-by-step guidance tailored to Sudanese Secondary Certificate students (55-110 words)"),
                            "importantRule" to Schema.string(description = "A concise golden rule summary sentence"),
                            "actionSteps" to Schema.array(
                                items = Schema.string(),
                                description = "2 to 3 concrete, practical action steps the student can apply immediately"
                            ),
                            "streamTag" to Schema.string(description = "Target stream in Arabic: 'عام' or 'القسم العلمي' or 'القسم الأدبي'")
                        )
                    ),
                    description = "Exactly 3 fresh, distinct Sudanese Secondary Certificate teacher tips"
                )
            )
        )
    }

    private val teacherTipsSystemInstruction = """
        أنت معلم سوداني خبير ومرشد تربوي متخصص في إعداد طلاب الشهادة الثانوية السودانية.
        مهمتك: تقديم نصائح متجددة، عملية، ومشجعة في كيفية التعامل مع قلق الاختبارات، أو استراتيجيات المذاكرة الفعالة للمواد العلمية والأدبية، أو تنظيم الوقت وإدارة قاعة الامتحان.
        قواعد:
        1. استخدم لغة عربية فصحى مبسطة وودودة ومحفزة تناسب طلاب السودان.
        2. قدّم نصائح عملية وخطوات تطبيقية قابلة للتنفيذ فوراً وليس كلاماً عاماً مكرراً.
        3. التزم في حقل category بإحدى الفئات التالية حرفياً: (قلق الاختبارات، المواد العلمية، المواد الأدبية، تنظيم الوقت، قاعة الامتحان).
    """.trimIndent()

    private val teacherTipsJsonConfig by lazy {
        generationConfig {
            temperature = 0.85f
            responseMimeType = "application/json"
            responseSchema = teacherTipsResponseSchema
        }
    }

    private val teacherTipsModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = teacherTipsJsonConfig,
            systemInstruction = content { text(teacherTipsSystemInstruction) }
        )
    }

    private val teacherTipsFallbackModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            generationConfig = teacherTipsJsonConfig,
            systemInstruction = content { text(teacherTipsSystemInstruction) }
        )
    }

    // --- Pomodoro Study Timer State ---
    private val _pomodoroState = MutableStateFlow(PomodoroUiState())
    val pomodoroState: StateFlow<PomodoroUiState> = _pomodoroState.asStateFlow()
    private var pomodoroTickerJob: Job? = null

    private var hasSyncedInitialSettings = false

    init {
        // Pre-seed conversation in Room database and continuously collect persisted history
        viewModelScope.launch {
            try {
                chatRepository.seedInitialIfEmpty(getInitialSeedMessages())
                chatRepository.messages.collect { persistedMessages ->
                    if (persistedMessages.isNotEmpty()) {
                        _messages.value = persistedMessages
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load chat history from Room DB", e)
            }
        }

        // Hydrate initial tutor role, search grounding, and Pomodoro preferences from DataStore
        viewModelScope.launch {
            try {
                settingsRepository.settings.collect { saved ->
                    if (!hasSyncedInitialSettings) {
                        hasSyncedInitialSettings = true
                        _selectedTutorRole.value = saved.defaultTutorRole
                        _isSearchGroundingEnabled.value = saved.searchGroundingDefault
                        val initialSecs = saved.pomodoroFocusMinutes * 60
                        _pomodoroState.update { current ->
                            current.copy(
                                focusMinutes = saved.pomodoroFocusMinutes,
                                shortBreakMinutes = saved.pomodoroShortBreakMinutes,
                                longBreakMinutes = saved.pomodoroLongBreakMinutes,
                                remainingSeconds = if (!current.isRunning && current.phase == PomodoroPhase.FOCUS) initialSecs else current.remainingSeconds,
                                totalPhaseSeconds = if (!current.isRunning && current.phase == PomodoroPhase.FOCUS) initialSecs else current.totalPhaseSeconds,
                                completedSessionsTotal = saved.pomodoroCompletedSessions,
                                totalFocusMinutes = saved.pomodoroTotalFocusMinutes,
                                soundEnabled = saved.pomodoroSoundEnabled,
                                autoStartNext = saved.pomodoroAutoStartNext
                            )
                        }
                    } else {
                        _pomodoroState.update { current ->
                            current.copy(
                                focusMinutes = saved.pomodoroFocusMinutes,
                                shortBreakMinutes = saved.pomodoroShortBreakMinutes,
                                longBreakMinutes = saved.pomodoroLongBreakMinutes,
                                completedSessionsTotal = saved.pomodoroCompletedSessions,
                                totalFocusMinutes = saved.pomodoroTotalFocusMinutes,
                                soundEnabled = saved.pomodoroSoundEnabled,
                                autoStartNext = saved.pomodoroAutoStartNext
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to hydrate initial settings", e)
            }
        }
    }

    fun selectTutorRole(role: TutorRole) {
        _selectedTutorRole.value = role
    }

    fun toggleSearchGrounding(enabled: Boolean = !_isSearchGroundingEnabled.value) {
        _isSearchGroundingEnabled.value = enabled
        viewModelScope.launch {
            try {
                settingsRepository.setSearchGroundingDefault(enabled)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to persist search grounding preference", e)
            }
        }
    }

    // --- Student Settings & Google Account Auth Actions ---
    fun updateThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            try {
                settingsRepository.setThemeMode(mode)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save theme mode", e)
            }
        }
    }

    fun toggleNightMode(currentlyDark: Boolean) {
        val nextMode = if (currentlyDark) AppThemeMode.LIGHT else AppThemeMode.DARK
        updateThemeMode(nextMode)
    }

    fun updateAcademicStream(stream: AcademicStream) {
        viewModelScope.launch {
            try {
                settingsRepository.setAcademicStream(stream)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save academic stream", e)
            }
        }
    }

    fun updateDefaultTutorRole(role: TutorRole) {
        _selectedTutorRole.value = role
        viewModelScope.launch {
            try {
                settingsRepository.setDefaultTutorRole(role)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save default tutor role", e)
            }
        }
    }

    fun updateSearchGroundingDefault(enabled: Boolean) {
        _isSearchGroundingEnabled.value = enabled
        viewModelScope.launch {
            try {
                settingsRepository.setSearchGroundingDefault(enabled)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save search grounding default", e)
            }
        }
    }

    fun updateStudyRemindersEnabled(enabled: Boolean, context: android.content.Context) {
        viewModelScope.launch {
            try {
                settingsRepository.setStudyRemindersEnabled(enabled)
                if (!enabled) {
                    _studySessions.value.forEach { session ->
                        com.example.util.StudyNotificationUtil.cancelStudyReminder(context, session.id)
                    }
                } else {
                    _studySessions.value.forEach { session ->
                        if (session.reminderEnabled) {
                            com.example.util.StudyNotificationUtil.scheduleStudyReminder(
                                context = context,
                                sessionId = session.id,
                                subjectTitle = session.subjectTitle,
                                topic = session.topic,
                                dayOfWeek = session.day.dayNumber,
                                hour = session.hour,
                                minute = session.minute,
                                isDaily = session.period == com.example.model.SchedulePeriod.DAILY
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update study reminders setting", e)
            }
        }
    }

    fun attemptSilentGoogleSignIn(context: android.content.Context) {
        viewModelScope.launch {
            GoogleAuthHelper.attemptAutoSignIn(context)
        }
    }

    fun signInWithGoogle(context: android.content.Context) {
        if (_isSigningIn.value) return
        _authError.value = null
        _authSuccessMessage.value = null
        _isSigningIn.value = true
        viewModelScope.launch {
            try {
                when (val result = GoogleAuthHelper.signInWithGoogle(context)) {
                    is AuthResult.Success -> {
                        _authSuccessMessage.value = "مرحباً بك يا ${result.account.displayName}! تم ربط حسابك في Google بنجاح."
                    }
                    AuthResult.Cancelled -> {
                        // User dismissed the credential sheet; leave no error banner
                    }
                    is AuthResult.Error -> {
                        _authError.value = result.message
                    }
                }
            } finally {
                _isSigningIn.value = false
            }
        }
    }

    fun signOutFromGoogle(context: android.content.Context) {
        _authError.value = null
        _authSuccessMessage.value = null
        viewModelScope.launch {
            GoogleAuthHelper.signOut(context)
            _authSuccessMessage.value = "تم تسجيل الخروج من حساب Google."
        }
    }

    fun clearAuthFeedback() {
        _authError.value = null
        _authSuccessMessage.value = null
    }

    fun clearPendingAttachment() {
        _pendingAttachment.value = null
    }

    fun attachFileFromUri(context: android.content.Context, uri: android.net.Uri) {
        val appContext = context.applicationContext
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val contentResolver = appContext.contentResolver
                    val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
                    var displayName = "ملف_مرفق"
                    var reportedSize = -1L
                    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                        if (cursor.moveToFirst()) {
                            if (nameIndex >= 0) {
                                displayName = cursor.getString(nameIndex) ?: displayName
                            }
                            if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                                reportedSize = cursor.getLong(sizeIndex)
                            }
                        }
                    }

                    if (reportedSize > MAX_ATTACHMENT_BYTES) {
                        return@withContext Result.failure<PendingAttachment>(
                            IllegalArgumentException("حجم الملف كبير جداً. يرجى اختيار ملف أقل من 18 ميغابايت.")
                        )
                    }

                    val bytes = contentResolver.openInputStream(uri)?.use { input ->
                        val buffer = ByteArray(8192)
                        val out = ByteArrayOutputStream()
                        var total = 0
                        while (true) {
                            val read = input.read(buffer)
                            if (read == -1) break
                            total += read
                            if (total > MAX_ATTACHMENT_BYTES) {
                                return@withContext Result.failure<PendingAttachment>(
                                    IllegalArgumentException("حجم الملف كبير جداً. يرجى اختيار ملف أقل من 18 ميغابايت.")
                                )
                            }
                            out.write(buffer, 0, read)
                        }
                        out.toByteArray()
                    }

                    if (bytes == null || bytes.isEmpty()) {
                        return@withContext Result.failure<PendingAttachment>(
                            IllegalStateException("تعذر قراءة الملف المرفق.")
                        )
                    }

                    val bitmap = if (mimeType.startsWith("image/")) {
                        decodeSampledBitmap(bytes, maxDimension = 2048)
                    } else {
                        null
                    }

                    Result.success(
                        PendingAttachment(
                            fileName = displayName,
                            mimeType = mimeType,
                            bytes = bytes,
                            previewBitmap = bitmap
                        )
                    )
                }

                result.onSuccess { attachment ->
                    _pendingAttachment.value = attachment
                }.onFailure { error ->
                    _tutorState.value = TutorUiState.Error(error.message ?: "تعذر إرفاق الملف.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to attach file from URI", e)
                _tutorState.value = TutorUiState.Error("تعذر إرفاق الملف: ${e.localizedMessage ?: ""}")
            }
        }
    }

    private fun decodeSampledBitmap(bytes: ByteArray, maxDimension: Int): android.graphics.Bitmap? {
        val boundsOptions = android.graphics.BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, boundsOptions)
        var sampleSize = 1
        val maxOrig = maxOf(boundsOptions.outWidth, boundsOptions.outHeight)
        while (maxOrig / sampleSize > maxDimension) {
            sampleSize *= 2
        }
        val decodeOptions = android.graphics.BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOptions)
    }

    private fun extractGroundingFromResponse(
        response: GenerateContentResponse,
        sourcesAccumulator: MutableList<GroundingSource>,
        queriesAccumulator: MutableList<String>
    ) {
        val candidate = response.candidates.firstOrNull() ?: return
        val metadata = candidate.groundingMetadata ?: return

        metadata.webSearchQueries.forEach { query ->
            val cleanQuery = query.trim()
            if (cleanQuery.isNotEmpty() && queriesAccumulator.none { it.equals(cleanQuery, ignoreCase = true) }) {
                queriesAccumulator.add(cleanQuery)
            }
        }

        metadata.groundingChunks.forEach { chunk ->
            val web = chunk.web
            if (web != null) {
                val uri = web.uri?.trim().orEmpty()
                val title = web.title?.trim().takeUnless { it.isNullOrEmpty() } ?: uri
                if (uri.isNotEmpty() && sourcesAccumulator.none { it.uri == uri }) {
                    sourcesAccumulator.add(GroundingSource(title = title, uri = uri))
                }
            }
        }
    }

    private fun getInitialSeedMessages(): List<ChatMessage> {
        val baseTimestamp = System.currentTimeMillis()
        val initialUserMsg = ChatMessage(
            id = "seed_user_1",
            text = "أنا طالب شهادة سودانية، ممكن تشرح لي أسباب الثورة المهدية باختصار؟",
            isUser = true,
            timestamp = baseTimestamp
        )

        val initialTeacherMsg = ChatMessage(
            id = "seed_tutor_1",
            text = """
مرحباً بك يا بني وبكل أبطال وبطلات الشهادة الثانوية السودانية! أسأل الله أن يوفقكم ويفتح عليكم فتوح العارفين.

سؤالك عن أسباب الثورة المهدية (1881م) هو من الأسئلة المحورية والجوهرية جداً في امتحان التاريخ. وحتى تحفظها وتفهمها فهماً عميقاً يضمن لك الدرجة الكاملة في الامتحان، قسّمها دائماً إلى أربعة محاور أساسية، بالإضافة إلى "السبب المباشر":

1️⃣ الأسباب الدينية والروحية:
• الرغبة الصادقة في إحياء السنة النبوية وإماتة البدع ومحاربة الفساد الأخلاقي.
• سخط العلماء وعامة الشعب على فساد قضاة وموظفي الحكم التركي وبعدهم عن شرع الله.
• انتشار عقيدة انتظار "المهدي المنتظر" لإنقاذ الناس وملء الأرض عدلاً وقسطاً بعد أن ملئت جوراً.

2️⃣ الأسباب السياسية والإدارية:
• استبداد الحكم التركي المصري (التركية السابقة) وسلب حرية المواطنين.
• إسناد المناصب الرفيعة لموظفين وحكام أجانب وأوروبيين (مثل غوردون وسلاطين) مما جرح كرامة السودانيين.
• تهميش وإهانة زعماء القبائل والمشايخ التقليديين.

3️⃣ الأسباب الاقتصادية (وهي المحرك الأكبر للغضب الشعبي):
• فداحة الضرائب وتعدد أسمائها (ضرائب على الرؤوس، النخيل، السواقي، والمواشي).
• القسوة والوحشية في الجباية باستخدام السوط (الكرباج) ومصادرة أموال الفلاحين.
• تدهور التجارة الداخلية بعد احتكار الحكومة لسلع هامة كالصمغ والعاج.

4️⃣ الأسباب الاجتماعية:
• سوء معاملة الأتراك للشعب السوداني واستعلاؤهم عليهم.
• سياسة مكافحة تجارة الرقيق الفجائية التي قادها ضباط أوروبيون دون تعويض أو توفير بدائل اقتصادية، مما أضر باقتصاد العديد من القبائل والأسر.

⚡ السبب المباشر (شرارة الانطلاق):
واقعة الجزيرة أبا (أغسطس 1881م / رمضان 1298هـ): أرسل الحاكم العام رؤوف باشا حملة عسكرية بقيادة أبي السعود للقبض على الإمام المهدي، فتصدى لها الأنصار بأسلحة تقليدية بسيطة وانتصروا عليها ناصعاً.

---
💡 والآن دورك يا بطل لتختبر استيعابك:
لو أتاك واضع الامتحان بسؤال: "علل: يعتبر الجانب الاقتصادي من أخطر دوافع قيام الثورة المهدية؟"
ما هي النقطتان الأساسيتان اللتان ستكتبهما؟ فكّر واكتب لي إجابتك، وسأراجعها معك خطوة بخطوة!
            """.trimIndent(),
            isUser = false,
            timestamp = baseTimestamp + 1L,
            isSearchGrounded = true
        )

        return listOf(initialUserMsg, initialTeacherMsg)
    }

    fun clearConversation() {
        activeGenerationJob?.cancel()
        lastUserPrompt = null
        lastUserAttachment = null
        _tutorState.value = TutorUiState.Idle
        _pendingAttachment.value = null
        val welcomeMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = "مرحباً بك يا بطل الشهادة السودانية! أنا أستاذك ومعلمك الذكي. يمكنك كتابة سؤالك أو إرفاق ملف PDF أو صورة مسألة، وسأشرح لك الخطوات بأسلوب مبسط وواضح لنيل الدرجة الكاملة.",
            isUser = false,
            isSearchGrounded = _isSearchGroundingEnabled.value
        )
        _messages.value = listOf(welcomeMsg)
        viewModelScope.launch {
            try {
                chatRepository.clearHistory()
                chatRepository.saveMessage(welcomeMsg)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear Room chat history", e)
            }
        }
    }

    fun deleteMessage(id: String) {
        _messages.value = _messages.value.filter { it.id != id }
        viewModelScope.launch {
            try {
                chatRepository.deleteMessageById(id)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete message from Room", e)
            }
        }
    }

    fun retryLastMessage() {
        val last = lastUserPrompt ?: return
        sendMessage(last, appendUserBubble = false)
    }

    private fun buildMultiTurnChatHistory(excludeLastUserMessage: Boolean): List<Content> {
        val rawMessages = if (excludeLastUserMessage && _messages.value.isNotEmpty()) {
            _messages.value.dropLast(1)
        } else {
            _messages.value
        }

        // Keep up to last 10 turns so multi-turn memory is rich yet bounded
        val recent = rawMessages.takeLast(10)
        val history = mutableListOf<Content>()
        var lastRole: String? = null

        for (msg in recent) {
            val text = msg.text.trim()
            if (text.isEmpty()) continue
            val role = if (msg.isUser) "user" else "model"
            // Gemini multi-turn history should begin with a user turn and alternate user/model
            if (history.isEmpty() && role == "model") continue
            if (role == lastRole) continue

            val formattedText = if (msg.isUser && !msg.attachmentName.isNullOrBlank()) {
                "[ملف مرفق: ${msg.attachmentName}] $text"
            } else {
                text
            }
            history.add(content(role) { text(formattedText) })
            lastRole = role
        }

        // Ensure history ends with a "model" turn before calling sendMessage/sendMessageStream
        if (history.lastOrNull()?.role == "user") {
            history.removeAt(history.lastIndex)
        }
        return history
    }

    fun sendMessage(userPrompt: String, appendUserBubble: Boolean = true) {
        val attachment = _pendingAttachment.value ?: if (!appendUserBubble) lastUserAttachment else null
        val trimmed = userPrompt.trim().ifEmpty {
            if (attachment != null) "يا أستاذ، اشرح لي محتوى هذا الملف المرفق (${attachment.fileName}) خطوة بخطوة." else ""
        }
        if (trimmed.isEmpty() && attachment == null) return

        lastUserPrompt = trimmed
        lastUserAttachment = attachment
        activeGenerationJob?.cancel()
        _pendingAttachment.value = null

        if (appendUserBubble) {
            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = trimmed,
                isUser = true,
                attachmentName = attachment?.fileName,
                attachmentMimeType = attachment?.mimeType
            )
            _messages.value = _messages.value + userMessage
            viewModelScope.launch {
                try {
                    chatRepository.saveMessage(userMessage)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to save user message to Room", e)
                }
            }
        }
        _tutorState.value = TutorUiState.Loading

        val useSearchGrounding = _isSearchGroundingEnabled.value
        val activeRole = _selectedTutorRole.value

        activeGenerationJob = viewModelScope.launch {
            val responseBuilder = StringBuilder()
            val groundingSources = mutableListOf<GroundingSource>()
            val searchQueries = mutableListOf<String>()
            val tempMessageId = UUID.randomUUID().toString()
            var modelUsed = activeRole.modelName

            try {
                val chatHistory = buildMultiTurnChatHistory(excludeLastUserMessage = appendUserBubble)
                val promptWithAttachmentNote = if (attachment != null) {
                    "قام الطالب بإرفاق ملف (${attachment.fileName}). حلّل محتوى الملف أو الصورة المرفقة واشرح الخطوات بوضوح:\n$trimmed"
                } else {
                    trimmed
                }

                val requestContent = content("user") {
                    if (attachment != null) {
                        if (attachment.previewBitmap != null) {
                            image(attachment.previewBitmap)
                        } else {
                            inlineData(attachment.bytes, attachment.mimeType)
                        }
                    }
                    text(promptWithAttachmentNote)
                }

                val primaryModel = getModelForRole(activeRole, useSearchGrounding)

                withTimeout(60_000L) {
                    // Primary attempt: selected Gemini 4 / 3.1 model using multi-turn startChat(history)
                    try {
                        withTimeoutOrNull(22_000L) {
                            val chatSession = primaryModel.startChat(history = chatHistory)
                            chatSession.sendMessageStream(requestContent).collect { chunk ->
                                extractGroundingFromResponse(chunk, groundingSources, searchQueries)
                                val chunkText = chunk.text.orEmpty()
                                if (chunkText.isNotEmpty() || groundingSources.isNotEmpty()) {
                                    responseBuilder.append(chunkText)
                                    _tutorState.value = TutorUiState.Streaming(
                                        partialResponse = responseBuilder.toString(),
                                        groundingSources = groundingSources.toList(),
                                        searchQueries = searchQueries.toList()
                                    )
                                }
                            }
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.w(TAG, "Primary model (${activeRole.modelName}) stream interrupted, trying fallback", e)
                    }

                    // Unary retry on primary model if stream yielded no text
                    if (responseBuilder.isBlank()) {
                        try {
                            withTimeoutOrNull(12_000L) {
                                val chatSession = primaryModel.startChat(history = chatHistory)
                                val response = chatSession.sendMessage(requestContent)
                                extractGroundingFromResponse(response, groundingSources, searchQueries)
                                val text = response.text.orEmpty()
                                if (text.isNotBlank()) {
                                    responseBuilder.append(text)
                                }
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            Log.w(TAG, "Unary primary model (${activeRole.modelName}) call failed", e)
                        }
                    }

                    // Resilient fallback to gemini-3.8-flash if primary Gemini 4 tier is unavailable
                    if (responseBuilder.isBlank()) {
                        modelUsed = "gemini-3.8-flash"
                        try {
                            withTimeoutOrNull(12_000L) {
                                val fallbackChat = getFallbackModel(activeRole, useSearchGrounding, fast = false)
                                    .startChat(history = chatHistory)
                                fallbackChat.sendMessageStream(requestContent).collect { chunk ->
                                    extractGroundingFromResponse(chunk, groundingSources, searchQueries)
                                    val chunkText = chunk.text.orEmpty()
                                    if (chunkText.isNotEmpty()) {
                                        responseBuilder.append(chunkText)
                                        _tutorState.value = TutorUiState.Streaming(
                                            partialResponse = responseBuilder.toString(),
                                            groundingSources = groundingSources.toList(),
                                            searchQueries = searchQueries.toList()
                                        )
                                    }
                                }
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            Log.w(TAG, "Fallback gemini-3.8-flash stream interrupted, trying fast lite model", e)
                        }
                    }

                    // Final ultra-fast fallback to gemini-3.5-flash-lite
                    if (responseBuilder.isBlank()) {
                        modelUsed = "gemini-3.5-flash-lite"
                        withTimeoutOrNull(12_000L) {
                            val fastChat = getFallbackModel(activeRole, useSearchGrounding = false, fast = true)
                                .startChat(history = chatHistory)
                            val unaryResponse = fastChat.sendMessage(requestContent)
                            val unaryText = unaryResponse.text.orEmpty()
                            if (unaryText.isNotBlank()) {
                                responseBuilder.append(unaryText)
                            }
                        }
                    }
                }

                val finalOutput = responseBuilder.toString().trim()
                if (finalOutput.isNotBlank()) {
                    lastUserAttachment = null
                    val botMessage = ChatMessage(
                        id = tempMessageId,
                        text = finalOutput,
                        isUser = false,
                        groundingSources = groundingSources.toList(),
                        searchQueries = searchQueries.toList(),
                        isSearchGrounded = useSearchGrounding,
                        tutorRoleName = activeRole.titleAr,
                        modelUsed = modelUsed
                    )
                    _messages.value = _messages.value + botMessage
                    viewModelScope.launch {
                        try {
                            chatRepository.saveMessage(botMessage)
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to save bot message to Room", e)
                        }
                    }
                    _tutorState.value = TutorUiState.Success(finalOutput)
                } else {
                    _tutorState.value = TutorUiState.Error("لم يتم استلام رد من النموذج الذكي. يرجى المحاولة مرة أخرى.")
                }

            } catch (e: TimeoutCancellationException) {
                val partialOutput = responseBuilder.toString().trim()
                if (partialOutput.isNotBlank()) {
                    val botMessage = ChatMessage(
                        id = tempMessageId,
                        text = partialOutput,
                        isUser = false,
                        groundingSources = groundingSources.toList(),
                        searchQueries = searchQueries.toList(),
                        isSearchGrounded = useSearchGrounding,
                        tutorRoleName = activeRole.titleAr,
                        modelUsed = modelUsed
                    )
                    _messages.value = _messages.value + botMessage
                    viewModelScope.launch {
                        try {
                            chatRepository.saveMessage(botMessage)
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to save partial bot message to Room", e)
                        }
                    }
                    _tutorState.value = TutorUiState.Success(partialOutput)
                } else {
                    Log.e(TAG, "Tutor response timed out", e)
                    val errorMsg = "يا بني، استغرق الطلب وقتاً أطول من المعتاد. اضغط على «إعادة المحاولة» بالأسفل."
                    _tutorState.value = TutorUiState.Error(errorMsg)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val partialOutput = responseBuilder.toString().trim()
                if (partialOutput.isNotBlank()) {
                    val botMessage = ChatMessage(
                        id = tempMessageId,
                        text = partialOutput,
                        isUser = false,
                        groundingSources = groundingSources.toList(),
                        searchQueries = searchQueries.toList(),
                        isSearchGrounded = useSearchGrounding,
                        tutorRoleName = activeRole.titleAr,
                        modelUsed = modelUsed
                    )
                    _messages.value = _messages.value + botMessage
                    viewModelScope.launch {
                        try {
                            chatRepository.saveMessage(botMessage)
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to save partial bot message to Room", e)
                        }
                    }
                    _tutorState.value = TutorUiState.Success(partialOutput)
                } else {
                    Log.e(TAG, "Firebase AI Logic generation error", e)
                    val errorMsg = "حدث عارض: ${e.localizedMessage ?: "تعذر جلب الإجابة حالياً"}. يرجى التحقق من اتصالك والمحاولة مجدداً."
                    _tutorState.value = TutorUiState.Error(errorMsg)
                }
            }
        }
    }

    fun openAiQuizConfigurator(subjectTitle: String? = null, topic: String? = null) {
        if (!subjectTitle.isNullOrBlank()) {
            _quizSelectedSubject.value = subjectTitle
        }
        if (topic != null) {
            _quizTopicInput.value = topic
        }
        _quizGeneratorState.value = QuizGeneratorUiState.Idle
        _isConfiguringAiQuiz.value = true
    }

    fun closeAiQuizConfigurator() {
        _isConfiguringAiQuiz.value = false
    }

    fun updateQuizGeneratorSubject(subjectTitle: String) {
        _quizSelectedSubject.value = subjectTitle
        if (_quizGeneratorState.value is QuizGeneratorUiState.Error) {
            _quizGeneratorState.value = QuizGeneratorUiState.Idle
        }
    }

    fun updateQuizGeneratorTopic(topic: String) {
        _quizTopicInput.value = topic
        if (_quizGeneratorState.value is QuizGeneratorUiState.Error) {
            _quizGeneratorState.value = QuizGeneratorUiState.Idle
        }
    }

    fun updateQuizGeneratorDifficulty(difficulty: QuizDifficulty) {
        _quizSelectedDifficulty.value = difficulty
    }

    fun updateQuizGeneratorCount(count: Int) {
        _quizQuestionCount.value = count.coerceIn(3, 8)
    }

    fun generateAiQuiz() {
        val subject = _quizSelectedSubject.value.trim().ifBlank { "عام — الشهادة السودانية" }
        val topic = _quizTopicInput.value.trim()
        if (topic.isBlank()) {
            _quizGeneratorState.value = QuizGeneratorUiState.Error("يرجى كتابة اسم الموضوع أو اختياره من المقترحات أولاً.")
            return
        }

        val difficulty = _quizSelectedDifficulty.value
        val count = _quizQuestionCount.value

        quizGenerationJob?.cancel()
        guidedHintJob?.cancel()
        _guidedHintState.value = GuidedHintUiState.Idle
        _quizGeneratorState.value = QuizGeneratorUiState.Generating

        val prompt = """
            أنشئ اختبار اختيار من متعدد مكوّن من $count أسئلة بالضبط.
            - المادة الدراسية: $subject
            - الموضوع المحدد: $topic
            - مستوى الصعوبة: ${difficulty.titleAr} (${difficulty.promptDescription})
            
            تأكد من:
            - تنوع الأسئلة بحيث تغطي جوانب مختلفة من الموضوع ولا تتكرر الفكرة نفسها.
            - احتواء كل سؤال على 4 خيارات (options) و4 رسائل توجيهية مطابقة لها بالترتيب (optionHints).
            - أن تقود رسائل التوجيه (socraticHint و optionHints للخيارات الخاطئة) الطالب للتفكير واكتشاف الخطأ دون التصريح بالإجابة الصحيحة نهائياً.
        """.trimIndent()

        quizGenerationJob = viewModelScope.launch {
            try {
                val rawJson = withTimeout(60_000L) {
                    val primaryText = try {
                        quizGeneratorModel.generateContent(prompt).text
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.w(TAG, "Primary AI quiz model failed, trying fallback", e)
                        null
                    }
                    primaryText?.takeIf { it.isNotBlank() }
                        ?: quizGeneratorFallbackModel.generateContent(prompt).text
                } ?: throw IllegalStateException("لم يُرجع النموذج الذكي بيانات الاختبار.")

                val parsed = AiQuizJsonParser.parse(
                    rawJson = rawJson,
                    fallbackTitle = "$subject: $topic"
                )

                _quizQuestions.value = parsed.questions
                _quizTitle.value = parsed.quizTitle
                activeQuizSubjectTitle = subject
                activeQuizTopic = topic
                resetQuiz()
                _quizGeneratorState.value = QuizGeneratorUiState.Ready(subject, topic)
                _isConfiguringAiQuiz.value = false
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI quiz generation timed out", e)
                _quizGeneratorState.value = QuizGeneratorUiState.Error(
                    "استغرق توليد الاختبار وقتاً أطول من المعتاد (60 ثانية). تأكد من اتصال الإنترنت وحاول مرة أخرى."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to generate AI quiz", e)
                _quizGeneratorState.value = QuizGeneratorUiState.Error(
                    e.localizedMessage ?: "تعذر إنشاء الاختبار الذكي حالياً. يرجى المحاولة مرة أخرى."
                )
            }
        }
    }

    fun selectQuizAnswer(index: Int) {
        val attempt = _quizAttemptState.value
        if (!attempt.isSolvedOrRevealed && !attempt.attemptedWrongIndices.contains(index)) {
            _selectedAnswerIndex.value = index
        }
    }

    fun submitQuizAnswer() {
        val selectedIdx = _selectedAnswerIndex.value ?: return
        val currentQuestion = _quizQuestions.value.getOrNull(_currentQuizIndex.value) ?: return
        val currentAttempt = _quizAttemptState.value
        if (currentAttempt.isSolvedOrRevealed) return

        val isCorrect = selectedIdx == currentQuestion.correctIndex
        if (isCorrect) {
            guidedHintJob?.cancel()
            _guidedHintState.value = GuidedHintUiState.Idle
            val isFirstTry = currentAttempt.attemptedWrongIndices.isEmpty()
            val affirmation = currentQuestion.optionHints.getOrNull(selectedIdx)
                ?.takeIf { it.isNotBlank() }
                ?: if (isFirstTry) {
                    "إجابة دقيقة وموفقة من المحاولة الأولى!"
                } else {
                    "رائع! استثمرت التوجيه بنجاح ووصلت إلى الإجابة الصحيحة بنفسك."
                }

            _isAnswerSubmitted.value = true
            _quizAttemptState.update { state ->
                state.copy(
                    isSolvedOrRevealed = true,
                    wasRevealedWithoutSolving = false,
                    activeGuidanceText = affirmation,
                    isGuidanceCorrect = true,
                    firstTryCorrectCount = state.firstTryCorrectCount + if (isFirstTry) 1 else 0,
                    guidedCorrectCount = state.guidedCorrectCount + if (!isFirstTry) 1 else 0
                )
            }
        } else {
            val updatedWrong = currentAttempt.attemptedWrongIndices + selectedIdx
            val targetedHint = currentQuestion.optionHints.getOrNull(selectedIdx)
                ?.takeIf { it.isNotBlank() }
                ?: currentQuestion.socraticHint.takeIf { it.isNotBlank() }
                ?: "هذا الخيار يحتاج إلى إعادة نظر. فكر في القاعدة الأساسية أو المعطيات في السؤال وحاول اختياراً آخر — أو اطلب توجيهاً إضافياً من المعلم الذكي."

            _selectedAnswerIndex.value = null
            _isAnswerSubmitted.value = false
            _guidedHintState.value = GuidedHintUiState.Idle
            _quizAttemptState.update { state ->
                state.copy(
                    attemptedWrongIndices = updatedWrong,
                    totalWrongAttempts = state.totalWrongAttempts + 1,
                    activeGuidanceText = targetedHint,
                    isGuidanceCorrect = false
                )
            }
        }
    }

    fun requestPreAttemptHint() {
        val currentQuestion = _quizQuestions.value.getOrNull(_currentQuizIndex.value) ?: return
        val attempt = _quizAttemptState.value
        if (attempt.isSolvedOrRevealed) return

        val presetHint = currentQuestion.socraticHint.trim()
        if (presetHint.isNotBlank() && !attempt.hasRequestedPreHint) {
            _quizAttemptState.update {
                it.copy(
                    hasRequestedPreHint = true,
                    activeGuidanceText = presetHint,
                    isGuidanceCorrect = false
                )
            }
            return
        }

        requestDeepSocraticGuidance()
    }

    fun requestDeepSocraticGuidance() {
        val currentQuestion = _quizQuestions.value.getOrNull(_currentQuizIndex.value) ?: return
        val attempt = _quizAttemptState.value
        if (attempt.isSolvedOrRevealed) return

        val wrongAttemptsSummary = if (attempt.attemptedWrongIndices.isEmpty()) {
            "لم يجرب الطالب أي خيار بعد، ويطلب تلميحاً تمهيدياً قبل الاختيار."
        } else {
            attempt.attemptedWrongIndices.joinToString(" ، ") { idx ->
                currentQuestion.options.getOrNull(idx).orEmpty()
            }.let { "جرّب الطالب هذه الخيارات الخاطئة: [$it]." }
        }

        val correctOptionText = currentQuestion.options.getOrNull(currentQuestion.correctIndex).orEmpty()
        val prompt = """
            الموضوع: ${_quizTitle.value}
            السؤال: ${currentQuestion.question}
            الخيارات المتاحة:
            ${currentQuestion.options.mapIndexed { i, opt -> "${i + 1}. $opt" }.joinToString("\n")}
            
            معلومة سرية للمعلم فقط (ممنوع كشفها أو ذكر نصها للطالب): الإجابة الصحيحة هي "$correctOptionText".
            وضع الطالب الحالي: $wrongAttemptsSummary
            
            المطلوب: أعطِ الطالب توجيهاً إرشادياً مركزاً (سؤالاً موجِّهاً أو تلميحاً للمبدأ العلمي/المنطقي) يساعده على استبعاد الخطأ والوصول إلى الحل الصحيح بنفسه دون التصريح بالإجابة إطلاقاً.
        """.trimIndent()

        guidedHintJob?.cancel()
        _guidedHintState.value = GuidedHintUiState.Loading
        _quizAttemptState.update { it.copy(hasRequestedPreHint = true) }

        guidedHintJob = viewModelScope.launch {
            val builder = StringBuilder()
            try {
                withTimeout(60_000L) {
                    try {
                        socraticCoachModel.generateContentStream(prompt).collect { chunk ->
                            val text = chunk.text.orEmpty()
                            if (text.isNotEmpty()) {
                                builder.append(text)
                                _guidedHintState.value = GuidedHintUiState.Streaming(builder.toString())
                            }
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.w(TAG, "Primary Socratic coach stream failed, trying fallback", e)
                    }

                    if (builder.isBlank()) {
                        val fallbackResp = socraticCoachFallbackModel.generateContent(prompt).text.orEmpty()
                        if (fallbackResp.isNotBlank()) {
                            builder.append(fallbackResp)
                        }
                    }
                }

                val finalHint = builder.toString().trim()
                if (finalHint.isNotBlank()) {
                    _guidedHintState.value = GuidedHintUiState.Ready(finalHint)
                    _quizAttemptState.update {
                        it.copy(
                            activeGuidanceText = finalHint,
                            isGuidanceCorrect = false
                        )
                    }
                } else {
                    _guidedHintState.value = GuidedHintUiState.Error("لم يتمكن المعلم الذكي من توليد توجيه حالياً.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Socratic guidance timed out", e)
                _guidedHintState.value = GuidedHintUiState.Error("استغرق جلب التوجيه وقتاً طويلاً. حاول مرة أخرى.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to generate Socratic guidance", e)
                _guidedHintState.value = GuidedHintUiState.Error(
                    e.localizedMessage ?: "تعذر جلب التوجيه الذكي حالياً."
                )
            }
        }
    }

    fun revealCurrentQuestionAnswer() {
        val currentQuestion = _quizQuestions.value.getOrNull(_currentQuizIndex.value) ?: return
        val attempt = _quizAttemptState.value
        if (attempt.isSolvedOrRevealed) return

        guidedHintJob?.cancel()
        _guidedHintState.value = GuidedHintUiState.Idle
        _selectedAnswerIndex.value = currentQuestion.correctIndex
        _isAnswerSubmitted.value = true
        _quizAttemptState.update { state ->
            state.copy(
                isSolvedOrRevealed = true,
                wasRevealedWithoutSolving = true,
                activeGuidanceText = "تم كشف الإجابة بناءً على طلبك — راجع الشرح المنهجي أسفله لتثبيت الفكرة.",
                isGuidanceCorrect = false,
                revealedCount = state.revealedCount + 1
            )
        }
    }

    fun nextQuizQuestion() {
        guidedHintJob?.cancel()
        _guidedHintState.value = GuidedHintUiState.Idle
        val total = _quizQuestions.value.size
        if (_currentQuizIndex.value + 1 < total) {
            _currentQuizIndex.value = _currentQuizIndex.value + 1
            _selectedAnswerIndex.value = null
            _isAnswerSubmitted.value = false
            _quizAttemptState.update { state ->
                state.copy(
                    attemptedWrongIndices = emptySet(),
                    isSolvedOrRevealed = false,
                    wasRevealedWithoutSolving = false,
                    activeGuidanceText = null,
                    isGuidanceCorrect = false,
                    hasRequestedPreHint = false
                )
            }
        } else {
            val currentAttempt = _quizAttemptState.value
            val totalQuestions = _quizQuestions.value.size
            val shouldRecord = !currentAttempt.hasRecordedResult && totalQuestions > 0
            _quizAttemptState.update {
                it.copy(
                    isQuizFinished = true,
                    hasRecordedResult = true
                )
            }
            if (shouldRecord) {
                val record = QuizResultRecord(
                    subjectTitle = activeQuizSubjectTitle,
                    topic = activeQuizTopic,
                    quizTitle = _quizTitle.value,
                    totalQuestions = totalQuestions,
                    firstTryCorrectCount = currentAttempt.firstTryCorrectCount,
                    guidedCorrectCount = currentAttempt.guidedCorrectCount,
                    revealedCount = currentAttempt.revealedCount,
                    totalWrongAttempts = currentAttempt.totalWrongAttempts,
                    completedAt = System.currentTimeMillis()
                )
                viewModelScope.launch {
                    try {
                        quizProgressRepository.recordQuizResult(record)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to save quiz progress result to Room", e)
                    }
                }
            }
        }
    }

    fun resetQuiz() {
        guidedHintJob?.cancel()
        _guidedHintState.value = GuidedHintUiState.Idle
        _currentQuizIndex.value = 0
        _selectedAnswerIndex.value = null
        _isAnswerSubmitted.value = false
        _quizAttemptState.value = QuizAttemptUiState()
    }

    fun loadSubjectQuiz(
        questions: List<QuizQuestion>,
        title: String = "مقرر التاريخ: الثورة المهدية",
        subjectTitle: String = "",
        topic: String = ""
    ) {
        if (questions.isNotEmpty()) {
            _quizQuestions.value = questions
            _quizTitle.value = title
            activeQuizSubjectTitle = subjectTitle.trim().ifBlank {
                inferSubjectFromQuizTitle(title)
            }
            activeQuizTopic = topic.trim().ifBlank { title }
            _isConfiguringAiQuiz.value = false
            resetQuiz()
        }
    }

    private fun inferSubjectFromQuizTitle(title: String): String {
        return CurriculumData.subjects.firstOrNull { sub ->
            title.contains(sub.title, ignoreCase = true) ||
                title.contains(sub.category.titleAr, ignoreCase = true)
        }?.title ?: title.substringAfter("مقرر ", title).substringBefore(":").trim()
            .ifBlank { "عام — الشهادة السودانية" }
    }

    fun generatePersonalizedStudyPlan() {
        val currentResults = quizResults.value
        val recommendations = ProgressAssessmentEngine.buildStudyRecommendations(currentResults)
        if (recommendations.isEmpty()) {
            _aiStudyPlanState.value = AiStudyPlanUiState.Ready(
                "أداؤك ممتاز في جميع المواضيع المسجلة (80% فأكثر)! استمر في خوض اختبارات تحدي المتفوقين للحفاظ على جاهزيتك."
            )
            return
        }

        val weakSummary = recommendations.take(5).joinToString("\n") { rec ->
            "- المادة: ${rec.subjectTitle} | الموضوع: ${rec.topic} | الإتقان: ${rec.masteryPercent}% | السبب: ${rec.weaknessReason}"
        }

        val prompt = """
            أنت مستشار أكاديمي خبير لطلاب الشهادة الثانوية السودانية.
            بناءً على نتائج اختبارات الطالب الفعلية التالية في المواضيع التي أظهر فيها ضعفاً:
            $weakSummary
            
            المطلوب:
            ضع للطالب خطة مراجعة عملية ومختصرة (100 إلى 180 كلمة) مرتبة حسب الأولوية:
            1. حدد له المواد والمواضيع التي يبدأ بمراجعتها أولاً ولماذا.
            2. اقترح لكل موضوع ضعيف خطوة مذاكرة عملية (مثل مراجعة القوانين الأساسية، تلخيص المفاهيم، ثم إعادة الاختبار بالمستوى التأسيسي).
            3. اختم برسالة تحفيزية قصيرة ومشجعة.
        """.trimIndent()

        studyPlanJob?.cancel()
        _aiStudyPlanState.value = AiStudyPlanUiState.Loading
        studyPlanJob = viewModelScope.launch {
            val builder = StringBuilder()
            try {
                withTimeout(60_000L) {
                    val primary = getModelForRole(TutorRole.GENERAL, withSearchGrounding = false)
                    try {
                        primary.generateContentStream(prompt).collect { chunk ->
                            val text = chunk.text.orEmpty()
                            if (text.isNotEmpty()) {
                                builder.append(text)
                                _aiStudyPlanState.value = AiStudyPlanUiState.Streaming(builder.toString())
                            }
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.w(TAG, "Primary study plan stream failed, trying fallback", e)
                    }

                    if (builder.isBlank()) {
                        val fallback = getFallbackModel(TutorRole.GENERAL, useSearchGrounding = false, fast = true)
                        val resp = fallback.generateContent(prompt).text.orEmpty()
                        if (resp.isNotBlank()) builder.append(resp)
                    }
                }

                val finalPlan = builder.toString().trim()
                if (finalPlan.isNotBlank()) {
                    _aiStudyPlanState.value = AiStudyPlanUiState.Ready(finalPlan)
                } else {
                    _aiStudyPlanState.value = AiStudyPlanUiState.Error("تعذر توليد خطة المراجعة حالياً. حاول مرة أخرى.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Personalized study plan timed out", e)
                _aiStudyPlanState.value = AiStudyPlanUiState.Error("استغرق إعداد خطة المراجعة وقتاً طويلاً. يرجى المحاولة مجدداً.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to generate personalized study plan", e)
                _aiStudyPlanState.value = AiStudyPlanUiState.Error(
                    e.localizedMessage ?: "تعذر إنشاء خطة المراجعة المخصصة حالياً."
                )
            }
        }
    }

    fun deleteQuizResult(id: Long) {
        viewModelScope.launch {
            try {
                quizProgressRepository.deleteResult(id)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete quiz result from Room", e)
            }
        }
    }

    fun clearQuizProgressHistory() {
        _aiStudyPlanState.value = AiStudyPlanUiState.Idle
        viewModelScope.launch {
            try {
                quizProgressRepository.clearAll()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear quiz progress history", e)
            }
        }
    }

    // --- Study Schedule State Management ---
    private val _studySessions = MutableStateFlow<List<com.example.model.StudySession>>(CurriculumData.defaultStudySessions)
    val studySessions: StateFlow<List<com.example.model.StudySession>> = _studySessions.asStateFlow()

    fun addStudySession(session: com.example.model.StudySession, context: android.content.Context) {
        _studySessions.value = _studySessions.value + session
        if (session.reminderEnabled) {
            com.example.util.StudyNotificationUtil.scheduleStudyReminder(
                context = context,
                sessionId = session.id,
                subjectTitle = session.subjectTitle,
                topic = session.topic,
                dayOfWeek = session.day.dayNumber,
                hour = session.hour,
                minute = session.minute,
                isDaily = session.period == com.example.model.SchedulePeriod.DAILY
            )
        }
    }

    fun toggleSessionCompleted(sessionId: String) {
        _studySessions.value = _studySessions.value.map {
            if (it.id == sessionId) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    fun deleteStudySession(sessionId: String, context: android.content.Context) {
        com.example.util.StudyNotificationUtil.cancelStudyReminder(context, sessionId)
        _studySessions.value = _studySessions.value.filter { it.id != sessionId }
    }

    fun toggleSessionReminder(sessionId: String, context: android.content.Context) {
        _studySessions.value = _studySessions.value.map { session ->
            if (session.id == sessionId) {
                val newEnabled = !session.reminderEnabled
                if (newEnabled) {
                    com.example.util.StudyNotificationUtil.scheduleStudyReminder(
                        context = context,
                        sessionId = session.id,
                        subjectTitle = session.subjectTitle,
                        topic = session.topic,
                        dayOfWeek = session.day.dayNumber,
                        hour = session.hour,
                        minute = session.minute,
                        isDaily = session.period == com.example.model.SchedulePeriod.DAILY
                    )
                } else {
                    com.example.util.StudyNotificationUtil.cancelStudyReminder(context, session.id)
                }
                session.copy(reminderEnabled = newEnabled)
            } else {
                session
            }
        }
    }

    fun testReminderNotification(context: android.content.Context, session: com.example.model.StudySession) {
        com.example.util.StudyNotificationUtil.showImmediateNotification(
            context = context,
            notificationId = session.id.hashCode(),
            title = "⏰ تذكير فوري: ${session.subjectTitle}",
            message = "يا بطل الشهادة السودانية! حان وقت جلسة «${session.topic}». استعن بالله وابدأ المذاكرة الآن!"
        )
    }

    fun restoreDefaultSchedule(context: android.content.Context) {
        _studySessions.value.forEach {
            com.example.util.StudyNotificationUtil.cancelStudyReminder(context, it.id)
        }
        _studySessions.value = CurriculumData.defaultStudySessions
        _studySessions.value.forEach { session ->
            if (session.reminderEnabled) {
                com.example.util.StudyNotificationUtil.scheduleStudyReminder(
                    context = context,
                    sessionId = session.id,
                    subjectTitle = session.subjectTitle,
                    topic = session.topic,
                    dayOfWeek = session.day.dayNumber,
                    hour = session.hour,
                    minute = session.minute,
                    isDaily = session.period == com.example.model.SchedulePeriod.DAILY
                )
            }
        }
    }

    // --- Educational Notes State Management (Room Local Database) ---
    fun saveStudyNote(
        title: String,
        content: String,
        subjectTitle: String,
        isPinned: Boolean = false,
        existingNote: StudyNote? = null
    ) {
        val cleanTitle = title.trim()
        val cleanContent = content.trim()
        val cleanSubject = subjectTitle.trim().ifEmpty { "عام" }
        if (cleanTitle.isEmpty() && cleanContent.isEmpty()) return

        val finalTitle = cleanTitle.ifEmpty {
            cleanContent.lineSequence().firstOrNull()?.take(48).orEmpty().ifEmpty { "ملاحظة دراسية" }
        }
        val finalContent = cleanContent.ifEmpty { finalTitle }
        val now = System.currentTimeMillis()

        viewModelScope.launch {
            try {
                if (existingNote != null && existingNote.id > 0L) {
                    studyNoteRepository.updateNote(
                        existingNote.copy(
                            title = finalTitle,
                            content = finalContent,
                            subjectTitle = cleanSubject,
                            isPinned = isPinned,
                            updatedAt = now
                        )
                    )
                } else {
                    studyNoteRepository.saveNote(
                        StudyNote(
                            title = finalTitle,
                            content = finalContent,
                            subjectTitle = cleanSubject,
                            isPinned = isPinned,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save study note to Room DB", e)
            }
        }
    }

    fun togglePinStudyNote(note: StudyNote) {
        if (note.id <= 0L) return
        viewModelScope.launch {
            try {
                studyNoteRepository.togglePin(note.id, !note.isPinned)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to toggle note pin in Room DB", e)
            }
        }
    }

    fun deleteStudyNote(noteId: Long) {
        if (noteId <= 0L) return
        viewModelScope.launch {
            try {
                studyNoteRepository.deleteNote(noteId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete note from Room DB", e)
            }
        }
    }

    // --- Pomodoro Study Timer Engine & Controls ---
    private fun durationSecondsForPhase(state: PomodoroUiState, phase: PomodoroPhase): Int {
        return when (phase) {
            PomodoroPhase.FOCUS -> state.focusMinutes * 60
            PomodoroPhase.SHORT_BREAK -> state.shortBreakMinutes * 60
            PomodoroPhase.LONG_BREAK -> state.longBreakMinutes * 60
        }
    }

    fun startOrPausePomodoro() {
        val currentlyRunning = _pomodoroState.value.isRunning
        if (currentlyRunning) {
            pausePomodoro()
        } else {
            startPomodoro()
        }
    }

    fun startPomodoro() {
        if (_pomodoroState.value.isRunning) return
        _pomodoroState.update { state ->
            val safeRemaining = if (state.remainingSeconds <= 0) {
                durationSecondsForPhase(state, state.phase)
            } else {
                state.remainingSeconds
            }
            state.copy(
                isRunning = true,
                remainingSeconds = safeRemaining,
                lastCompletedMessage = null
            )
        }
        launchPomodoroTicker()
    }

    fun pausePomodoro() {
        pomodoroTickerJob?.cancel()
        pomodoroTickerJob = null
        _pomodoroState.update { it.copy(isRunning = false) }
    }

    fun resetPomodoro() {
        pomodoroTickerJob?.cancel()
        pomodoroTickerJob = null
        _pomodoroState.update { state ->
            val phaseSeconds = durationSecondsForPhase(state, state.phase)
            state.copy(
                isRunning = false,
                remainingSeconds = phaseSeconds,
                totalPhaseSeconds = phaseSeconds,
                lastCompletedMessage = null
            )
        }
    }

    fun selectPomodoroPhase(phase: PomodoroPhase) {
        pomodoroTickerJob?.cancel()
        pomodoroTickerJob = null
        _pomodoroState.update { state ->
            val phaseSeconds = durationSecondsForPhase(state, phase)
            state.copy(
                phase = phase,
                isRunning = false,
                remainingSeconds = phaseSeconds,
                totalPhaseSeconds = phaseSeconds,
                lastCompletedMessage = null
            )
        }
    }

    fun skipPomodoroPhase() {
        pomodoroTickerJob?.cancel()
        pomodoroTickerJob = null
        _pomodoroState.update { state ->
            val nextPhase = when (state.phase) {
                PomodoroPhase.FOCUS -> if ((state.cycleFocusCount + 1) >= 4) PomodoroPhase.LONG_BREAK else PomodoroPhase.SHORT_BREAK
                PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> PomodoroPhase.FOCUS
            }
            val nextSeconds = durationSecondsForPhase(state, nextPhase)
            state.copy(
                phase = nextPhase,
                isRunning = false,
                remainingSeconds = nextSeconds,
                totalPhaseSeconds = nextSeconds,
                lastCompletedMessage = null
            )
        }
    }

    fun applyPomodoroPreset(preset: PomodoroPreset) {
        updateCustomPomodoroDurations(
            focusMinutes = preset.focusMinutes,
            shortBreakMinutes = preset.shortBreakMinutes,
            longBreakMinutes = preset.longBreakMinutes
        )
    }

    fun updateCustomPomodoroDurations(
        focusMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int
    ) {
        val safeFocus = focusMinutes.coerceIn(5, 120)
        val safeShort = shortBreakMinutes.coerceIn(1, 30)
        val safeLong = longBreakMinutes.coerceIn(5, 60)

        pomodoroTickerJob?.cancel()
        pomodoroTickerJob = null

        _pomodoroState.update { state ->
            val updated = state.copy(
                isRunning = false,
                focusMinutes = safeFocus,
                shortBreakMinutes = safeShort,
                longBreakMinutes = safeLong,
                lastCompletedMessage = null
            )
            val newSecs = durationSecondsForPhase(updated, updated.phase)
            updated.copy(
                remainingSeconds = newSecs,
                totalPhaseSeconds = newSecs
            )
        }

        viewModelScope.launch {
            try {
                settingsRepository.setPomodoroDurations(safeFocus, safeShort, safeLong)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to persist Pomodoro durations", e)
            }
        }
    }

    fun addExtraPomodoroMinutes(extraMinutes: Int = 5) {
        val extraSeconds = extraMinutes.coerceIn(1, 30) * 60
        _pomodoroState.update { state ->
            state.copy(
                remainingSeconds = state.remainingSeconds + extraSeconds,
                totalPhaseSeconds = maxOf(state.totalPhaseSeconds, state.remainingSeconds + extraSeconds)
            )
        }
    }

    fun setPomodoroSubject(subjectTitle: String) {
        val clean = subjectTitle.trim().ifEmpty { "عام — الشهادة السودانية" }
        _pomodoroState.update { it.copy(selectedSubjectTitle = clean) }
    }

    fun setPomodoroGoal(goal: String) {
        _pomodoroState.update { it.copy(sessionGoal = goal) }
    }

    fun togglePomodoroSound(enabled: Boolean) {
        _pomodoroState.update { it.copy(soundEnabled = enabled) }
        viewModelScope.launch {
            try {
                settingsRepository.setPomodoroSoundEnabled(enabled)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to persist Pomodoro sound toggle", e)
            }
        }
    }

    fun togglePomodoroAutoStart(enabled: Boolean) {
        _pomodoroState.update { it.copy(autoStartNext = enabled) }
        viewModelScope.launch {
            try {
                settingsRepository.setPomodoroAutoStartNext(enabled)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to persist Pomodoro auto-start toggle", e)
            }
        }
    }

    fun clearPomodoroCompletionBanner() {
        _pomodoroState.update { it.copy(lastCompletedMessage = null) }
    }

    fun resetPomodoroStatistics() {
        _pomodoroState.update {
            it.copy(
                cycleFocusCount = 0,
                completedSessionsTotal = 0,
                totalFocusMinutes = 0
            )
        }
        viewModelScope.launch {
            try {
                settingsRepository.resetPomodoroStats()
                pomodoroStatsRepository.clearAll()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reset Pomodoro statistics", e)
            }
        }
    }

    fun deletePomodoroSessionLog(sessionId: Long) {
        viewModelScope.launch {
            try {
                pomodoroStatsRepository.deleteSession(sessionId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete Pomodoro session log", e)
            }
        }
    }

    private fun launchPomodoroTicker() {
        pomodoroTickerJob?.cancel()
        pomodoroTickerJob = viewModelScope.launch {
            while (isActive && _pomodoroState.value.isRunning) {
                delay(1000L)
                if (!_pomodoroState.value.isRunning) break

                val current = _pomodoroState.value
                if (current.remainingSeconds > 1) {
                    _pomodoroState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
                } else {
                    handlePomodoroPhaseCompletion()
                    if (!_pomodoroState.value.isRunning) {
                        break
                    }
                }
            }
        }
    }

    private fun handlePomodoroPhaseCompletion() {
        val current = _pomodoroState.value
        val finishedPhase = current.phase
        val shouldAutoStart = current.autoStartNext

        if (finishedPhase == PomodoroPhase.FOCUS) {
            val nextCycleRaw = current.cycleFocusCount + 1
            val nextPhase = if (nextCycleRaw >= 4) PomodoroPhase.LONG_BREAK else PomodoroPhase.SHORT_BREAK
            val wrappedCycle = if (nextCycleRaw >= 4) 0 else nextCycleRaw
            val nextSeconds = durationSecondsForPhase(current, nextPhase)

            _pomodoroState.update { state ->
                state.copy(
                    phase = nextPhase,
                    isRunning = shouldAutoStart,
                    remainingSeconds = nextSeconds,
                    totalPhaseSeconds = nextSeconds,
                    cycleFocusCount = wrappedCycle,
                    completedSessionsTotal = state.completedSessionsTotal + 1,
                    totalFocusMinutes = state.totalFocusMinutes + state.focusMinutes,
                    lastCompletedMessage = "أحسنت يا بطل! أتممت جلسة تركيز (${state.focusMinutes} دقيقة) في «${state.selectedSubjectTitle}»."
                )
            }

            viewModelScope.launch {
                try {
                    settingsRepository.recordPomodoroFocusCompletion(current.focusMinutes)
                    pomodoroStatsRepository.recordCompletedSession(
                        subjectTitle = current.selectedSubjectTitle,
                        durationMinutes = current.focusMinutes,
                        goal = current.sessionGoal
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to record Pomodoro session completion", e)
                }
            }
        } else {
            val nextSeconds = durationSecondsForPhase(current, PomodoroPhase.FOCUS)
            _pomodoroState.update { state ->
                state.copy(
                    phase = PomodoroPhase.FOCUS,
                    isRunning = shouldAutoStart,
                    remainingSeconds = nextSeconds,
                    totalPhaseSeconds = nextSeconds,
                    lastCompletedMessage = "انتهت الاستراحة! استعد لبدء جولة تركيز جديدة في «${state.selectedSubjectTitle}»."
                )
            }
        }

        val appContext = getApplication<Application>()
        PomodoroFeedbackUtil.notifyPhaseCompleted(
            context = appContext,
            finishedPhase = finishedPhase,
            subjectTitle = current.selectedSubjectTitle,
            playSound = current.soundEnabled,
            sendNotification = settings.value.studyRemindersEnabled
        )

        if (!shouldAutoStart) {
            pomodoroTickerJob = null
        }
    }

    fun refreshTeacherTips(category: TeacherTipCategory = TeacherTipCategory.ALL) {
        teacherTipsJob?.cancel()
        _teacherTipsRefreshState.value = TeacherTipsRefreshState.Loading

        val streamTitle = settings.value.academicStream.titleAr
        val existingTitles = _teacherTips.value.take(12).joinToString(" | ") { it.title }
        val focusDescription = if (category == TeacherTipCategory.ALL) {
            "متنوعة تشمل التعامل مع قلق الاختبارات واستراتيجيات مذاكرة المواد العلمية والأدبية وتنظيم الوقت"
        } else {
            "مركزة حول محور: «${category.titleAr}»"
        }

        val prompt = """
            ولّد 3 نصائح جديدة ومتجددة لطلاب الشهادة الثانوية السودانية ($streamTitle).
            التركيز المطلوب: $focusDescription.
            تجنب تكرار عناوين النصائح الحالية التالية:
            $existingTitles
            اجعل كل نصيحة عملية وملهمة، مع قاعدة ذهبية و2 إلى 3 خطوات تطبيقية واضحة.
        """.trimIndent()

        teacherTipsJob = viewModelScope.launch {
            try {
                val rawJson = withTimeout(45_000L) {
                    val primaryText = try {
                        teacherTipsModel.generateContent(prompt).text
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.w(TAG, "Primary teacher tips model failed, trying fallback", e)
                        null
                    }
                    primaryText?.takeIf { it.isNotBlank() }
                        ?: teacherTipsFallbackModel.generateContent(prompt).text
                } ?: throw IllegalStateException("لم تُستلم نصائح جديدة من المعلم الذكي.")

                val parsedTips = TeacherTipsJsonParser.parse(
                    rawJson = rawJson,
                    fallbackCategory = category,
                    defaultStreamTag = streamTitle
                )

                val currentList = _teacherTips.value
                val existingNormalizedTitles = currentList.map { it.title.trim() }.toSet()
                val uniqueFreshTips = parsedTips.filterNot { it.title.trim() in existingNormalizedTitles }
                val finalAdditions = if (uniqueFreshTips.isNotEmpty()) uniqueFreshTips else parsedTips

                _teacherTips.value = finalAdditions + currentList
                _featuredTipIndex.value = 0
                _teacherTipsRefreshState.value = TeacherTipsRefreshState.Success(
                    addedCount = finalAdditions.size,
                    categoryTitle = category.titleAr
                )
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Teacher tips refresh timed out", e)
                _teacherTipsRefreshState.value = TeacherTipsRefreshState.Error(
                    "استغرق تجديد النصائح وقتاً طويلاً (45 ثانية). تحقق من اتصال الإنترنت وحاول مرة أخرى."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to refresh teacher tips via Firebase AI Logic", e)
                _teacherTipsRefreshState.value = TeacherTipsRefreshState.Error(
                    e.localizedMessage ?: "تعذر توليد نصائح جديدة حالياً. يرجى المحاولة مجدداً."
                )
            }
        }
    }

    fun cycleFeaturedTip() {
        val total = _teacherTips.value.size
        if (total > 0) {
            _featuredTipIndex.value = (_featuredTipIndex.value + 1) % total
        }
    }

    fun dismissTeacherTipsRefreshState() {
        _teacherTipsRefreshState.value = TeacherTipsRefreshState.Idle
    }

    override fun onCleared() {
        pomodoroTickerJob?.cancel()
        teacherTipsJob?.cancel()
        super.onCleared()
    }
}
