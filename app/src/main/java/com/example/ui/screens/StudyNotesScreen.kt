package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CurriculumData
import com.example.model.StudyNote
import com.example.ui.components.PremiumActionButton
import com.example.ui.components.PremiumSurfaceCard
import com.example.ui.components.SudXHeaderSurface
import com.example.ui.theme.SudXBronzeGold
import com.example.ui.theme.SudXChampagneGold
import com.example.ui.theme.SudXCrimson
import com.example.ui.theme.SudXGoldBorder
import com.example.ui.theme.SudXGoldTint
import com.example.ui.theme.SudXMidnightNavy
import com.example.ui.theme.SudXNavySoft
import com.example.ui.theme.SudXRoyalNavy
import com.example.viewmodel.TutorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudyNotesScreen(
    viewModel: TutorViewModel,
    onAskTeacherAboutNote: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (onBack != null) {
        BackHandler { onBack() }
    }

    val notes by viewModel.studyNotes.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubjectFilter by remember { mutableStateOf("الكل") }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var noteBeingEdited by remember { mutableStateOf<StudyNote?>(null) }

    val subjectFilters = remember {
        listOf("الكل") + CurriculumData.subjects.map { it.title } + listOf("عام")
    }

    val filteredNotes = remember(notes, searchQuery, selectedSubjectFilter) {
        notes.filter { note ->
            val matchesSubject = selectedSubjectFilter == "الكل" || note.subjectTitle == selectedSubjectFilter
            val matchesQuery = searchQuery.isBlank() ||
                note.title.contains(searchQuery, ignoreCase = true) ||
                note.content.contains(searchQuery, ignoreCase = true) ||
                note.subjectTitle.contains(searchQuery, ignoreCase = true)
            matchesSubject && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("study_notes_screen")
    ) {
        // SudX Luxury Header
        SudXHeaderSurface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("notes_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = SudXGoldTint
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
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
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ملاحظاتي التعليمية • المعلم الذكي",
                            style = MaterialTheme.typography.titleMedium,
                            color = SudXGoldTint,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = SudXChampagneGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "محفوظة محلياً في قاعدة البيانات (Room • ${notes.size})",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            noteBeingEdited = null
                            showAddEditDialog = true
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SudXChampagneGold)
                            .testTag("add_note_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "إضافة ملاحظة جديدة",
                            tint = SudXMidnightNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Search Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 10.dp, bottom = 6.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "ابحث في ملاحظاتك المحفوظة...",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = SudXBronzeGold
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
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("notes_search_input")
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subject Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subjectFilters) { subject ->
                    val isSelected = selectedSubjectFilter == subject
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubjectFilter = subject },
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
                            containerColor = SudXNavySoft,
                            labelColor = SudXMidnightNavy
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = SudXGoldBorder.copy(alpha = 0.5f),
                            selectedBorderColor = SudXChampagneGold
                        ),
                        modifier = Modifier.testTag("notes_filter_$subject")
                    )
                }
            }
        }

        // Notes List or Empty State
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("notes_list"),
            contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredNotes.isEmpty()) {
                item {
                    PremiumSurfaceCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        cornerRadius = 20.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(SudXGoldTint)
                                    .border(1.dp, SudXGoldBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = SudXBronzeGold,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (notes.isEmpty()) {
                                    "دفتر ملاحظاتك فارغ حالياً"
                                } else {
                                    "لا توجد ملاحظات مطابقة للبحث"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "دوّن القوانين المهمة، التلخيصات، أو احفظ شروحات المعلم الذكي هنا لتعود إليها في أي وقت دون إنترنت.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PremiumActionButton(
                                onClick = {
                                    noteBeingEdited = null
                                    showAddEditDialog = true
                                },
                                modifier = Modifier.testTag("empty_add_note_button"),
                                containerColor = SudXRoyalNavy,
                                contentColor = SudXGoldTint,
                                cornerRadius = 14.dp
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = SudXChampagneGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "إضافة أول ملاحظة",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredNotes, key = { it.id }) { note ->
                    StudyNoteCard(
                        note = note,
                        onTogglePin = { viewModel.togglePinStudyNote(note) },
                        onEdit = {
                            noteBeingEdited = note
                            showAddEditDialog = true
                        },
                        onAskTeacher = {
                            onAskTeacherAboutNote(
                                "يا أستاذ، أريد مراجعة وتوسيع هذه الملاحظة في مادة (${note.subjectTitle}):\n${note.title}\n${note.content}"
                            )
                        },
                        onDelete = { viewModel.deleteStudyNote(note.id) }
                    )
                }
            }
        }
    }

    if (showAddEditDialog) {
        AddEditStudyNoteDialog(
            existingNote = noteBeingEdited,
            onDismiss = {
                showAddEditDialog = false
                noteBeingEdited = null
            },
            onSave = { title, content, subjectTitle, isPinned ->
                viewModel.saveStudyNote(
                    title = title,
                    content = content,
                    subjectTitle = subjectTitle,
                    isPinned = isPinned,
                    existingNote = noteBeingEdited
                )
                showAddEditDialog = false
                noteBeingEdited = null
            }
        )
    }
}

