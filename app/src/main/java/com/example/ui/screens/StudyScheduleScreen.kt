package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.CurriculumData
import com.example.model.SchedulePeriod
import com.example.model.StudyDay
import com.example.model.StudySession
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
import com.example.viewmodel.TutorViewModel
import java.util.UUID

@Composable
fun StudyScheduleScreen(
    viewModel: TutorViewModel,
    onBack: (() -> Unit)? = null,
    onOpenPomodoro: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (onBack != null) {
        BackHandler { onBack() }
    }

    val context = LocalContext.current
    val sessions by viewModel.studySessions.collectAsState()

    var selectedViewMode by remember { mutableIntStateOf(0) } // 0 = Daily, 1 = Weekly
    var selectedDayFilter by remember { mutableStateOf(StudyDay.SATURDAY) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Runtime notification permission launcher for Android 13+ (API 33+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission granted
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val completedCount = sessions.count { it.isCompleted }
    val totalCount = sessions.size
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    val displayedSessions = if (selectedViewMode == 0) {
        // Daily: show sessions for the selected day or daily sessions
        sessions.filter { it.period == SchedulePeriod.DAILY || it.day == selectedDayFilter }
    } else {
        // Weekly: show all sessions grouped
        sessions
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // SudX Luxury Top Header
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
                        IconButton(onClick = onBack) {
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
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "جدول المذاكرة • المعلم الذكي",
                            style = MaterialTheme.typography.titleMedium,
                            color = SudXGoldTint,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "جداول يومية وأسبوعية مخصصة لكل مادة مع إشعارات ذكية",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                    if (onOpenPomodoro != null) {
                        IconButton(
                            onClick = onOpenPomodoro,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SudXChampagneGold.copy(alpha = 0.16f))
                                .border(1.dp, SudXChampagneGold.copy(alpha = 0.55f), CircleShape)
                                .testTag("schedule_open_pomodoro_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "فتح مؤقت بومودورو",
                                tint = SudXChampagneGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SudXChampagneGold)
                            .testTag("add_schedule_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "إضافة جلسة",
                            tint = SudXMidnightNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // View Mode Tabs: يومي vs أسبوعي
        TabRow(
            selectedTabIndex = selectedViewMode,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedViewMode]),
                    color = SudXChampagneGold,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedViewMode == 0,
                onClick = { selectedViewMode = 0 },
                text = {
                    Text(
                        text = "الجدول اليومي",
                        fontWeight = if (selectedViewMode == 0) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                modifier = Modifier.testTag("tab_daily_schedule")
            )
            Tab(
                selected = selectedViewMode == 1,
                onClick = { selectedViewMode = 1 },
                text = {
                    Text(
                        text = "الجدول الأسبوعي الكامل",
                        fontWeight = if (selectedViewMode == 1) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                modifier = Modifier.testTag("tab_weekly_schedule")
            )
        }

        // Days Filter Bar if Daily view is selected
        if (selectedViewMode == 0) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(StudyDay.entries) { day ->
                    val isSelected = selectedDayFilter == day
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDayFilter = day },
                        label = {
                            Text(
                                text = day.titleAr,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
                            borderColor = SudXGoldBorder.copy(alpha = 0.6f),
                            selectedBorderColor = SudXChampagneGold
                        ),
                        modifier = Modifier.testTag("day_chip_${day.name}")
                    )
                }
            }
        }

        // Main List Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stats & Progress Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SudXEmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "إنجاز جلسات المذاكرة",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "$completedCount من $totalCount جلسات منجزة",
                                style = MaterialTheme.typography.labelMedium,
                                color = SudXBronzeGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SudXChampagneGold,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "نصيحة الأستاذ: «المذاكرة في ساعات الفجر والصباح الباكر تعادل ضعف المذاكرة في أوقات التعب!»",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Actions: Add Session & Restore Default
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_new_session_quick_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SudXRoyalNavy,
                            contentColor = SudXGoldTint
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إضافة جلسة مذاكرة",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.restoreDefaultSchedule(context) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = SudXBronzeGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "الجدول النموذجي",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Empty State
            if (displayedSessions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد جلسات مذاكرة مضافة لهذا اليوم",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "اضغط على زر (إضافة جلسة مذاكرة) بالأعلى لإنشاء جدولك الخاص",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(displayedSessions, key = { it.id }) { session ->
                    StudySessionCardItem(
                        session = session,
                        onToggleCompleted = { viewModel.toggleSessionCompleted(session.id) },
                        onToggleReminder = { viewModel.toggleSessionReminder(session.id, context) },
                        onTestReminder = { viewModel.testReminderNotification(context, session) },
                        onDelete = { viewModel.deleteStudySession(session.id, context) }
                    )
                }
            }
        }
    }

    // Add Session Modal Dialog
    if (showAddDialog) {
        AddStudySessionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newSession ->
                viewModel.addStudySession(newSession, context)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun StudySessionCardItem(
    session: StudySession,
    onToggleCompleted: () -> Unit,
    onToggleReminder: () -> Unit,
    onTestReminder: () -> Unit,
    onDelete: () -> Unit
) {
    val isDone = session.isCompleted

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = SudXMidnightNavy.copy(alpha = 0.08f),
                spotColor = SudXMidnightNavy.copy(alpha = 0.14f)
            )
            .border(
                width = 1.dp,
                color = if (isDone) SudXEmeraldDark.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("study_session_card_${session.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) SudXEmeraldSoft else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Subject, Period Badge, Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Checkbox Circle
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isDone) SudXEmeraldDark else MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (isDone) SudXEmeraldDark else SudXGoldBorder,
                                shape = CircleShape
                            )
                            .clickable { onToggleCompleted() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "تم الإنجاز",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = session.subjectTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDone) SudXEmeraldDark else MaterialTheme.colorScheme.primary,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SudXGoldTint)
                            .border(1.dp, SudXGoldBorder.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (session.period == SchedulePeriod.DAILY) "يومي" else session.day.titleAr,
                            style = MaterialTheme.typography.labelSmall,
                            color = SudXBronzeGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Priority Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (session.priority) {
                                "عالية" -> Color(0xFFFEE2E2)
                                "متوسطة" -> SudXGoldTint
                                else -> SudXNavySoft
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = session.priority,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (session.priority) {
                            "عالية" -> SudXCrimson
                            "متوسطة" -> SudXBronzeGold
                            else -> SudXRoyalNavy
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Topic Description
            Text(
                text = session.topic,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDone) SudXMidnightNavy else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Timing Details & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = if (isDone) SudXEmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = session.formattedTime,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isDone) SudXMidnightNavy else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (isDone) SudXEmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${session.durationMinutes} دقيقة",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isDone) SudXMidnightNavy else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Actions: Test notification, Toggle reminder, Delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Test notification button
                    IconButton(
                        onClick = onTestReminder,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("test_reminder_${session.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "تجربة الإشعار",
                            tint = SudXBronzeGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Toggle Reminder Alarm Icon
                    IconButton(
                        onClick = onToggleReminder,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("toggle_reminder_${session.id}")
                    ) {
                        Icon(
                            imageVector = if (session.reminderEnabled) Icons.Default.Alarm else Icons.Default.AlarmOff,
                            contentDescription = "تفعيل/إلغاء التنبيه",
                            tint = if (session.reminderEnabled) SudXEmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete Session
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_session_${session.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف الجلسة",
                            tint = SudXCrimson,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddStudySessionDialog(
    onDismiss: () -> Unit,
    onConfirm: (StudySession) -> Unit
) {
    val availableSubjects = CurriculumData.subjects
    var selectedSubject by remember { mutableStateOf(availableSubjects.first()) }
    var topicText by remember { mutableStateOf("") }
    var selectedPeriod by remember { mutableStateOf(SchedulePeriod.DAILY) }
    var selectedDay by remember { mutableStateOf(StudyDay.SATURDAY) }
    var selectedHour by remember { mutableIntStateOf(5) }
    var selectedMinute by remember { mutableIntStateOf(30) }
    var selectedDuration by remember { mutableIntStateOf(60) }
    var selectedPriority by remember { mutableStateOf("عالية") }
    var reminderEnabled by remember { mutableStateOf(true) }

    val hoursList = listOf(5, 6, 7, 8, 14, 16, 17, 18, 19, 20, 21)
    val durationsList = listOf(30, 45, 60, 90, 120)
    val prioritiesList = listOf("عالية", "متوسطة", "مراجعة")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "إضافة جلسة مذاكرة جديدة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Select Subject
                item {
                    Text(
                        text = "اختر المادة الدراسية:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(availableSubjects) { subject ->
                            val isSelected = selectedSubject.id == subject.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSubject = subject },
                                label = { Text(subject.title, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SudXRoyalNavy,
                                    selectedLabelColor = SudXGoldTint
                                )
                            )
                        }
                    }
                }

                // 2. Topic Input
                item {
                    Text(
                        text = "موضوع المذاكرة والهدف:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = topicText,
                        onValueChange = { topicText = it },
                        placeholder = { Text("مثال: حل مسائل التفاضل، مراجعة الثورة المهدية...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_topic_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SudXChampagneGold
                        )
                    )
                }

                // 3. Frequency (يومي vs أسبوعي)
                item {
                    Text(
                        text = "التكرار الزمني:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedPeriod == SchedulePeriod.DAILY,
                            onClick = { selectedPeriod = SchedulePeriod.DAILY },
                            label = { Text("يومي (كل يوم)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SudXRoyalNavy,
                                selectedLabelColor = SudXGoldTint
                            )
                        )
                        FilterChip(
                            selected = selectedPeriod == SchedulePeriod.WEEKLY,
                            onClick = { selectedPeriod = SchedulePeriod.WEEKLY },
                            label = { Text("أسبوعي (يوم محدد)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SudXRoyalNavy,
                                selectedLabelColor = SudXGoldTint
                            )
                        )
                    }
                }

                // 4. Day of Week if Weekly
                if (selectedPeriod == SchedulePeriod.WEEKLY) {
                    item {
                        Text(
                            text = "اليوم المحدد:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(StudyDay.entries) { day ->
                                val isSelected = selectedDay == day
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedDay = day },
                                    label = { Text(day.titleAr, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SudXRoyalNavy,
                                        selectedLabelColor = SudXGoldTint
                                    )
                                )
                            }
                        }
                    }
                }

                // 5. Hour of Day
                item {
                    Text(
                        text = "وقت المذاكرة المفضل:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(hoursList) { h ->
                            val label = when (h) {
                                5 -> "05:00 ص (فجر)"
                                6 -> "06:00 ص (صباح)"
                                7 -> "07:00 ص"
                                8 -> "08:00 ص"
                                14 -> "02:00 م (ظهر)"
                                16 -> "04:00 م (عصر)"
                                17 -> "05:00 م"
                                18 -> "06:00 م (مغرب)"
                                19 -> "07:00 م"
                                20 -> "08:00 م (عشاء)"
                                21 -> "09:00 م"
                                else -> "$h:00"
                            }
                            val isSelected = selectedHour == h
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedHour = h
                                    selectedMinute = 0
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SudXRoyalNavy,
                                    selectedLabelColor = SudXGoldTint
                                )
                            )
                        }
                    }
                }

                // 6. Duration
                item {
                    Text(
                        text = "المدة الزمنية للجلسة:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        durationsList.forEach { dur ->
                            val isSelected = selectedDuration == dur
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDuration = dur },
                                label = { Text("$dur دقيقة", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SudXRoyalNavy,
                                    selectedLabelColor = SudXGoldTint
                                )
                            )
                        }
                    }
                }

                // 7. Priority Selector
                item {
                    Text(
                        text = "أولوية الجلسة:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        prioritiesList.forEach { priority ->
                            val isSelected = selectedPriority == priority
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedPriority = priority },
                                label = { Text(priority, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SudXRoyalNavy,
                                    selectedLabelColor = SudXGoldTint
                                )
                            )
                        }
                    }
                }

                // 8. Reminder Notification Toggle
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SudXNavySoft)
                            .border(1.dp, SudXGoldBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تفعيل إشعار التذكير",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SudXMidnightNavy
                            )
                            Text(
                                text = "إرسال تنبيه قبل الموعد لتحفيزك على بدء الجلسة",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF475569)
                            )
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SudXChampagneGold,
                                checkedTrackColor = SudXRoyalNavy
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTopic = topicText.ifBlank { "مراجعة شاملة وحل تدريبات" }
                    val newSession = StudySession(
                        id = UUID.randomUUID().toString(),
                        subjectId = selectedSubject.id,
                        subjectTitle = selectedSubject.title,
                        topic = finalTopic,
                        period = selectedPeriod,
                        day = selectedDay,
                        hour = selectedHour,
                        minute = selectedMinute,
                        durationMinutes = selectedDuration,
                        reminderEnabled = reminderEnabled,
                        isCompleted = false,
                        priority = selectedPriority
                    )
                    onConfirm(newSession)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SudXRoyalNavy,
                    contentColor = SudXGoldTint
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_study_session_button")
            ) {
                Text("حفظ الجلسة في الجدول", fontWeight = FontWeight.Bold)
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
