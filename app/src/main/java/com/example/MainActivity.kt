package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.CurriculumData
import com.example.data.local.AppThemeMode
import com.example.model.QuizDifficulty
import com.example.model.Subject
import com.example.model.Textbook
import com.example.ui.screens.ExamsResourcesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PdfReaderScreen
import com.example.ui.screens.PomodoroTimerScreen
import com.example.ui.screens.ProgressAssessmentScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudyNotesScreen
import com.example.ui.screens.StudyScheduleScreen
import com.example.ui.screens.StudyStatsScreen
import com.example.ui.screens.SubjectDetailScreen
import com.example.ui.screens.SubjectsGridScreen
import com.example.ui.screens.TutorChatScreen
import com.example.util.ProgressAssessmentEngine
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SudXChampagneGold
import com.example.ui.theme.SudXGoldTint
import com.example.ui.theme.SudXMidnightNavy
import com.example.ui.theme.SudXRoyalNavy
import com.example.ui.theme.SudXSapphireSlate
import com.example.util.configureAppCheck
import com.example.viewmodel.TutorViewModel

enum class NavigationTab(
    val titleAr: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    HOME("الرئيسية", Icons.Filled.Home, Icons.Outlined.Home, "tab_home"),
    SUBJECTS("المواد", Icons.Filled.AutoStories, Icons.Outlined.AutoStories, "tab_subjects"),
    EXAMS("الامتحانات", Icons.Filled.Quiz, Icons.Outlined.Quiz, "tab_exams"),
    SMART_ASSISTANT("المساعد الذكي", Icons.Filled.SmartToy, Icons.Outlined.SmartToy, "tab_smart_assistant")
}

class MainActivity : ComponentActivity() {

