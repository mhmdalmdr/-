package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.model.BookTrack
import com.example.model.PastExam
import com.example.model.Textbook
import com.example.ui.components.PremiumActionButton
import com.example.ui.components.PremiumOutlinedActionButton
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
import com.example.util.PdfTextbookGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ExamsResourcesScreen(
    initialTab: Int = 0,
    onStartExamSimulation: (PastExam) -> Unit,
    onAskTeacherAboutExam: (String) -> Unit,
    onOpenPdfTextbook: (Textbook) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }
    val tabTitles = listOf("امتحانات سابقة", "الكتب المدرسية", "نصائح الامتحان")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // SudX Header
        SudXHeaderSurface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "بنك الامتحانات والموارد • SudX",
                    style = MaterialTheme.typography.titleMedium,
                    color = SudXGoldTint,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "الامتحانات السابقة والكتب الرسمية للشهادة الثانوية",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE2E8F0)
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = SudXChampagneGold,
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.ExtraBold else FontWeight.Medium,
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    modifier = Modifier.testTag("exams_resource_tab_$index")
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> PastExamsList(
                onStartExamSimulation = onStartExamSimulation,
                onAskTeacher = onAskTeacherAboutExam
            )
            1 -> TextbooksList(
                onAskTeacher = onAskTeacherAboutExam,
                onOpenPdfTextbook = onOpenPdfTextbook
            )
            2 -> ExamTipsScreen(
                onAskTeacherForAdvice = onAskTeacherAboutExam
            )
        }
    }
}

@Composable
fun PastExamsList(
    onStartExamSimulation: (PastExam) -> Unit,
    onAskTeacher: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            PremiumSurfaceCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = SudXGoldTint,
                borderColor = SudXGoldBorder,
                cornerRadius = 18.dp,
                elevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = null,
                        tint = SudXBronzeGold,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "أوراق امتحانات الشهادة الثانوية السودانية الرسمية مع التركيز على الأسئلة المتكررة وطريقة التصحيح.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SudXMidnightNavy,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        items(CurriculumData.pastExams) { exam ->
            PremiumSurfaceCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("past_exam_card_${exam.id}"),
                cornerRadius = 20.dp,
                elevation = 5.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "امتحان ${exam.subjectTitle} - ${exam.year}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SudXGoldTint)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = exam.session,
                                style = MaterialTheme.typography.labelSmall,
                                color = SudXBronzeGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${exam.durationMinutes} دقيقة",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SudXEmeraldDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${exam.totalMarks} درجة كاملة",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "أبرز موضوعات الامتحان: " + exam.keyTopics.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (exam.sampleQuestions.isNotEmpty()) {
                            PremiumActionButton(
                                onClick = { onStartExamSimulation(exam) },
                                modifier = Modifier.weight(1f),
                                containerColor = SudXRoyalNavy,
                                contentColor = SudXGoldTint,
                                cornerRadius = 14.dp
                            ) {
                                Text(
                                    text = "حل نموذج الامتحان",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        PremiumOutlinedActionButton(
                            onClick = {
                                onAskTeacher("يا أستاذ، ما هي أهم الأسئلة المتكررة في امتحان ${exam.subjectTitle} للشهادة السودانية وكيف أستعد لها؟")
                            },
                            modifier = Modifier.weight(1f),
                            cornerRadius = 14.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SudXBronzeGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "توجيه الأستاذ",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TextbooksList(
    onAskTeacher: (String) -> Unit,
    onOpenPdfTextbook: (Textbook) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTrack by remember { mutableStateOf<BookTrack?>(null) }
    var pendingSaveBook by remember { mutableStateOf<Textbook?>(null) }

    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { targetUri ->
        val book = pendingSaveBook
        pendingSaveBook = null
        if (targetUri != null && book != null) {
            coroutineScope.launch {
                val ok = withContext(Dispatchers.IO) {
                    val units = CurriculumData.subjects
                        .filter { it.id == book.subjectId || it.title == book.subjectTitle }
                        .flatMap { it.units }
                    val file = PdfTextbookGenerator.generateOrGetPdfFile(context, book, units)
                    PdfTextbookGenerator.copyPdfToUri(context, file, targetUri)
                }
                Toast.makeText(
                    context,
                    if (ok) "تم حفظ كتاب PDF في جهازك" else "تعذر حفظ ملف PDF",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val filteredBooks = remember(selectedTrack) {
        if (selectedTrack == null) CurriculumData.textbooks
        else CurriculumData.textbooks.filter { it.track == selectedTrack }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            PremiumSurfaceCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = SudXGoldTint,
                borderColor = SudXGoldBorder,
                cornerRadius = 18.dp,
                elevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = SudXRoyalNavy,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "جميع كتب القسم العلمي والأدبي والمشتركة بصيغة PDF للشهادة الثانوية السودانية.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SudXMidnightNavy,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedTrack == null,
                        onClick = { selectedTrack = null },
                        label = { Text("الكل (${CurriculumData.textbooks.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SudXRoyalNavy,
                            selectedLabelColor = SudXGoldTint
                        )
                    )
                }
                items(BookTrack.entries) { track ->
                    FilterChip(
                        selected = selectedTrack == track,
                        onClick = { selectedTrack = track },
                        label = { Text(track.titleAr) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SudXRoyalNavy,
                            selectedLabelColor = SudXGoldTint
                        )
                    )
                }
            }
        }

        items(filteredBooks, key = { it.id }) { book ->
            PremiumSurfaceCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("textbook_card_${book.id}"),
                cornerRadius = 20.dp,
                elevation = 5.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SudXNavySoft)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = book.track.badgeAr,
                                style = MaterialTheme.typography.labelSmall,
                                color = SudXRoyalNavy,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${book.subjectTitle} • ${book.grade} • ${book.pagesCount} صفحة",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = book.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "فهرس الأبواب: " + book.chapters.take(3).joinToString(" | ") + "...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PremiumActionButton(
                            onClick = { onOpenPdfTextbook(book) },
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize(),
                            containerColor = SudXRoyalNavy,
                            contentColor = SudXGoldTint,
                            cornerRadius = 14.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = SudXChampagneGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "قراءة PDF",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        PremiumOutlinedActionButton(
                            onClick = {
                                pendingSaveBook = book
                                saveLauncher.launch(book.pdfFileName.ifBlank { "${book.id}.pdf" })
                            },
                            modifier = Modifier.minimumInteractiveComponentSize(),
                            cornerRadius = 14.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = SudXBronzeGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "حفظ",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        PremiumOutlinedActionButton(
                            onClick = {
                                onAskTeacher("يا أستاذ، أريد تلخيصاً لأهم أبواب ${book.title} وكيف أذاكرها بسرعة للامتحان.")
                            },
                            modifier = Modifier.minimumInteractiveComponentSize(),
                            cornerRadius = 14.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "تلخيص الأستاذ",
                                tint = SudXBronzeGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

