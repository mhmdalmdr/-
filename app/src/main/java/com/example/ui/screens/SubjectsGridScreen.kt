package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumData
import com.example.model.BookTrack
import com.example.model.Subject
import com.example.model.Textbook
import com.example.ui.components.PremiumActionButton
import com.example.ui.components.PremiumInteractiveCard
import com.example.ui.components.PremiumOutlinedActionButton
import com.example.ui.components.PremiumSurfaceCard
import com.example.ui.components.SudXHeaderSurface
import com.example.ui.theme.SubjectArabicBg
import com.example.ui.theme.SubjectArabicFg
import com.example.ui.theme.SubjectChemBg
import com.example.ui.theme.SubjectChemFg
import com.example.ui.theme.SubjectEnglishBg
import com.example.ui.theme.SubjectEnglishFg
import com.example.ui.theme.SubjectHistoryBg
import com.example.ui.theme.SubjectHistoryFg
import com.example.ui.theme.SubjectMathBg
import com.example.ui.theme.SubjectMathFg
import com.example.ui.theme.SubjectPhysicsBg
import com.example.ui.theme.SubjectPhysicsFg
import com.example.ui.theme.SudXBronzeGold
import com.example.ui.theme.SudXChampagneGold
import com.example.ui.theme.SudXCrimson
import com.example.ui.theme.SudXCrimsonSoft
import com.example.ui.theme.SudXEmerald
import com.example.ui.theme.SudXEmeraldDark
import com.example.ui.theme.SudXEmeraldSoft
import com.example.ui.theme.SudXGoldBorder
import com.example.ui.theme.SudXGoldTint
import com.example.ui.theme.SudXMidnightNavy
import com.example.ui.theme.SudXNavySoft
import com.example.ui.theme.SudXRoyalNavy
import com.example.util.PdfTextbookGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class SubjectsSectionMode(val titleAr: String) {
    ALL("الكل"),
    PDF_BOOKS("كتب PDF"),
    SUBJECTS("المواد والدروس")
}

private enum class TrackFilterOption(val titleAr: String, val track: BookTrack?) {
    ALL("جميع المسارات", null),
    SCIENCE("القسم العلمي", BookTrack.SCIENCE),
    ARTS("القسم الأدبي", BookTrack.ARTS),
    COMMON("المواد المشتركة", BookTrack.COMMON)
}

