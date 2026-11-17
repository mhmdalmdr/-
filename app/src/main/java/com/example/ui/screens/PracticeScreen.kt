package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CurriculumData
import com.example.model.GuidedHintUiState
import com.example.model.QuizFeedbackMode
import com.example.ui.components.AiQuizGeneratorPanel
import com.example.ui.components.PremiumActionButton
import com.example.ui.components.PremiumOutlinedActionButton
import com.example.ui.components.PremiumSurfaceCard
import com.example.ui.components.SudXHeaderSurface
import com.example.ui.theme.SudXChampagneGold
import com.example.ui.theme.SudXEmerald
import com.example.ui.theme.SudXEmeraldDark
import com.example.ui.theme.SudXGoldTint
import com.example.ui.theme.SudXMidnightNavy
import com.example.ui.theme.SudXRoyalNavy
import com.example.ui.theme.SudXSapphireSlate
import com.example.ui.theme.SudXSoftGold
import com.example.ui.theme.sudxCrimsonFg
import com.example.ui.theme.sudxCrimsonSoftBg
import com.example.ui.theme.sudxEmeraldFg
import com.example.ui.theme.sudxEmeraldSoftBg
import com.example.ui.theme.sudxGoldBorder
import com.example.ui.theme.sudxGoldFg
import com.example.ui.theme.sudxGoldTintBg
import com.example.ui.theme.sudxNavyFg
import com.example.viewmodel.TutorViewModel