@Composable
private fun StudyNoteCard(
    note: StudyNote,
    onTogglePin: () -> Unit,
    onEdit: () -> Unit,
    onAskTeacher: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy • hh:mm a", Locale.US) }
    val formattedDate = remember(note.updatedAt) { dateFormatter.format(Date(note.updatedAt)) }
    var isExpanded by remember(note.id) { mutableStateOf(false) }

    PremiumSurfaceCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("study_note_card_${note.id}"),
        containerColor = if (note.isPinned) SudXGoldTint.copy(alpha = 0.55f) else MaterialTheme.colorScheme.surface,
        borderColor = if (note.isPinned) SudXChampagneGold else MaterialTheme.colorScheme.outlineVariant,
        cornerRadius = 18.dp,
        elevation = if (note.isPinned) 5.dp else 3.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SudXNavySoft)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = note.subjectTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = SudXRoyalNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (note.isPinned) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SudXChampagneGold.copy(alpha = 0.25f))
                                .border(0.8.dp, SudXGoldBorder, RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "مثبتة",
                                style = MaterialTheme.typography.labelSmall,
                                color = SudXBronzeGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
                maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis
            )

            if (note.content.length > 140) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { isExpanded = !isExpanded },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isExpanded) "عرض أقل" else "قراءة المزيد",
                        style = MaterialTheme.typography.labelSmall,
                        color = SudXRoyalNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onAskTeacher,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("ask_teacher_note_${note.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SudXBronzeGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "اسأل الأستاذ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("pin_note_${note.id}")
                    ) {
                        Icon(
                            imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (note.isPinned) "إلغاء التثبيت" else "تثبيت الملاحظة",
                            tint = if (note.isPinned) SudXBronzeGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("edit_note_${note.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل الملاحظة",
                            tint = SudXRoyalNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("delete_note_${note.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "حذف الملاحظة",
                            tint = SudXCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditStudyNoteDialog(
    existingNote: StudyNote? = null,
    initialSubject: String? = null,
    initialTitle: String = "",
    initialContent: String = "",
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, subjectTitle: String, isPinned: Boolean) -> Unit
) {
    val subjectOptions = remember {
        CurriculumData.subjects.map { it.title } + listOf("عام")
    }

    var selectedSubject by remember(existingNote, initialSubject) {
        mutableStateOf(
            existingNote?.subjectTitle
                ?: initialSubject?.takeIf { it in subjectOptions }
                ?: subjectOptions.first()
        )
    }
    var titleText by remember(existingNote, initialTitle) {
        mutableStateOf(existingNote?.title ?: initialTitle)
    }
    var contentText by remember(existingNote, initialContent) {
        mutableStateOf(existingNote?.content ?: initialContent)
    }
    var isPinned by remember(existingNote) {
        mutableStateOf(existingNote?.isPinned ?: false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = if (existingNote != null) "تعديل الملاحظة التعليمية" else "حفظ ملاحظة تعليمية جديدة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "المادة الدراسية:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(subjectOptions) { subject ->
                        val isSelected = selectedSubject == subject
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSubject = subject },
                            label = { Text(subject, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SudXRoyalNavy,
                                selectedLabelColor = SudXGoldTint
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("عنوان الملاحظة") },
                    placeholder = { Text("مثال: قوانين الحركة الدائرية...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SudXChampagneGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_title_input")
                )

                OutlinedTextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    label = { Text("محتوى الملاحظة") },
                    placeholder = { Text("اكتب ملخصك أو القاعدة المهمة هنا...") },
                    minLines = 4,
                    maxLines = 7,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SudXChampagneGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_content_input")
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SudXNavySoft)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تثبيت الملاحظة في الأعلى",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SudXMidnightNavy
                    )
                    Switch(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SudXChampagneGold,
                            checkedTrackColor = SudXRoyalNavy
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(titleText, contentText, selectedSubject, isPinned) },
                enabled = titleText.isNotBlank() || contentText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SudXRoyalNavy,
                    contentColor = SudXGoldTint
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_note_button")
            ) {
                Text(
                    text = if (existingNote != null) "تحديث الملاحظة" else "حفظ في Room",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("إلغاء")
            }
        }
    )
}