    private val tutorViewModel: TutorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory App Check initialization with launch intent BEFORE setContent or ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            val settings by tutorViewModel.settings.collectAsStateWithLifecycle()
            val isDarkTheme = when (settings.themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            LaunchedEffect(Unit) {
                tutorViewModel.attemptSilentGoogleSignIn(this@MainActivity)
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                // Natural Right-To-Left layout for Arabic
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    SudanCertApp(viewModel = tutorViewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        configureAppCheck(this, intent)
    }
}

@Composable
fun SudanCertApp(
    viewModel: TutorViewModel
) {
    var currentTab by remember { mutableStateOf(NavigationTab.SMART_ASSISTANT) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var selectedUnitId by remember { mutableStateOf<String?>(null) }
    var examsInitialSubTab by remember { mutableIntStateOf(0) }
    var isViewingSchedule by remember { mutableStateOf(false) }
    var isViewingPractice by remember { mutableStateOf(false) }
    var isViewingNotes by remember { mutableStateOf(false) }
    var isViewingSettings by remember { mutableStateOf(false) }
    var isViewingPomodoro by remember { mutableStateOf(false) }
    var isViewingStats by remember { mutableStateOf(false) }
    var isViewingProgress by remember { mutableStateOf(false) }
    var selectedPdfTextbook by remember { mutableStateOf<Textbook?>(null) }
    val studyNotes by viewModel.studyNotes.collectAsStateWithLifecycle()
    val currentStudent by viewModel.currentStudent.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val pomodoroState by viewModel.pomodoroState.collectAsStateWithLifecycle()
    val pomodoroSessions by viewModel.pomodoroSessions.collectAsStateWithLifecycle()
    val quizResults by viewModel.quizResults.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val fabGlowTransition = rememberInfiniteTransition(label = "fab_glow")
    val fabGlowAlpha by fabGlowTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fab_glow_alpha"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            // Prominent Floating Action Button (FAB) named 'المعلم الذكي' with SudX Obsidian & Champagne Gold glow
            if (currentTab != NavigationTab.SMART_ASSISTANT && selectedSubject == null && selectedPdfTextbook == null && !isViewingSchedule && !isViewingPractice && !isViewingNotes && !isViewingSettings && !isViewingPomodoro && !isViewingStats && !isViewingProgress) {
                val fabShape = RoundedCornerShape(18.dp)
                ExtendedFloatingActionButton(
                    onClick = {
                        selectedSubject = null
                        selectedPdfTextbook = null
                        isViewingSchedule = false
                        isViewingPractice = false
                        isViewingNotes = false
                        isViewingSettings = false
                        isViewingPomodoro = false
                        isViewingStats = false
                        isViewingProgress = false
                        currentTab = NavigationTab.SMART_ASSISTANT
                    },
                    containerColor = SudXMidnightNavy,
                    contentColor = SudXGoldTint,
                    shape = fabShape,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 4.dp
                    ),
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        SudXChampagneGold.copy(alpha = 0.55f * fabGlowAlpha),
                                        SudXSapphireSlate.copy(alpha = 0.45f * fabGlowAlpha)
                                    )
                                ),
                                cornerRadius = CornerRadius(22.dp.toPx(), 22.dp.toPx()),
                                size = size.copy(
                                    width = size.width + 8.dp.toPx(),
                                    height = size.height + 8.dp.toPx()
                                ),
                                topLeft = Offset((-4).dp.toPx(), (-4).dp.toPx())
                            )
                        }
                        .border(
                            width = 1.2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    SudXChampagneGold.copy(alpha = 0.88f),
                                    SudXGoldTint.copy(alpha = 0.38f)
                                )
                            ),
                            shape = fabShape
                        )
                        .testTag("fab_smart_teacher"),
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = "المعلم الذكي",
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "المعلم الذكي",
                                fontWeight = FontWeight.Bold,
                                color = SudXGoldTint,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = SudXChampagneGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            // Sleek, Luxury SudX Bottom Navigation Bar
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                tonalElevation = 10.dp,
                shadowElevation = 16.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_nav_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab && selectedSubject == null && selectedPdfTextbook == null && !isViewingSchedule && !isViewingPractice && !isViewingNotes && !isViewingSettings && !isViewingPomodoro && !isViewingStats && !isViewingProgress
                        val itemInteraction = remember { MutableInteractionSource() }
                        val isPressed by itemInteraction.collectIsPressedAsState()
                        val isHovered by itemInteraction.collectIsHoveredAsState()

                        val iconScale by animateFloatAsState(
                            targetValue = when {
                                isPressed -> 0.90f
                                isSelected -> 1.12f
                                isHovered -> 1.05f
                                else -> 1.0f
                            },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "nav_icon_scale_${tab.name}"
                        )

                        val iconOffsetY by animateDpAsState(
                            targetValue = if (isSelected) (-2).dp else 0.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "nav_icon_offset_${tab.name}"
                        )

                        val pillBgColor by animateColorAsState(
                            targetValue = when {
                                isSelected -> SudXRoyalNavy
                                isHovered -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                                else -> Color.Transparent
                            },
                            animationSpec = tween(durationMillis = 220),
                            label = "nav_pill_bg_${tab.name}"
                        )

                        val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant

                        val iconTint by animateColorAsState(
                            targetValue = if (isSelected) SudXChampagneGold else inactiveColor,
                            animationSpec = tween(durationMillis = 220),
                            label = "nav_icon_tint_${tab.name}"
                        )

                        val labelColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary else inactiveColor,
                            animationSpec = tween(durationMillis = 220),
                            label = "nav_label_color_${tab.name}"
                        )

                        val pillHorizontalPadding by animateDpAsState(
                            targetValue = if (isSelected) 18.dp else 12.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "nav_pill_padding_${tab.name}"
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .hoverable(interactionSource = itemInteraction)
                                .clickable(
                                    interactionSource = itemInteraction,
                                    indication = null,
                                    onClick = {
                                        selectedSubject = null
                                        selectedPdfTextbook = null
                                        isViewingSchedule = false
                                        isViewingPractice = false
                                        isViewingNotes = false
                                        isViewingSettings = false
                                        isViewingPomodoro = false
                                        isViewingStats = false
                                        isViewingProgress = false
                                        currentTab = tab
                                    }
                                )
                                .padding(vertical = 4.dp)
                                .testTag(tab.tag),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .offset(y = iconOffsetY)
                                    .clip(RoundedCornerShape(50))
                                    .background(pillBgColor)
                                    .then(
                                        if (isSelected) {
                                            Modifier.border(
                                                width = 1.dp,
                                                color = SudXChampagneGold.copy(alpha = 0.65f),
                                                shape = RoundedCornerShape(50)
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .padding(horizontal = pillHorizontalPadding, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.titleAr,
                                    tint = iconTint,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .graphicsLayer {
                                            scaleX = iconScale
                                            scaleY = iconScale
                                        }
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = tab.titleAr,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = labelColor,
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // Animated champagne gold indicator dot under active tab
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 5.dp else 0.dp)
                                    .clip(CircleShape)
                                    .background(SudXChampagneGold)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        if (selectedPdfTextbook != null) {
            PdfReaderScreen(
                textbook = selectedPdfTextbook!!,
                onBack = { selectedPdfTextbook = null },
                onAskTeacherAboutBook = { prompt ->
                    selectedPdfTextbook = null
                    selectedSubject = null
                    currentTab = NavigationTab.SMART_ASSISTANT
                    viewModel.sendMessage(prompt)
                },
                modifier = screenModifier
            )
        } else if (selectedSubject != null) {
            SubjectDetailScreen(
                subject = selectedSubject!!,
                initialSelectedUnitId = selectedUnitId,
                onBack = {
                    selectedSubject = null
                    selectedUnitId = null
                },
                onAskTeacher = { prompt ->
                    selectedSubject = null
                    isViewingSchedule = false
                    isViewingPractice = false
                    isViewingNotes = false
                    currentTab = NavigationTab.SMART_ASSISTANT
                    viewModel.sendMessage(prompt)
                },
                onStartSubjectQuiz = { questions ->
                    val subjectTitle = selectedSubject?.title ?: "الشهادة السودانية"
                    val unitTitle = selectedSubject?.units?.firstOrNull { it.sampleQuestions == questions }?.title
                        ?: "مقرر $subjectTitle"
                    selectedSubject = null
                    isViewingSchedule = false
                    isViewingNotes = false
                    viewModel.loadSubjectQuiz(
                        questions = questions,
                        title = "مقرر $subjectTitle: $unitTitle",
                        subjectTitle = subjectTitle,
                        topic = unitTitle
                    )
                    isViewingPractice = true
                },
                onSaveLessonNote = { title, content, subjectTitle, isPinned ->
                    viewModel.saveStudyNote(
                        title = title,
                        content = content,
                        subjectTitle = subjectTitle,
                        isPinned = isPinned
                    )
                },
                onOpenPdfTextbook = { book ->
                    selectedPdfTextbook = book
                },
                onStartPomodoroForSubject = { subjectTitle ->
                    viewModel.setPomodoroSubject(subjectTitle)
                    selectedSubject = null
                    isViewingPomodoro = true
                },
                onStartAiQuizForTopic = { subjectTitle, topicTitle ->
                    viewModel.openAiQuizConfigurator(subjectTitle, topicTitle)
                    selectedSubject = null
                    isViewingPractice = true
                },
                modifier = screenModifier
            )
        } else if (isViewingPomodoro) {
            PomodoroTimerScreen(
                viewModel = viewModel,
                onBack = { isViewingPomodoro = false },
                onAskTeacher = { prompt ->
                    isViewingPomodoro = false
                    currentTab = NavigationTab.SMART_ASSISTANT
                    viewModel.sendMessage(prompt)
                },
                onOpenStats = {
                    isViewingPomodoro = false
                    isViewingStats = true
                },
                modifier = screenModifier
            )
        } else if (isViewingStats) {
            StudyStatsScreen(
                viewModel = viewModel,
                onBack = { isViewingStats = false },
                onOpenPomodoroForSubject = { subjectTitle ->
                    if (!subjectTitle.isNullOrBlank()) {
                        viewModel.setPomodoroSubject(subjectTitle)
                    }
                    isViewingStats = false
                    isViewingPomodoro = true
                },
                modifier = screenModifier
            )
        } else if (isViewingProgress) {
            ProgressAssessmentScreen(
                viewModel = viewModel,
                onBack = { isViewingProgress = false },
                onOpenSubjectUnit = { subject, unitId ->
                    isViewingProgress = false
                    selectedSubject = subject
                    selectedUnitId = unitId
                },
                onOpenTextbook = { book ->
                    selectedPdfTextbook = book
                },
                onStartTargetedAiQuiz = { subjectTitle, topic, difficulty ->
                    isViewingProgress = false
                    viewModel.openAiQuizConfigurator(subjectTitle, topic)
                    viewModel.updateQuizGeneratorDifficulty(difficulty)
                    isViewingPractice = true
                },
                onAskTutorAboutTopic = { prompt ->
                    isViewingProgress = false
                    currentTab = NavigationTab.SMART_ASSISTANT
                    viewModel.sendMessage(prompt)
                },
                modifier = screenModifier
            )
        } else if (isViewingPractice) {
            androidx.activity.compose.BackHandler {
                isViewingPractice = false
            }
            com.example.ui.screens.PracticeScreen(
                viewModel = viewModel,
                onNavigateToChat = { prompt ->
                    isViewingPractice = false
                    currentTab = NavigationTab.SMART_ASSISTANT
                    viewModel.sendMessage(prompt)
                },
                onOpenProgressAssessment = {
                    isViewingPractice = false
                    isViewingProgress = true
                },
                onBack = { isViewingPractice = false },
                modifier = screenModifier
            )
        } else if (isViewingSchedule) {
            StudyScheduleScreen(
                viewModel = viewModel,
                onBack = { isViewingSchedule = false },
                onOpenPomodoro = {
                    isViewingSchedule = false
                    isViewingPomodoro = true
                },
                modifier = screenModifier
            )
        } else if (isViewingNotes) {
            StudyNotesScreen(
                viewModel = viewModel,
                onAskTeacherAboutNote = { prompt ->
                    isViewingNotes = false
                    currentTab = NavigationTab.SMART_ASSISTANT
                    viewModel.sendMessage(prompt)
                },
                onBack = { isViewingNotes = false },
                modifier = screenModifier
            )
        } else if (isViewingSettings) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToSchedule = {
                    isViewingSettings = false
                    isViewingSchedule = true
                },
                onNavigateToNotes = {
                    isViewingSettings = false
                    isViewingNotes = true
                },
                onBack = { isViewingSettings = false },
                modifier = screenModifier
            )
        } else {
            when (currentTab) {
                NavigationTab.HOME -> {
                    HomeScreen(
                        onSelectSubject = { subject ->
                            selectedSubject = subject
                            selectedUnitId = null
                        },
                        onNavigateToTextbooks = {
                            examsInitialSubTab = 1
                            currentTab = NavigationTab.EXAMS
                        },
                        onNavigateToPastExams = {
                            examsInitialSubTab = 0
                            currentTab = NavigationTab.EXAMS
                        },
                        onNavigateToSmartTeacher = {
                            currentTab = NavigationTab.SMART_ASSISTANT
                        },
                        onNavigateToSchedule = {
                            isViewingSchedule = true
                        },
                        onNavigateToNotes = {
                            isViewingNotes = true
                        },
                        notesCount = studyNotes.size,
                        pomodoroState = pomodoroState,
                        onNavigateToPomodoro = {
                            isViewingPomodoro = true
                        },
                        loggedStudyMinutes = pomodoroSessions.sumOf { it.durationMinutes },
                        onNavigateToStats = {
                            isViewingStats = true
                        },
                        onNavigateToAiQuiz = {
                            viewModel.openAiQuizConfigurator()
                            isViewingPractice = true
                        },
                        quizResultsCount = quizResults.size,
                        quizMasteryPercent = ProgressAssessmentEngine.assessOverallMastery(quizResults),
                        weakTopicsCount = ProgressAssessmentEngine.buildStudyRecommendations(quizResults).size,
                        onNavigateToProgressAssessment = {
                            isViewingProgress = true
                        },
                        studentAccount = currentStudent,
                        academicStreamTitle = settings.academicStream.titleAr,
                        onNavigateToSettings = {
                            isViewingSettings = true
                        },
                        onGoogleSignIn = {
                            viewModel.signInWithGoogle(context)
                        },
                        onOpenMahdiyaLesson = {
                            val historySubject = CurriculumData.subjects.find { it.id == "history" }
                            if (historySubject != null) {
                                selectedSubject = historySubject
                                selectedUnitId = CurriculumData.mahdistRevolutionLesson.id
                            }
                        },
                        modifier = screenModifier
                    )
                }
                NavigationTab.SUBJECTS -> {
                    SubjectsGridScreen(
                        onSelectSubject = { subject ->
                            selectedSubject = subject
                            selectedUnitId = null
                        },
                        onOpenPdfTextbook = { book ->
                            selectedPdfTextbook = book
                        },
                        modifier = screenModifier
                    )
                }
                NavigationTab.EXAMS -> {
                    ExamsResourcesScreen(
                        initialTab = examsInitialSubTab,
                        onStartExamSimulation = { exam ->
                            if (exam.sampleQuestions.isNotEmpty()) {
                                viewModel.loadSubjectQuiz(
                                    questions = exam.sampleQuestions,
                                    title = "نموذج امتحان ${exam.subjectTitle} (${exam.year})",
                                    subjectTitle = exam.subjectTitle,
                                    topic = exam.title
                                )
                                isViewingPractice = true
                            }
                        },
                        onAskTeacherAboutExam = { prompt ->
                            currentTab = NavigationTab.SMART_ASSISTANT
                            viewModel.sendMessage(prompt)
                        },
                        onOpenPdfTextbook = { book ->
                            selectedPdfTextbook = book
                        },
                        onOpenAiQuizGenerator = {
                            viewModel.openAiQuizConfigurator()
                            isViewingPractice = true
                        },
                        modifier = screenModifier
                    )
                }
                NavigationTab.SMART_ASSISTANT -> {
                    TutorChatScreen(
                        viewModel = viewModel,
                        onOpenNotes = { isViewingNotes = true },
                        onOpenSettings = { isViewingSettings = true },
                        modifier = screenModifier
                    )
                }
            }
        }
    }
}
