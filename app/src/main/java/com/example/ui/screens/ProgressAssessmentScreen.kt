package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.model.AiStudyPlanUiState
import com.example.model.MasteryLevel
import com.example.model.QuizDifficulty
import com.example.model.QuizResultRecord
import com.example.model.RecommendedStudyMaterial
import com.example.model.Subject
import com.example.model.SubjectProgressSummary
import com.example.model.Textbook
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
import com.example.util.ProgressAssessmentEngine
import com.example.viewmodel.TutorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressAssessmentScreen(
    viewModel: TutorViewModel,
    onBack: () -> Unit,
    onOpenSubjectUnit: (subject: Subject, unitId: String?) -> Unit,
    onOpenTextbook: (Textbook) -> Unit,
    onStartTargetedAiQuiz: (subjectTitle: String, topic: String, difficulty: QuizDifficulty) -> Unit,
    onAskTutorAboutTopic: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val quizResults by viewModel.quizResults.collectAsStateWithLifecycle()
    val aiStudyPlanState by viewModel.aiStudyPlanState.collectAsStateWithLifecycle()

    val overallMastery = remember(quizResults) {
        ProgressAssessmentEngine.assessOverallMastery(quizResults)
    }
    val subjectSummaries = remember(quizResults) {
        ProgressAssessmentEngine.assessBySubject(quizResults)
    }
    val recommendations = remember(quizResults) {
        ProgressAssessmentEngine.buildStudyRecommendations(quizResults)
    }
    val totalQuestionsAnswered = remember(quizResults) {
        quizResults.sumOf { it.totalQuestions }
    }
    val totalFirstTry = remember(quizResults) {
        quizResults.sumOf { it.firstTryCorrectCount }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // SudX Header
        SudXHeaderSurface {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("progress_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "العودة",
                        tint = SudXGoldTint
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SudXChampagneGold.copy(alpha = 0.16f))
                        .border(1.dp, SudXChampagneGold.copy(alpha = 0.65f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = SudXChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "تقييم التقدم والتوصيات • SudX",
                        style = MaterialTheme.typography.titleMedium,
                        color = SudXGoldTint,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "تحليل أداء الاختبارات واقتراح المراجعات للمواضيع الأضعف",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE2E8F0)
                    )
                }

                IconButton(
                    onClick = {
                        onStartTargetedAiQuiz(
                            CurriculumData.subjects.firstOrNull()?.title ?: "الرياضيات المتخصصة",
                            "",
                            QuizDifficulty.EXAM_LEVEL
                        )
                    },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("progress_open_ai_quiz_header_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "إنشاء اختبار ذكي",
                        tint = SudXChampagneGold
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 720.dp)
                .align(Alignment.CenterHorizontally),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Overall Readiness Hero Card
            item {
                val overallLevel = ProgressAssessmentEngine.masteryLevelFor(overallMastery)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = SudXMidnightNavy,
                    border = BorderStroke(
                        1.2.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                SudXChampagneGold.copy(alpha = 0.85f),
                                SudXSoftGold.copy(alpha = 0.30f),
                                SudXChampagneGold.copy(alpha = 0.65f)
                            )
                        )
                    ),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("progress_hero_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(SudXMidnightNavy, SudXRoyalNavy, SudXSapphireSlate)
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "مؤشر الجاهزية الأكاديمية",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SudXSoftGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (quizResults.isEmpty()) "لم يُقَس بعد" else overallLevel.titleAr,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SudXGoldTint
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.10f),
                                border = BorderStroke(1.dp, SudXChampagneGold.copy(alpha = 0.45f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$overallMastery%",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SudXChampagneGold
                                    )
                                    Text(
                                        text = "متوسط الإتقان",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProgressHeroPill(
                                icon = Icons.Default.Quiz,
                                label = "الاختبارات المكتملة",
                                value = "${quizResults.size} اختبار",
                                tint = SudXChampagneGold,
                                modifier = Modifier.weight(1f)
                            )
                            ProgressHeroPill(
                                icon = Icons.Default.CheckCircle,
                                label = "إتقان من أول محاولة",
                                value = "$totalFirstTry / $totalQuestionsAnswered",
                                tint = SudXEmerald,
                                modifier = Modifier.weight(1f)
                            )
                            ProgressHeroPill(
                                icon = Icons.Default.WarningAmber,
                                label = "مواضيع للمراجعة",
                                value = "${recommendations.size}",
                                tint = if (recommendations.isNotEmpty()) Color(0xFFFBBF24) else SudXEmerald,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 2. Empty State OR Weak-Topic Recommendations + Subject Assessments
            if (quizResults.isEmpty()) {
                item {
                    PremiumSurfaceCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("progress_empty_state")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(SudXGoldTint)
                                    .border(1.5.dp, SudXGoldBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = SudXBronzeGold,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "لا توجد نتائج اختبارات مسجلة بعد",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "أكمل اختباراً في مولّد الاختبارات الذكي أو من دروس المواد، وسيقوم النظام بتحليل أدائك تلقائياً واقتراح الدروس والكتب المناسبة لأي موضوع يحتاج تقوية.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PremiumActionButton(
                                onClick = {
                                    onStartTargetedAiQuiz(
                                        CurriculumData.subjects.firstOrNull()?.title ?: "الرياضيات المتخصصة",
                                        "",
                                        QuizDifficulty.EXAM_LEVEL
                                    )
                                },
                                modifier = Modifier.testTag("progress_start_first_quiz_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SudXChampagneGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ابدأ اختباراً ذكياً الآن",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // 3. Recommended Study Materials & Reviews Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = SudXBronzeGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مواد ومراجعات مقترحة لمواضع الضعف",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        if (recommendations.isNotEmpty()) {
                            Text(
                                text = "${recommendations.size} توصيات",
                                style = MaterialTheme.typography.labelMedium,
                                color = SudXCrimson,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (recommendations.isEmpty()) {
                    item {
                        PremiumSurfaceCard(
                            containerColor = SudXEmeraldSoft,
                            borderColor = SudXEmerald.copy(alpha = 0.55f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("progress_all_mastered_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = SudXEmeraldDark,
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "تقدم متميز في جميع الاختبارات!",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SudXMidnightNavy
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "جميع المواضيع التي اختبرتها حققت فيها 80% فأكثر. جرّب مستوى «تحدي المتفوقين» لرفع جاهزيتك للامتحان النهائي.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF334155),
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(recommendations, key = { it.id }) { rec ->
                        WeakTopicRecommendationCard(
                            recommendation = rec,
                            onOpenUnit = {
                                val sub = CurriculumData.subjects.firstOrNull { it.id == rec.matchedSubjectId }
                                if (sub != null) {
                                    onOpenSubjectUnit(sub, rec.matchedUnitId)
                                }
                            },
                            onOpenTextbook = {
                                val book = CurriculumData.textbooks.firstOrNull { it.id == rec.matchedTextbookId }
                                if (book != null) {
                                    onOpenTextbook(book)
                                }
                            },
                            onPracticeFoundationQuiz = {
                                onStartTargetedAiQuiz(rec.subjectTitle, rec.topic, QuizDifficulty.FOUNDATION)
                            },
                            onAskTutor = {
                                onAskTutorAboutTopic(
                                    "يا أستاذ، أظهرت نتائج اختباراتي حاجة لمراجعة موضوع «${rec.topic}» في مادة ${rec.subjectTitle} (${rec.weaknessReason}). اشرح لي المفهوم الأساسي خطوة بخطوة وأعطني تمريناً موجهاً."
                                )
                            }
                        )
                    }

                    // AI Personalized Revision Roadmap Card
                    item {
                        AiStudyPlanSectionCard(
                            state = aiStudyPlanState,
                            onGeneratePlan = { viewModel.generatePersonalizedStudyPlan() }
                        )
                    }
                }

                // 4. Subject Mastery Breakdown
                item {
                    Text(
                        text = "مستوى التقدم حسب المادة (${subjectSummaries.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(subjectSummaries, key = { it.subjectTitle }) { summary ->
                    SubjectMasterySummaryCard(
                        summary = summary,
                        onOpenSubject = {
                            val sub = CurriculumData.subjects.firstOrNull { it.id == summary.subjectId }
                            if (sub != null) {
                                onOpenSubjectUnit(sub, null)
                            } else {
                                onStartTargetedAiQuiz(summary.subjectTitle, "", QuizDifficulty.EXAM_LEVEL)
                            }
                        }
                    )
                }

                // 5. Completed Quiz Performance History
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = SudXBronzeGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "سجل أداء الاختبارات",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "${quizResults.size} اختبارات",
                            style = MaterialTheme.typography.labelMedium,
                            color = SudXBronzeGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(quizResults.take(20), key = { it.id }) { record ->
                    QuizResultHistoryCard(
                        record = record,
                        onDelete = { viewModel.deleteQuizResult(record.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(22.dp)) }
        }
    }
}

@Composable
private fun ProgressHeroPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.16f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = SudXGoldTint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFCBD5E1),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun WeakTopicRecommendationCard(
    recommendation: RecommendedStudyMaterial,
    onOpenUnit: () -> Unit,
    onOpenTextbook: () -> Unit,
    onPracticeFoundationQuiz: () -> Unit,
    onAskTutor: () -> Unit
) {
    val isUrgent = recommendation.masteryLevel == MasteryLevel.NEEDS_REVIEW
    val accentColor = if (isUrgent) SudXCrimson else SudXBronzeGold
    val badgeBg = if (isUrgent) Color(0xFFFEF2F2) else SudXGoldTint

    PremiumSurfaceCard(
        borderColor = accentColor.copy(alpha = 0.45f),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recommendation_card_${recommendation.topic}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBg,
                    border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = recommendation.priorityLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "${recommendation.masteryPercent}% إتقان",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${recommendation.subjectTitle} • ${recommendation.topic}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recommendation.weaknessReason,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (recommendation.masteryPercent / 100f).coerceIn(0.05f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = accentColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SudXNavySoft.copy(alpha = 0.72f),
                border = BorderStroke(0.8.dp, SudXRoyalNavy.copy(alpha = 0.14f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "المراجعة المقترحة: ${recommendation.suggestedActionText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SudXMidnightNavy,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Curriculum Actions (Lesson / Textbook PDF)
            if (recommendation.matchedSubjectId != null || recommendation.matchedTextbookId != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (recommendation.matchedSubjectId != null) {
                        PremiumActionButton(
                            onClick = onOpenUnit,
                            containerColor = SudXRoyalNavy,
                            contentColor = SudXGoldTint,
                            cornerRadius = 12.dp,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("rec_open_lesson_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = SudXChampagneGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "مراجعة الدرس",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (recommendation.matchedTextbookId != null) {
                        PremiumOutlinedActionButton(
                            onClick = onOpenTextbook,
                            cornerRadius = 12.dp,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("rec_open_pdf_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = SudXBronzeGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "الكتاب PDF",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Secondary Remedial Actions (Foundation Quiz + Smart Teacher)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onPracticeFoundationQuiz,
                    border = BorderStroke(1.dp, SudXChampagneGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("rec_foundation_quiz_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = null,
                        tint = SudXBronzeGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "اختبار تأسيسي",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onAskTutor,
                    border = BorderStroke(1.dp, SudXRoyalNavy.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("rec_ask_tutor_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = SudXRoyalNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "شرح المعلم",
                        style = MaterialTheme.typography.labelMedium,
                        color = SudXRoyalNavy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AiStudyPlanSectionCard(
    state: AiStudyPlanUiState,
    onGeneratePlan: () -> Unit
) {
    PremiumSurfaceCard(
        containerColor = SudXGoldTint.copy(alpha = 0.78f),
        borderColor = SudXGoldBorder,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("progress_ai_study_plan_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = SudXBronzeGold,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "خطة المراجعة الذكية المخصصة (AI)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = SudXMidnightNavy
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            when (state) {
                is AiStudyPlanUiState.Idle -> {
                    Text(
                        text = "احصل على خطة مراجعة مرتبة بالأولويات من المعلم الذكي بناءً على مواضع الضعف في نتائج اختباراتك.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF334155),
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PremiumActionButton(
                        onClick = onGeneratePlan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_ai_study_plan_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إنشاء خطة مراجعة الآن", fontWeight = FontWeight.Bold)
                    }
                }
                is AiStudyPlanUiState.Loading -> {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = SudXRoyalNavy
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "جاري تحليل نتائجك وإعداد خطة مراجعة مخصصة...",
                            style = MaterialTheme.typography.bodySmall,
                            color = SudXMidnightNavy
                        )
                    }
                }
                is AiStudyPlanUiState.Streaming -> {
                    Text(
                        text = state.planText,
                        style = MaterialTheme.typography.bodySmall,
                        color = SudXMidnightNavy,
                        lineHeight = 21.sp
                    )
                }
                is AiStudyPlanUiState.Ready -> {
                    Text(
                        text = state.planText,
                        style = MaterialTheme.typography.bodySmall,
                        color = SudXMidnightNavy,
                        lineHeight = 21.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PremiumOutlinedActionButton(
                        onClick = onGeneratePlan,
                        cornerRadius = 12.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("تحديث خطة المراجعة", fontWeight = FontWeight.Bold)
                    }
                }
                is AiStudyPlanUiState.Error -> {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = SudXCrimson
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PremiumOutlinedActionButton(
                        onClick = onGeneratePlan,
                        cornerRadius = 12.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إعادة المحاولة", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectMasterySummaryCard(
    summary: SubjectProgressSummary,
    onOpenSubject: () -> Unit
) {
    val barColor = when (summary.masteryLevel) {
        MasteryLevel.MASTERED -> SudXEmeraldDark
        MasteryLevel.DEVELOPING -> SudXChampagneGold
        MasteryLevel.NEEDS_REVIEW -> SudXCrimson
    }

    PremiumSurfaceCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subject_progress_card_${summary.subjectTitle}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = summary.subjectTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${summary.quizzesCompleted} اختبارات • ${summary.totalQuestions} سؤال • ${summary.masteryLevel.titleAr}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${summary.averageMasteryPercent}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = barColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (summary.averageMasteryPercent / 100f).coerceIn(0.04f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = barColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (summary.weakTopicsCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${summary.weakTopicsCount} مواضيع تحتاج تقوية",
                        style = MaterialTheme.typography.labelSmall,
                        color = SudXCrimson,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedButton(
                        onClick = onOpenSubject,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text(
                            text = "استعراض المادة",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizResultHistoryCard(
    record: QuizResultRecord,
    onDelete: () -> Unit
) {
    val formattedDate = remember(record.completedAt) {
        SimpleDateFormat("dd MMM • hh:mm a", Locale("ar")).format(Date(record.completedAt))
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_result_history_${record.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (record.masteryPercent >= 80) SudXEmeraldSoft else SudXGoldTint),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${record.masteryPercent}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (record.masteryPercent >= 80) SudXEmeraldDark else SudXBronzeGold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${record.subjectTitle} — ${record.topic}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "أول محاولة: ${record.firstTryCorrectCount} • بالتوجيه: ${record.guidedCorrectCount} • كُشفت: ${record.revealedCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = SudXBronzeGold
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف سجل الاختبار",
                    tint = SudXCrimson.copy(alpha = 0.78f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
