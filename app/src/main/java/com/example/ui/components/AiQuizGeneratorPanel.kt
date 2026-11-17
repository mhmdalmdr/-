package com.example.ui.components

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumData
import com.example.model.QuizDifficulty
import com.example.model.QuizGeneratorUiState
import com.example.ui.theme.SudXBronzeGold
import com.example.ui.theme.SudXChampagneGold
import com.example.ui.theme.SudXCrimson
import com.example.ui.theme.SudXGoldBorder
import com.example.ui.theme.SudXGoldTint
import com.example.ui.theme.SudXMidnightNavy
import com.example.ui.theme.SudXRoyalNavy
import com.example.ui.theme.SudXSapphireSlate
import com.example.ui.theme.SudXSoftGold

@Composable
fun AiQuizGeneratorPanel(
    selectedSubjectTitle: String,
    topicInput: String,
    selectedDifficulty: QuizDifficulty,
    questionCount: Int,
    generatorState: QuizGeneratorUiState,
    onSelectSubject: (String) -> Unit,
    onTopicChange: (String) -> Unit,
    onSelectDifficulty: (QuizDifficulty) -> Unit,
    onSelectCount: (Int) -> Unit,
    onGenerateQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjectTitles = remember {
        CurriculumData.subjects.map { it.title } + "عام — الشهادة السودانية"
    }

    val suggestedTopics = remember(selectedSubjectTitle) {
        val matchedSubject = CurriculumData.subjects.firstOrNull { it.title == selectedSubjectTitle }
        val unitTitles = matchedSubject?.units?.map { it.title }.orEmpty()
        val examTopics = CurriculumData.pastExams
            .filter { it.subjectTitle == selectedSubjectTitle }
            .flatMap { it.keyTopics }
        (unitTitles + examTopics)
            .distinct()
            .take(6)
            .ifEmpty {
                listOf(
                    "أسباب الثورة المهدية",
                    "المراجعة الشاملة للمنهج",
                    "أسئلة التفكير الناقد والاستنتاج"
                )
            }
    }

    val isGenerating = generatorState is QuizGeneratorUiState.Generating

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = 700.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Explainer Banner
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SudXMidnightNavy,
                border = BorderStroke(
                    1.2.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            SudXChampagneGold.copy(alpha = 0.85f),
                            SudXSoftGold.copy(alpha = 0.25f),
                            SudXChampagneGold.copy(alpha = 0.65f)
                        )
                    )
                ),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SudXMidnightNavy,
                                    SudXRoyalNavy,
                                    SudXSapphireSlate
                                )
                            )
                        )
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(SudXChampagneGold.copy(alpha = 0.16f))
                            .border(1.2.dp, SudXChampagneGold.copy(alpha = 0.7f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مولّد الاختبارات التوجيهي (AI)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SudXGoldTint
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اختر المادة والموضوع، وسيولّد الذكاء الاصطناعي أسئلة اختيار من متعدد مع تلميحات سقراطية تقودك للإجابة بدلاً من كشفها مباشرة.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }

        // 1. Subject Selector
        item {
            PremiumSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = SudXBronzeGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. اختر المادة الدراسية",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(subjectTitles) { subject ->
                            val isSelected = selectedSubjectTitle == subject
                            FilterChip(
                                selected = isSelected,
                                onClick = { if (!isGenerating) onSelectSubject(subject) },
                                label = {
                                    Text(
                                        text = subject,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SudXRoyalNavy,
                                    selectedLabelColor = SudXGoldTint,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                                    selectedBorderColor = SudXChampagneGold
                                ),
                                modifier = Modifier.testTag("ai_quiz_subject_$subject")
                            )
                        }
                    }
                }
            }
        }

        // 2. Topic Input + Quick Chips
        item {
            PremiumSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Topic,
                            contentDescription = null,
                            tint = SudXBronzeGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. حدد موضوع الاختبار",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { if (!isGenerating) onTopicChange(it) },
                        label = { Text("اكتب الموضوع (مثال: الحركة الدائرية، التنازع، الأحماض والقواعد)") },
                        singleLine = true,
                        enabled = !isGenerating,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SudXChampagneGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_quiz_topic_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "مقترحات سريعة من المنهج:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(suggestedTopics) { suggestion ->
                            val isSelected = topicInput.trim() == suggestion
                            FilterChip(
                                selected = isSelected,
                                onClick = { if (!isGenerating) onTopicChange(suggestion) },
                                label = {
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SudXGoldTint,
                                    selectedLabelColor = SudXMidnightNavy,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                                    selectedBorderColor = SudXChampagneGold
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. Difficulty & Question Count
        item {
            PremiumSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = SudXBronzeGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. مستوى الصعوبة وعدد الأسئلة",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuizDifficulty.entries.forEach { level ->
                            val isSelected = selectedDifficulty == level
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) SudXRoyalNavy else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(
                                    width = if (isSelected) 1.3.dp else 1.dp,
                                    color = if (isSelected) SudXChampagneGold else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable(enabled = !isGenerating) { onSelectDifficulty(level) }
                                    .minimumInteractiveComponentSize()
                                    .testTag("ai_quiz_difficulty_${level.name}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = level.titleAr,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) SudXGoldTint else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = level.badgeAr,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) SudXChampagneGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "عدد الأسئلة:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(3, 5, 8).forEach { count ->
                                val isSelected = questionCount == count
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { if (!isGenerating) onSelectCount(count) },
                                    label = {
                                        Text(
                                            text = "$count أسئلة",
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SudXGoldTint,
                                        selectedLabelColor = SudXMidnightNavy
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                                        selectedBorderColor = SudXChampagneGold
                                    ),
                                    modifier = Modifier.testTag("ai_quiz_count_$count")
                                )
                            }
                        }
                    }
                }
            }
        }

        // Error Banner if Generation Failed
        if (generatorState is QuizGeneratorUiState.Error) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, SudXCrimson.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_quiz_error_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = SudXCrimson,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = generatorState.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = SudXMidnightNavy,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }

        // Generate CTA
        item {
            PremiumActionButton(
                onClick = { if (!isGenerating) onGenerateQuiz() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_ai_quiz_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = SudXChampagneGold,
                        strokeWidth = 2.2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "جاري تأليف الاختبار الذكي وتجهيز التلميحات...",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SudXChampagneGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "توليد الاختبار التوجيهي الآن",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // Pedagogy Info Pill
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SudXGoldTint.copy(alpha = 0.72f),
                border = BorderStroke(1.dp, SudXGoldBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = null,
                        tint = SudXBronzeGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "أسلوب التعلّم الموجَّه: عند اختيار إجابة خاطئة لن يظهر الحل فوراً؛ بل سيعطيك الأستاذ الذكي تلميحاً يصحح تفكيرك لتصل للإجابة الصحيحة بنفسك.",
                        style = MaterialTheme.typography.labelSmall,
                        color = SudXMidnightNavy,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
