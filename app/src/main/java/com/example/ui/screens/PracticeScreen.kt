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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.GuidedHintUiState
import com.example.ui.components.AiQuizGeneratorPanel
import com.example.ui.components.PremiumActionButton
import com.example.ui.components.PremiumOutlinedActionButton
import com.example.ui.components.PremiumSurfaceCard
import com.example.ui.components.SudXHeaderSurface
import com.example.ui.theme.SudXBronzeGold
import com.example.ui.theme.SudXChampagneGold
import com.example.ui.theme.SudXCrimson
import com.example.ui.theme.SudXEmerald
import com.example.ui.theme.SudXEmeraldDark
import com.example.ui.theme.SudXEmeraldSoft
import com.example.ui.theme.SudXGoldBorder
import com.example.ui.theme.SudXGoldTint
import com.example.ui.theme.SudXMidnightNavy
import com.example.ui.theme.SudXNavySoft
import com.example.ui.theme.SudXRoyalNavy
import com.example.ui.theme.SudXSapphireSlate
import com.example.ui.theme.SudXSoftGold
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
                            text = if (isConfiguringAiQuiz) "مولّد الاختبارات الذكي" else "التدريب التوجيهي • المعلم الذكي",
                            style = MaterialTheme.typography.titleMedium,
                            color = SudXGoldTint,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (isConfiguringAiQuiz) {
                                "أنشئ أسئلة اختيار من متعدد مخصصة حسب المادة والموضوع"
                            } else {
                                "تعلّم بالتوجيه السقراطي للوصول إلى الإجابة بنفسك"
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
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                AiQuizGeneratorPanel(
                    selectedSubjectTitle = selectedSubject,
                    topicInput = topicInput,
                    selectedDifficulty = selectedDifficulty,
                    questionCount = questionCount,
                    generatorState = generatorState,
                    onSelectSubject = viewModel::updateQuizGeneratorSubject,
                    onTopicChange = viewModel::updateQuizGeneratorTopic,
                    onSelectDifficulty = viewModel::updateQuizGeneratorDifficulty,
                    onSelectCount = viewModel::updateQuizGeneratorCount,
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
                // Progress Bar & Attempts Counter
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
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
                                textAlign = TextAlign.End
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / questions.size },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SudXChampagneGold,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                // Question Card with Pre-Attempt Guiding Hint Button
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
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

                                if (!attemptState.isSolvedOrRevealed) {
                                    TextButton(
                                        onClick = { viewModel.requestPreAttemptHint() },
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("quiz_request_hint_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = SudXBronzeGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "تلميح",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = SudXBronzeGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
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
                        }
                    }
                }

                // MCQ Options (Wrong attempts marked without revealing answer)
                items(currentQuestion.options.size) { optIdx ->
                    val optionText = currentQuestion.options[optIdx]
                    val isSelected = selectedIndex == optIdx
                    val isCorrectOption = optIdx == currentQuestion.correctIndex
                    val wasTriedWrong = attemptState.attemptedWrongIndices.contains(optIdx)
                    val isSolvedOrRevealed = attemptState.isSolvedOrRevealed

                    val borderColor = when {
                        isSolvedOrRevealed && isCorrectOption -> SudXEmeraldDark
                        wasTriedWrong -> SudXCrimson.copy(alpha = 0.55f)
                        !isSolvedOrRevealed && isSelected -> SudXChampagneGold
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }

                    val containerColor = when {
                        isSolvedOrRevealed && isCorrectOption -> SudXEmeraldSoft
                        wasTriedWrong -> Color(0xFFFEF2F2)
                        !isSolvedOrRevealed && isSelected -> SudXChampagneGold.copy(alpha = 0.14f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val optionTextColor = when {
                        isSolvedOrRevealed && isCorrectOption -> SudXMidnightNavy
                        wasTriedWrong -> SudXMidnightNavy.copy(alpha = 0.68f)
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
                                            wasTriedWrong -> SudXCrimson
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
                                        color = SudXCrimson
                                    )
                                }
                            }
                        }
                    }
                }

                // Guided Socratic Feedback Card (Visible during wrong attempts / pre-hints OR after solving)
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
                                    color = if (isSolvedCorrectly) SudXEmeraldDark.copy(alpha = 0.55f) else SudXChampagneGold.copy(alpha = 0.75f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("quiz_guided_feedback_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSolvedCorrectly) Color(0xFFF0FDF4) else SudXGoldTint
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isSolvedCorrectly) Icons.Default.School else Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = if (isSolvedCorrectly) SudXEmeraldDark else SudXBronzeGold,
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
                                            else ->
                                                "توجيه المعلم الذكي (بدون كشف الحل)"
                                        },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSolvedCorrectly) SudXEmeraldDark else SudXMidnightNavy
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                when (currentHintState) {
                                    is GuidedHintUiState.Loading -> {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = SudXBronzeGold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "المعلم الذكي يصيغ لك توجيهاً مخصصاً...",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = SudXMidnightNavy
                                            )
                                        }
                                    }
                                    is GuidedHintUiState.Error -> {
                                        Text(
                                            text = displayedGuidance,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = SudXMidnightNavy,
                                            lineHeight = 22.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = currentHintState.message,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SudXCrimson
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = displayedGuidance,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = SudXMidnightNavy,
                                            lineHeight = 22.sp
                                        )
                                    }
                                }

                                // Deeper AI Socratic Nudge + Optional Reveal Answer after trying
                                AnimatedVisibility(visible = !attemptState.isSolvedOrRevealed) {
                                    Column {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { viewModel.requestDeepSocraticGuidance() },
                                                enabled = guidedHintState !is GuidedHintUiState.Loading,
                                                border = BorderStroke(1.dp, SudXRoyalNavy.copy(alpha = 0.5f)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .minimumInteractiveComponentSize()
                                                    .testTag("quiz_deep_ai_guidance_btn")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = SudXRoyalNavy,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "توجيه أعمق (AI)",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = SudXRoyalNavy,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            if (attemptState.attemptedWrongIndices.isNotEmpty()) {
                                                TextButton(
                                                    onClick = { viewModel.revealCurrentQuestionAnswer() },
                                                    modifier = Modifier
                                                        .minimumInteractiveComponentSize()
                                                        .testTag("quiz_reveal_answer_btn")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Visibility,
                                                        contentDescription = null,
                                                        tint = SudXBronzeGold,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "أرني الحل",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = SudXBronzeGold,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Full explanation shown ONLY once solved or revealed
                                if (attemptState.isSolvedOrRevealed) {
                                    if (currentQuestion.explanation.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.White.copy(alpha = 0.72f)
                                        ) {
                                            Text(
                                                text = "الشرح المنهجي: ${currentQuestion.explanation}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF334155),
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
                                            color = SudXRoyalNavy,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Button: Check Attempt OR Next Question / Finish
                item {
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
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.nextQuizQuestion() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("next_question_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentIndex + 1 < questions.size) SudXChampagneGold else SudXEmeraldDark,
                                    contentColor = if (currentIndex + 1 < questions.size) SudXMidnightNavy else Color.White
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = if (currentIndex + 1 < questions.size) "السؤال التالي" else "عرض النتيجة النهائية",
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    onNavigateToChat("يا أستاذ، أريد أن تناقشني في تفاصيل هذا السؤال: ${currentQuestion.question}")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("discuss_question_tutor_btn"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SudXChampagneGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "نقاش الأستاذ",
                                    color = MaterialTheme.colorScheme.primary
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
                        text = "أحسنت! أتممت الاختبار التوجيهي",
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
                                tint = SudXBronzeGold,
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
                                tint = SudXRoyalNavy,
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
