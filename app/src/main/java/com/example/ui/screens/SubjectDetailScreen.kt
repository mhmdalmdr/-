package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumData
import com.example.model.Subject
import com.example.model.Textbook
import com.example.ui.components.PremiumActionButton
import com.example.ui.components.PremiumInteractiveCard
import com.example.ui.components.PremiumSurfaceCard
import com.example.ui.components.SudXHeaderSurface
import com.example.ui.theme.SudXBronzeGold
import com.example.ui.theme.SudXChampagneGold
import com.example.ui.theme.SudXEmeraldDark
import com.example.ui.theme.SudXGoldBorder
import com.example.ui.theme.SudXGoldTint
import com.example.ui.theme.SudXMidnightNavy
import com.example.ui.theme.SudXNavySoft
import com.example.ui.theme.SudXRoyalNavy

@Composable
fun SubjectDetailScreen(
    subject: Subject,
    onBack: () -> Unit,
    onAskTeacher: (String) -> Unit,
    onStartSubjectQuiz: (List<com.example.model.QuizQuestion>) -> Unit,
    onSaveLessonNote: ((title: String, content: String, subjectTitle: String, isPinned: Boolean) -> Unit)? = null,
    onOpenPdfTextbook: ((Textbook) -> Unit)? = null,
    onStartPomodoroForSubject: ((String) -> Unit)? = null,
    initialSelectedUnitId: String? = null,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    var selectedUnit by remember(subject.id, initialSelectedUnitId) {
        mutableStateOf(
            subject.units.find { it.id == initialSelectedUnitId } ?: subject.units.firstOrNull()
        )
    }
    var showSaveNoteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // SudX Luxury Top App Bar
        SudXHeaderSurface {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع للمقررات",
                        tint = SudXGoldTint
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = SudXGoldTint,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "مقرر الشهادة الثانوية • SudX",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE2E8F0)
                    )
                }
                val matchingBook = remember(subject.id) {
                    CurriculumData.textbooks.firstOrNull { it.subjectId == subject.id }
                }
                if (matchingBook != null && onOpenPdfTextbook != null) {
                    IconButton(
                        onClick = { onOpenPdfTextbook(matchingBook) },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("subject_detail_pdf_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "فتح كتاب المادة PDF",
                            tint = SudXChampagneGold
                        )
                    }
                }
                if (onStartPomodoroForSubject != null) {
                    IconButton(
                        onClick = { onStartPomodoroForSubject(subject.title) },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("subject_detail_pomodoro_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "بدء مؤقت بومودورو للمادة",
                            tint = SudXChampagneGold
                        )
                    }
                }
                if (onSaveLessonNote != null && selectedUnit != null) {
                    IconButton(
                        onClick = { showSaveNoteDialog = true },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("save_unit_note_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = "حفظ الدرس كملاحظة",
                            tint = SudXChampagneGold
                        )
                    }
                }
            }
        }

        // Units Selector Tabs if multiple units exist
        if (subject.units.size > 1) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        subject.units.forEach { unit ->
                            val isSelected = unit.id == selectedUnit?.id
                            PremiumInteractiveCard(
                                onClick = { selectedUnit = unit },
                                cornerRadius = 14.dp,
                                containerColor = if (isSelected) SudXRoyalNavy else MaterialTheme.colorScheme.surface,
                                borderColor = if (isSelected) SudXChampagneGold else MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("unit_tab_${unit.id}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = unit.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) SudXGoldTint else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Main Unit Content
        val currentUnit = selectedUnit
        if (currentUnit != null) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Unit Title Card
                item {
                    PremiumSurfaceCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = SudXBronzeGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentUnit.subtitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SudXBronzeGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentUnit.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Key Points / Summary Pills
                item {
                    PremiumSurfaceCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SudXEmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "النقاط المحورية للامتحان",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            currentUnit.keyPoints.forEachIndexed { index, point ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(SudXNavySoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SudXRoyalNavy,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = point,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f),
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Detailed Explanation
                item {
                    PremiumSurfaceCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "شرح الدرس التفصيلي",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = currentUnit.fullContent,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 26.sp
                            )
                        }
                    }
                }

                // Exam Focus Box
                item {
                    PremiumSurfaceCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = SudXGoldTint,
                        borderColor = SudXGoldBorder,
                        cornerRadius = 20.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = SudXBronzeGold,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "توجيهات واضع الامتحان",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SudXMidnightNavy
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentUnit.examFocus,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF334155),
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                // Action Buttons: Ask Smart Teacher & Quiz
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PremiumActionButton(
                            onClick = { onAskTeacher("يا أستاذ، أريد شرحاً وتدريباً حول درس: ${currentUnit.title}") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("discuss_with_tutor_btn"),
                            containerColor = SudXRoyalNavy,
                            contentColor = SudXGoldTint,
                            cornerRadius = 14.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = SudXChampagneGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "اسأل المعلم الذكي",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (currentUnit.sampleQuestions.isNotEmpty()) {
                            PremiumActionButton(
                                onClick = { onStartSubjectQuiz(currentUnit.sampleQuestions) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("start_quiz_btn"),
                                containerColor = SudXChampagneGold,
                                contentColor = SudXMidnightNavy,
                                cornerRadius = 14.dp
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "اختبر فهمك",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val unitForNote = selectedUnit
    if (showSaveNoteDialog && onSaveLessonNote != null && unitForNote != null) {
        AddEditStudyNoteDialog(
            initialSubject = subject.title,
            initialTitle = unitForNote.title,
            initialContent = unitForNote.keyPoints.joinToString("\n• ", prefix = "• ") +
                "\n\n" + unitForNote.examFocus,
            onDismiss = { showSaveNoteDialog = false },
            onSave = { title, content, subjectTitle, isPinned ->
                onSaveLessonNote(title, content, subjectTitle, isPinned)
                showSaveNoteDialog = false
                Toast.makeText(context, "تم حفظ الملاحظة في قاعدة البيانات المحلية (Room)", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