@Composable
fun PracticeScreen(
    viewModel: TutorViewModel,
    onNavigateToChat: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val questions by viewModel.quizQuestions.collectAsStateWithLifecycle()
    val quizTitle by viewModel.quizTitle.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentQuizIndex.collectAsStateWithLifecycle()
    val selectedIndex by viewModel.selectedAnswerIndex.collectAsStateWithLifecycle()
    val attemptState by viewModel.quizAttemptState.collectAsStateWithLifecycle()
    val guidedHintState by viewModel.guidedHintState.collectAsStateWithLifecycle()

    val isConfiguringAiQuiz by viewModel.isConfiguringAiQuiz.collectAsStateWithLifecycle()
    val generatorState by viewModel.quizGeneratorState.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.quizSelectedSubject.collectAsStateWithLifecycle()
    val topicInput by viewModel.quizTopicInput.collectAsStateWithLifecycle()
    val selectedDifficulty by viewModel.quizSelectedDifficulty.collectAsStateWithLifecycle()
    val questionCount by viewModel.quizQuestionCount.collectAsStateWithLifecycle()
    val customInstructions by viewModel.quizCustomInstructions.collectAsStateWithLifecycle()
    val feedbackMode by viewModel.quizFeedbackMode.collectAsStateWithLifecycle()

    var showQuizLibraryPicker by remember { mutableStateOf(false) }

    val curatedQuizzes = remember {
        val unitQuizzes = CurriculumData.subjects.flatMap { subject ->
            subject.units.filter { it.sampleQuestions.isNotEmpty() }.map { unit ->
                Triple("${subject.title}: ${unit.title}", subject.title, unit.sampleQuestions)
            }
        }
        val examQuizzes = CurriculumData.pastExams.filter { it.sampleQuestions.isNotEmpty() }.map { exam ->
            Triple("${exam.subjectTitle} (${exam.year})", exam.subjectTitle, exam.sampleQuestions)
        }
        unitQuizzes + examQuizzes
    }

    BackHandler {
        if (isConfiguringAiQuiz && questions.isNotEmpty()) {
            viewModel.closeAiQuizConfigurator()
        } else {
            onBack?.invoke()
        }
    }

    val currentQuestion = questions.getOrNull(currentIndex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // SudX Luxury Screen Header
        SudXHeaderSurface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onBack != null) {
                        IconButton(
                            onClick = {
                                if (isConfiguringAiQuiz && questions.isNotEmpty()) {
                                    viewModel.closeAiQuizConfigurator()
                                } else {
                                    onBack()
                                }
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("close_practice_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "العودة",
                                tint = SudXGoldTint
                            )
                        }
                    }

                    Icon(
                        imageVector = if (isConfiguringAiQuiz) Icons.Default.Psychology else Icons.Default.Quiz,
                        contentDescription = null,
                        tint = SudXChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isConfiguringAiQuiz) "مولّد الاختبارات الحر" else "بنك الأسئلة والتدريب الحر",
                            style = MaterialTheme.typography.titleMedium,
                            color = SudXGoldTint,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (isConfiguringAiQuiz) {
                                "خصص المادة والموضوع وعدد الأسئلة وشروطك بكل حرية"
                            } else {
                                "تنقّل بحرية، بدّل أسلوب التوجيه، أو ولّد اختبارك الخاص"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE2E8F0)
                        )
                    }

                    if (!isConfiguringAiQuiz) {
                        OutlinedButton(
                            onClick = { viewModel.openAiQuizConfigurator() },
                            border = BorderStroke(1.dp, SudXChampagneGold.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("open_ai_quiz_generator_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SudXChampagneGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "توليد AI",
                                color = SudXGoldTint,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else if (questions.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { viewModel.closeAiQuizConfigurator() },
                            border = BorderStroke(1.dp, SudXGoldTint.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Text(
                                text = "العودة للأسئلة",
                                color = SudXGoldTint,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }

        if (isConfiguringAiQuiz) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                AiQuizGeneratorPanel(
                    selectedSubjectTitle = selectedSubject,
                    topicInput = topicInput,
                    selectedDifficulty = selectedDifficulty,
                    questionCount = questionCount,
                    customInstructions = customInstructions,
                    selectedFeedbackMode = feedbackMode,
                    generatorState = generatorState,
                    onSelectSubject = viewModel::updateQuizGeneratorSubject,
                    onTopicChange = viewModel::updateQuizGeneratorTopic,
                    onSelectDifficulty = viewModel::updateQuizGeneratorDifficulty,
                    onSelectCount = viewModel::updateQuizGeneratorCount,
                    onCustomInstructionsChange = viewModel::updateQuizGeneratorCustomInstructions,
                    onSelectFeedbackMode = viewModel::updateQuizFeedbackMode,
                    onGenerateQuiz = viewModel::generateAiQuiz
                )
            }
        } else if (attemptState.isQuizFinished) {
            QuizCompletionSummaryView(
                quizTitle = quizTitle,
                totalQuestions = questions.size,
                firstTryCount = attemptState.firstTryCorrectCount,
                guidedCount = attemptState.guidedCorrectCount,
                revealedCount = attemptState.revealedCount,
                onRestartSameQuiz = viewModel::resetQuiz,
                onGenerateNewQuiz = { viewModel.openAiQuizConfigurator() },
                onDiscussWeakPoints = {
                    onNavigateToChat(
                        "يا أستاذ، أنهيت للتو اختبار «$quizTitle» وأجبت عن ${attemptState.firstTryCorrectCount} من ${questions.size} من المحاولة الأولى. أريد مراجعة الأفكار الأساسية التي احتجت فيها لتوجيه."
                    )
                },
                modifier = Modifier.weight(1f)
            )
        } else if (currentQuestion != null) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 700.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Freedom Control Bar: Feedback Mode + Quick Switcher + Finish Early
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            item {
                                val nextMode = if (attemptState.feedbackMode == QuizFeedbackMode.GUIDED) {
                                    QuizFeedbackMode.FREE_INSTANT
                                } else {
                                    QuizFeedbackMode.GUIDED
                                }
                                FilterChip(
                                    selected = attemptState.feedbackMode == QuizFeedbackMode.FREE_INSTANT,
                                    onClick = { viewModel.changeQuizFeedbackModeDuringQuiz(nextMode) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.SwapHoriz,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "النمط: ${attemptState.feedbackMode.titleAr}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = sudxGoldTintBg(),
                                        selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = attemptState.feedbackMode == QuizFeedbackMode.FREE_INSTANT,
                                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                                        selectedBorderColor = SudXChampagneGold
                                    ),
                                    modifier = Modifier.testTag("quiz_mode_toggle_btn")
                                )
                            }

                            item {
                                FilterChip(
                                    selected = showQuizLibraryPicker,
                                    onClick = { showQuizLibraryPicker = !showQuizLibraryPicker },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.LibraryBooks,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "اختر اختباراً آخر",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SudXRoyalNavy,
                                        selectedLabelColor = SudXGoldTint,
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier.testTag("switch_quiz_topic_btn")
                                )
                            }

                            item {
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.finishQuizEarly() },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.DoneAll,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "إنهاء وعرض النتيجة",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = sudxEmeraldSoftBg(),
                                        labelColor = sudxEmeraldFg()
                                    ),
                                    modifier = Modifier.testTag("finish_quiz_early_btn")
                                )
                            }
                        }

                        AnimatedVisibility(visible = showQuizLibraryPicker) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, sudxGoldBorder()),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "اختر من الاختبارات الجاهزة أو اضغط «توليد AI» لإنشاء أي اختبار:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(curatedQuizzes) { (title, subject, qs) ->
                                            val isCurrent = quizTitle == title
                                            FilterChip(
                                                selected = isCurrent,
                                                onClick = {
                                                    viewModel.loadSubjectQuiz(
                                                        questions = qs,
                                                        title = title,
                                                        subjectTitle = subject,
                                                        topic = title
                                                    )
                                                    showQuizLibraryPicker = false
                                                },
                                                label = {
                                                    Text(
                                                        text = title,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = SudXRoyalNavy,
                                                    selectedLabelColor = SudXGoldTint
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Question Progress Header & Linear Indicator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "السؤال ${currentIndex + 1} من ${questions.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = quizTitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / questions.size.coerceAtLeast(1) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SudXChampagneGold,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )

                        // Free Question Number Navigator Strip
                        if (questions.size > 1) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                items(questions.size) { idx ->
                                    val isCurrent = idx == currentIndex
                                    val snap = attemptState.questionSnapshots[idx]
                                    val qSolved = if (isCurrent) {
                                        attemptState.isSolvedOrRevealed && attemptState.isGuidanceCorrect
                                    } else {
                                        snap != null && snap.isSolvedOrRevealed && snap.isGuidanceCorrect
                                    }
                                    val qRevealed = if (isCurrent) {
                                        attemptState.wasRevealedWithoutSolving
                                    } else {
                                        snap?.wasRevealedWithoutSolving == true
                                    }
                                    val qHasWrong = if (isCurrent) {
                                        attemptState.attemptedWrongIndices.isNotEmpty()
                                    } else {
                                        snap?.attemptedWrongIndices?.isNotEmpty() == true
                                    }

                                    val badgeBg = when {
                                        isCurrent -> SudXRoyalNavy
                                        qSolved -> sudxEmeraldSoftBg()
                                        qRevealed -> sudxGoldTintBg()
                                        qHasWrong -> sudxCrimsonSoftBg()
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                    val badgeBorder = when {
                                        isCurrent -> SudXChampagneGold
                                        qSolved -> sudxEmeraldFg()
                                        qRevealed -> sudxGoldFg()
                                        qHasWrong -> sudxCrimsonFg().copy(alpha = 0.6f)
                                        else -> MaterialTheme.colorScheme.outlineVariant
                                    }
                                    val badgeTextColor = when {
                                        isCurrent -> SudXGoldTint
                                        qSolved -> sudxEmeraldFg()
                                        qRevealed -> sudxGoldFg()
                                        qHasWrong -> sudxCrimsonFg()
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = badgeBg,
                                        border = BorderStroke(if (isCurrent) 1.8.dp else 1.dp, badgeBorder),
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .clickable { viewModel.jumpToQuizQuestion(idx) }
                                            .testTag("quiz_jump_q_$idx")
                                    ) {
                                        Box(
                                            modifier = Modifier.size(38.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${idx + 1}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = badgeTextColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Question Card with Pre-Attempt Hint & Immediate Reveal Answer Freedom
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(SudXRoyalNavy)
                                            .border(1.dp, SudXChampagneGold.copy(alpha = 0.7f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "س${currentIndex + 1}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = SudXChampagneGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (attemptState.attemptedWrongIndices.isEmpty()) {
                                            "اختر الإجابة الأدق:"
                                        } else {
                                            "جرّب اختياراً آخر مستفيداً من التوجيه:"
                                        },
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = currentQuestion.question,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 26.sp
                            )

                            if (!attemptState.isSolvedOrRevealed) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TextButton(
                                        onClick = { viewModel.requestPreAttemptHint() },
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("quiz_request_hint_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = sudxGoldFg(),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "تلميح",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = sudxGoldFg(),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    TextButton(
                                        onClick = { viewModel.revealCurrentQuestionAnswer() },
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("quiz_reveal_answer_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = sudxNavyFg(),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "أرني الحل فوراً",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = sudxNavyFg(),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // MCQ Options
                items(currentQuestion.options.size) { optIdx ->
                    val optionText = currentQuestion.options[optIdx]
                    val isSelected = selectedIndex == optIdx
                    val isCorrectOption = optIdx == currentQuestion.correctIndex
                    val wasTriedWrong = attemptState.attemptedWrongIndices.contains(optIdx)
                    val isSolvedOrRevealed = attemptState.isSolvedOrRevealed

                    val borderColor = when {
                        isSolvedOrRevealed && isCorrectOption -> sudxEmeraldFg()
                        wasTriedWrong -> sudxCrimsonFg().copy(alpha = 0.65f)
                        !isSolvedOrRevealed && isSelected -> SudXChampagneGold
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }

                    val containerColor = when {
                        isSolvedOrRevealed && isCorrectOption -> sudxEmeraldSoftBg()
                        wasTriedWrong -> sudxCrimsonSoftBg()
                        !isSolvedOrRevealed && isSelected -> sudxGoldTintBg().copy(alpha = 0.72f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val optionTextColor = when {
                        wasTriedWrong && !isSolvedOrRevealed -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                            .clickable(enabled = !isSolvedOrRevealed && !wasTriedWrong) {
                                viewModel.selectQuizAnswer(optIdx)
                            }
                            .minimumInteractiveComponentSize()
                            .testTag("quiz_option_$optIdx"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (wasTriedWrong) 0.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSolvedOrRevealed && isCorrectOption -> SudXEmeraldDark
                                            wasTriedWrong -> sudxCrimsonFg()
                                            isSelected -> SudXRoyalNavy
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSolvedOrRevealed && isCorrectOption) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "إجابة صحيحة",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (wasTriedWrong) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "خيار غير دقيق",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${('أ'.code + optIdx).toChar()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) SudXChampagneGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = optionTextColor
                                )
                                if (wasTriedWrong && !isSolvedOrRevealed) {
                                    Text(
                                        text = "تمت التجربة — راجع التوجيه واختر بديلاً أدق",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = sudxCrimsonFg()
                                    )
                                }
                            }
                        }
                    }
                }

                // Guided Socratic Feedback Card
                val showGuidanceCard = !attemptState.activeGuidanceText.isNullOrBlank() ||
                    guidedHintState !is GuidedHintUiState.Idle ||
                    attemptState.isSolvedOrRevealed

                if (showGuidanceCard) {
                    item {
                        val isSolvedCorrectly = attemptState.isSolvedOrRevealed && attemptState.isGuidanceCorrect
                        val currentHintState = guidedHintState
                        val liveStreamingHint = when (currentHintState) {
                            is GuidedHintUiState.Streaming -> currentHintState.text
                            is GuidedHintUiState.Ready -> currentHintState.text
                            else -> null
                        }
                        val displayedGuidance = liveStreamingHint
                            ?: attemptState.activeGuidanceText
                            ?: currentQuestion.teacherGuidance

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.2.dp,
                                    color = if (isSolvedCorrectly) sudxEmeraldFg().copy(alpha = 0.6f) else sudxGoldBorder(),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("quiz_guided_feedback_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSolvedCorrectly) sudxEmeraldSoftBg() else sudxGoldTintBg()
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isSolvedCorrectly) Icons.Default.School else Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = if (isSolvedCorrectly) sudxEmeraldFg() else sudxGoldFg(),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when {
                                            isSolvedCorrectly && attemptState.attemptedWrongIndices.isEmpty() ->
                                                "ممتاز يا بطل! إجابة صحيحة من المحاولة الأولى"
                                            isSolvedCorrectly ->
                                                "أحسنت! استثمرت التوجيه ووصلت للحل الصحيح"
                                            attemptState.wasRevealedWithoutSolving ->
                                                "الإجابة والشرح المنهجي"
                                            attemptState.feedbackMode == QuizFeedbackMode.FREE_INSTANT ->
                                                "تصحيح وشرح المعلم الذكي"
                                            else ->
                                                "توجيه المعلم الذكي (بدون كشف الحل)"
                                        },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSolvedCorrectly) sudxEmeraldFg() else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                when (currentHintState) {
                                    is GuidedHintUiState.Loading -> {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = sudxGoldFg()
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "المعلم الذكي يصيغ لك توجيهاً مخصصاً...",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                    is GuidedHintUiState.Error -> {
                                        Text(
                                            text = displayedGuidance,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 22.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = currentHintState.message,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = sudxCrimsonFg()
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = displayedGuidance,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 22.sp
                                        )
                                    }
                                }

                                AnimatedVisibility(visible = !attemptState.isSolvedOrRevealed) {
                                    Column {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedButton(
                                            onClick = { viewModel.requestDeepSocraticGuidance() },
                                            enabled = guidedHintState !is GuidedHintUiState.Loading,
                                            border = BorderStroke(1.dp, sudxNavyFg().copy(alpha = 0.55f)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .minimumInteractiveComponentSize()
                                                .testTag("quiz_deep_ai_guidance_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = sudxGoldFg(),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "اطلب توجيهاً أعمق من المعلم الذكي (AI)",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = sudxNavyFg(),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (attemptState.isSolvedOrRevealed) {
                                    if (currentQuestion.explanation.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                                        ) {
                                            Text(
                                                text = "الشرح المنهجي: ${currentQuestion.explanation}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                lineHeight = 20.sp,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }
                                    }
                                    if (currentQuestion.teacherGuidance.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "نصيحة الامتحان: ${currentQuestion.teacherGuidance}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = sudxNavyFg(),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action & Bidirectional Navigation Row (always free to move back/skip/finish)
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!attemptState.isSolvedOrRevealed) {
                            Button(
                                onClick = { viewModel.submitQuizAnswer() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("submit_answer_button"),
                                enabled = selectedIndex != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SudXRoyalNavy,
                                    contentColor = SudXGoldTint
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = if (attemptState.attemptedWrongIndices.isEmpty()) {
                                        "تحقق من الإجابة"
                                    } else {
                                        "تحقق من المحاولة الجديدة"
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.previousQuizQuestion() },
                                enabled = currentIndex > 0,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("previous_question_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "السابق",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { viewModel.nextQuizQuestion() },
                                modifier = Modifier
                                    .weight(1.25f)
                                    .height(50.dp)
                                    .testTag("next_question_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentIndex + 1 < questions.size) SudXChampagneGold else SudXEmeraldDark,
                                    contentColor = if (currentIndex + 1 < questions.size) SudXMidnightNavy else Color.White
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                val nextLabel = when {
                                    currentIndex + 1 >= questions.size -> "عرض النتيجة النهائية"
                                    attemptState.isSolvedOrRevealed -> "السؤال التالي"
                                    else -> "تخطي / التالي"
                                }
                                Text(
                                    text = nextLabel,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (attemptState.isSolvedOrRevealed) {
                            OutlinedButton(
                                onClick = {
                                    onNavigateToChat("يا أستاذ، أريد أن تناقشني في تفاصيل هذا السؤال: ${currentQuestion.question}")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("discuss_question_tutor_btn"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = sudxGoldFg(),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ناقش السؤال مع المعلم الذكي",
                                    color = sudxNavyFg(),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizCompletionSummaryView(
    quizTitle: String,
    totalQuestions: Int,
    firstTryCount: Int,
    guidedCount: Int,
    revealedCount: Int,
    onRestartSameQuiz: () -> Unit,
    onGenerateNewQuiz: () -> Unit,
    onDiscussWeakPoints: () -> Unit,
    modifier: Modifier = Modifier
) {
    val solvedTotal = firstTryCount + guidedCount
    val masteryPercent = if (totalQuestions > 0) ((solvedTotal * 100) / totalQuestions) else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = 680.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SudXMidnightNavy,
                border = BorderStroke(
                    1.2.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            SudXChampagneGold.copy(alpha = 0.85f),
                            SudXSoftGold.copy(alpha = 0.35f),
                            SudXChampagneGold.copy(alpha = 0.7f)
                        )
                    )
                ),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_completion_summary_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(SudXMidnightNavy, SudXRoyalNavy, SudXSapphireSlate)
                            )
                        )
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(SudXChampagneGold.copy(alpha = 0.16f))
                            .border(1.4.dp, SudXChampagneGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "أحسنت! ملخص جلستك التدريبية",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = SudXGoldTint,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = quizTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$masteryPercent%",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = SudXChampagneGold
                    )
                    Text(
                        text = "معدل الوصول للحل ($solvedTotal من $totalQuestions أسئلة)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryMetricChip(
                            label = "من أول محاولة",
                            value = "$firstTryCount",
                            accent = SudXEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricChip(
                            label = "بالتوجيه الذكي",
                            value = "$guidedCount",
                            accent = SudXChampagneGold,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricChip(
                            label = "كُشفت للمراجعة",
                            value = "$revealedCount",
                            accent = Color(0xFF94A3B8),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            PremiumSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PremiumActionButton(
                        onClick = onGenerateNewQuiz,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("summary_generate_new_quiz_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("توليد اختبار ذكي جديد (مادة/موضوع آخر)", fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PremiumOutlinedActionButton(
                            onClick = onRestartSameQuiz,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("restart_quiz_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = sudxGoldFg(),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إعادة نفس الاختبار", fontWeight = FontWeight.Bold)
                        }

                        PremiumOutlinedActionButton(
                            onClick = onDiscussWeakPoints,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = sudxNavyFg(),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ناقش المعلم", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryMetricChip(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.09f),
        border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.16f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = accent
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFCBD5E1),
                textAlign = TextAlign.Center
            )
        }
    }
}