@Composable
fun SubjectsGridScreen(
    onSelectSubject: (Subject) -> Unit,
    onOpenPdfTextbook: (Textbook) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedMode by remember { mutableStateOf(SubjectsSectionMode.ALL) }
    var selectedTrackFilter by remember { mutableStateOf(TrackFilterOption.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var pendingBookForSave by remember { mutableStateOf<Textbook?>(null) }

    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { targetUri ->
        val book = pendingBookForSave
        pendingBookForSave = null
        if (targetUri != null && book != null) {
            coroutineScope.launch {
                val saved = withContext(Dispatchers.IO) {
                    val units = CurriculumData.subjects
                        .filter { it.id == book.subjectId || it.title == book.subjectTitle }
                        .flatMap { it.units }
                    val file = PdfTextbookGenerator.generateOrGetPdfFile(context, book, units)
                    PdfTextbookGenerator.copyPdfToUri(context, file, targetUri)
                }
                Toast.makeText(
                    context,
                    if (saved) "تم حفظ كتاب ${book.title} بصيغة PDF" else "تعذر حفظ ملف PDF",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val filteredSubjects = remember(selectedTrackFilter, searchQuery) {
        CurriculumData.subjects.filter { subject ->
            val matchesTrack = selectedTrackFilter.track == null || subject.track == selectedTrackFilter.track
            val matchesSearch = searchQuery.isBlank() ||
                subject.title.contains(searchQuery, ignoreCase = true) ||
                subject.description.contains(searchQuery, ignoreCase = true)
            matchesTrack && matchesSearch
        }
    }

    val filteredBooks = remember(selectedTrackFilter, searchQuery) {
        CurriculumData.textbooks.filter { book ->
            val matchesTrack = selectedTrackFilter.track == null || book.track == selectedTrackFilter.track
            val matchesSearch = searchQuery.isBlank() ||
                book.title.contains(searchQuery, ignoreCase = true) ||
                book.subjectTitle.contains(searchQuery, ignoreCase = true) ||
                book.description.contains(searchQuery, ignoreCase = true) ||
                book.chapters.any { it.contains(searchQuery, ignoreCase = true) }
            matchesTrack && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("subjects_grid_screen")
    ) {
        SudXHeaderSurface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "المواد وكتب PDF • المعلم الذكي",
                            style = MaterialTheme.typography.titleMedium,
                            color = SudXGoldTint,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "جميع مقررات وكتب القسم العلمي والأدبي بصيغة PDF",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SudXChampagneGold.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, SudXChampagneGold.copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = SudXChampagneGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${CurriculumData.textbooks.size} كتاب PDF",
                                style = MaterialTheme.typography.labelSmall,
                                color = SudXGoldTint,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        // Search & Filter Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "ابحث عن كتاب PDF أو مادة (أحياء، جغرافيا، فيزياء...)",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "مسح البحث"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SudXChampagneGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subjects_search_input")
            )

            // View Mode Tabs (الكل / كتب PDF / المواد والدروس)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SubjectsSectionMode.entries.forEach { mode ->
                    val isSelected = selectedMode == mode
                    val tag = when (mode) {
                        SubjectsSectionMode.ALL -> "subjects_mode_all"
                        SubjectsSectionMode.PDF_BOOKS -> "subjects_mode_pdf"
                        SubjectsSectionMode.SUBJECTS -> "subjects_mode_subjects"
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedMode = mode }
                            .minimumInteractiveComponentSize()
                            .testTag(tag),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SudXRoyalNavy else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SudXChampagneGold else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.titleAr,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) SudXGoldTint else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Track Filter Chips (جميع المسارات / القسم العلمي / القسم الأدبي / المواد المشتركة)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(TrackFilterOption.entries) { option ->
                    val selected = selectedTrackFilter == option
                    FilterChip(
                        selected = selected,
                        onClick = { selectedTrackFilter = option },
                        label = {
                            Text(
                                text = option.titleAr,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SudXGoldTint,
                            selectedLabelColor = SudXMidnightNavy
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            selectedBorderColor = SudXChampagneGold,
                            borderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.testTag("track_filter_${option.name.lowercase()}")
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (selectedMode == SubjectsSectionMode.ALL || selectedMode == SubjectsSectionMode.PDF_BOOKS) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مكتبة الكتب المدرسية PDF (${filteredBooks.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "جاهزة للقراءة والحفظ",
                            style = MaterialTheme.typography.labelSmall,
                            color = SudXBronzeGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(filteredBooks, key = { "pdf_${it.id}" }) { book ->
                    PdfTextbookCard(
                        book = book,
                        onReadPdf = { onOpenPdfTextbook(book) },
                        onSavePdf = {
                            pendingBookForSave = book
                            savePdfLauncher.launch(book.pdfFileName.ifBlank { "${book.id}.pdf" })
                        },
                        onSharePdf = {
                            coroutineScope.launch {
                                val file = withContext(Dispatchers.IO) {
                                    val units = CurriculumData.subjects
                                        .filter { it.id == book.subjectId || it.title == book.subjectTitle }
                                        .flatMap { it.units }
                                    PdfTextbookGenerator.generateOrGetPdfFile(context, book, units)
                                }
                                if (!PdfTextbookGenerator.sharePdf(context, file, book.title)) {
                                    Toast.makeText(context, "تعذر مشاركة ملف PDF", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }

            if (selectedMode == SubjectsSectionMode.ALL || selectedMode == SubjectsSectionMode.SUBJECTS) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "المواد والدروس التفاعلية (${filteredSubjects.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(filteredSubjects, key = { "subj_${it.id}" }) { subject ->
                    val matchingBook = remember(subject.id) {
                        CurriculumData.textbooks.firstOrNull { it.subjectId == subject.id }
                    }
                    SubjectFullCardItem(
                        subject = subject,
                        matchingBook = matchingBook,
                        onClick = { onSelectSubject(subject) },
                        onOpenBookPdf = {
                            if (matchingBook != null) {
                                onOpenPdfTextbook(matchingBook)
                            }
                        }
                    )
                }
            }

            if (filteredBooks.isEmpty() && filteredSubjects.isEmpty()) {
                item {
                    PremiumSurfaceCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 18.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(38.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "لا توجد كتب أو مواد مطابقة لبحثك",
                                style = MaterialTheme.typography.titleSmall,
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
fun PdfTextbookCard(
    book: Textbook,
    onReadPdf: () -> Unit,
    onSavePdf: () -> Unit,
    onSharePdf: () -> Unit
) {
    val (trackBg, trackFg) = when (book.track) {
        BookTrack.SCIENCE -> SudXNavySoft to SudXRoyalNavy
        BookTrack.ARTS -> SudXGoldTint to SudXBronzeGold
        BookTrack.COMMON -> SudXEmeraldSoft to SudXEmeraldDark
    }

    PremiumSurfaceCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pdf_textbook_card_${book.id}"),
        cornerRadius = 20.dp,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SudXCrimsonSoft)
                        .border(1.dp, SudXCrimson.copy(alpha = 0.28f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "ملف PDF",
                            tint = SudXCrimson,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "PDF",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SudXCrimson
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(trackBg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = book.track.titleAr,
                                style = MaterialTheme.typography.labelSmall,
                                color = trackFg,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${book.pagesCount} صفحة • ${book.chaptersCount} وحدات",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onSharePdf,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("share_pdf_btn_${book.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "مشاركة ${book.title}",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = book.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "الملف: ${book.pdfFileName.ifBlank { "${book.id}.pdf" }}",
                style = MaterialTheme.typography.labelSmall,
                color = SudXBronzeGold,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremiumActionButton(
                    onClick = onReadPdf,
                    containerColor = SudXRoyalNavy,
                    contentColor = SudXGoldTint,
                    cornerRadius = 12.dp,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("open_pdf_btn_${book.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = SudXChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "فتح وقراءة PDF",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                PremiumOutlinedActionButton(
                    onClick = onSavePdf,
                    borderColor = SudXGoldBorder,
                    cornerRadius = 12.dp,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("save_pdf_btn_${book.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = SudXBronzeGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "حفظ PDF",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectFullCardItem(
    subject: Subject,
    matchingBook: Textbook? = null,
    onClick: () -> Unit,
    onOpenBookPdf: () -> Unit = {}
) {
    val icon = when (subject.iconName) {
        "calculate" -> Icons.Default.Calculate
        "speed" -> Icons.Default.Speed
        "science" -> Icons.Default.Science
        "menu_book" -> Icons.Default.MenuBook
        "language" -> Icons.Default.Language
        "history" -> Icons.Default.History
        else -> Icons.Default.AutoAwesome
    }

    val iconBgColor = when (subject.id) {
        "math", "math_basic", "computer_science" -> SubjectMathBg
        "physics", "geography" -> SubjectPhysicsBg
        "chemistry", "commerce" -> SubjectChemBg
        "arabic", "biology", "islamic" -> SubjectArabicBg
        "english" -> SubjectEnglishBg
        else -> SubjectHistoryBg
    }

    val iconColor = when (subject.id) {
        "math", "math_basic", "computer_science" -> SubjectMathFg
        "physics", "geography" -> SubjectPhysicsFg
        "chemistry", "commerce" -> SubjectChemFg
        "arabic", "biology", "islamic" -> SubjectArabicFg
        "english" -> SubjectEnglishFg
        else -> SubjectHistoryFg
    }

    PremiumInteractiveCard(
        onClick = onClick,
        cornerRadius = 20.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subject_full_card_${subject.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconBgColor)
                    .border(1.dp, iconColor.copy(alpha = 0.22f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = subject.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SudXNavySoft)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = subject.track.badgeAr,
                            style = MaterialTheme.typography.labelSmall,
                            color = SudXRoyalNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subject.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    lineHeight = 19.sp
                )

                if (matchingBook != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SudXCrimsonSoft,
                        border = BorderStroke(0.8.dp, SudXCrimson.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onOpenBookPdf)
                            .minimumInteractiveComponentSize()
                            .testTag("subject_pdf_pill_${subject.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = SudXCrimson,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "فتح الكتاب PDF",
                                style = MaterialTheme.typography.labelSmall,
                                color = SudXCrimson,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SudXNavySoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "فتح المادة",
                    tint = SudXRoyalNavy,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
