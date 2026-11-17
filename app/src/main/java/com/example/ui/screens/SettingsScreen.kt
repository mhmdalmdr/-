package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.AcademicStream
import com.example.data.local.AppThemeMode
import com.example.model.TutorRole
import com.example.ui.components.PremiumActionButton
import com.example.ui.components.PremiumOutlinedActionButton
import com.example.ui.components.PremiumSurfaceCard
import com.example.ui.components.SudXHeaderSurface
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
import com.example.ui.theme.SudXSoftGold
import com.example.ui.theme.sudxCrimsonFg
import com.example.ui.theme.sudxCrimsonSoftBg
import com.example.ui.theme.sudxEmeraldFg
import com.example.ui.theme.sudxEmeraldSoftBg
import com.example.ui.theme.sudxGoldFg
import com.example.ui.theme.sudxGoldTintBg
import com.example.ui.theme.sudxIsDarkTheme
import com.example.ui.theme.sudxNavyFg
import com.example.ui.theme.sudxNavySoftBg
import com.example.util.StudentAccount
import com.example.viewmodel.TutorViewModel

@Composable
fun SettingsScreen(
    viewModel: TutorViewModel,
    onNavigateToSchedule: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val currentStudent by viewModel.currentStudent.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val isSigningIn by viewModel.isSigningIn.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val authSuccessMessage by viewModel.authSuccessMessage.collectAsStateWithLifecycle()
    val notesCount = viewModel.studyNotes.collectAsStateWithLifecycle().value.size

    var showClearChatDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("settings_screen")
    ) {
        // SudX Luxury Header
        SudXHeaderSurface {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = SudXGoldTint
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SudXChampagneGold.copy(alpha = 0.16f))
                        .border(1.dp, SudXChampagneGold.copy(alpha = 0.65f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = SudXChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "الإعدادات وحساب الطالب • المعلم الذكي",
                        style = MaterialTheme.typography.titleMedium,
                        color = SudXGoldTint,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "حساب Google وتخصيص مسار الشهادة السودانية",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 720.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 92.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Student Google Account Card
                item {
                    StudentGoogleAccountCard(
                        student = currentStudent,
                        academicStream = settings.academicStream,
                        isSigningIn = isSigningIn,
                        authError = authError,
                        authSuccessMessage = authSuccessMessage,
                        onSignInClick = { viewModel.signInWithGoogle(context) },
                        onSignOutClick = { viewModel.signOutFromGoogle(context) },
                        onDismissFeedback = { viewModel.clearAuthFeedback() }
                    )
                }

                // 2. Academic Stream Selector
                item {
                    SettingsSectionTitle(
                        title = "المسار الدراسي في الشهادة السودانية",
                        subtitle = "اختر مسارك لتخصيص تجربتك الدراسية"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AcademicStream.entries.forEach { stream ->
                            val isSelected = settings.academicStream == stream
                            val tag = if (stream == AcademicStream.SCIENCE) "stream_science_card" else "stream_arts_card"
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { viewModel.updateAcademicStream(stream) }
                                    .minimumInteractiveComponentSize()
                                    .testTag(tag),
                                shape = RoundedCornerShape(18.dp),
                                color = if (isSelected) SudXRoyalNavy else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) SudXChampagneGold else MaterialTheme.colorScheme.outlineVariant
                                ),
                                tonalElevation = if (isSelected) 4.dp else 1.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.School,
                                            contentDescription = null,
                                            tint = if (isSelected) SudXChampagneGold else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "مختار",
                                                tint = SudXEmerald,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = stream.titleAr,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SudXGoldTint else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stream.subtitleAr,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Color(0xFFCBD5E1) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Appearance & Night Mode
                item {
                    val isDarkActive = sudxIsDarkTheme()
                    SettingsSectionTitle(
                        title = "الوضع الليلي ومظهر التطبيق",
                        subtitle = "بدّل إلى الوضع الليلي لتسهيل المذاكرة في الأوقات المتأخرة وتقليل إجهاد العين"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PremiumSurfaceCard(cornerRadius = 20.dp) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            SettingsSwitchRow(
                                icon = Icons.Default.DarkMode,
                                iconTint = sudxGoldFg(),
                                iconBg = sudxGoldTintBg(),
                                title = "تفعيل الوضع الليلي للمذاكرة",
                                subtitle = if (isDarkActive) {
                                    "الوضع الليلي مفعّل الآن • ألوان داكنة مريحة للعين"
                                } else {
                                    "واجهة داكنة تقلل سطوع الشاشة وإجهاد العين ليلاً"
                                },
                                checked = isDarkActive,
                                onCheckedChange = { enabled ->
                                    viewModel.updateThemeMode(
                                        if (enabled) AppThemeMode.DARK else AppThemeMode.LIGHT
                                    )
                                },
                                testTag = "settings_night_mode_switch"
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AppThemeMode.entries.forEach { mode ->
                                    val isSelected = settings.themeMode == mode
                                    val icon = when (mode) {
                                        AppThemeMode.SYSTEM -> Icons.Default.PhoneAndroid
                                        AppThemeMode.LIGHT -> Icons.Default.LightMode
                                        AppThemeMode.DARK -> Icons.Default.DarkMode
                                    }
                                    val tag = when (mode) {
                                        AppThemeMode.SYSTEM -> "theme_mode_system"
                                        AppThemeMode.LIGHT -> "theme_mode_light"
                                        AppThemeMode.DARK -> "theme_mode_dark"
                                    }
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable { viewModel.updateThemeMode(mode) }
                                            .minimumInteractiveComponentSize()
                                            .testTag(tag),
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) SudXRoyalNavy else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = if (isSelected) SudXChampagneGold else Color.Transparent
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = mode.titleAr,
                                                tint = if (isSelected) SudXChampagneGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = mode.titleAr,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) SudXGoldTint else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Smart Teacher Preferences
                item {
                    SettingsSectionTitle(
                        title = "تفضيلات المعلم الذكي",
                        subtitle = "تخصيص سلوك المساعد الذكي ومصادر الإجابات"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PremiumSurfaceCard(cornerRadius = 20.dp) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            SettingsSwitchRow(
                                icon = Icons.Default.Public,
                                iconTint = sudxEmeraldFg(),
                                iconBg = sudxEmeraldSoftBg(),
                                title = "تفعيل بحث Google للمصادر",
                                subtitle = "استخدام مصادر البحث الموثوقة لدعم شروحات المنهج",
                                checked = settings.searchGroundingDefault,
                                onCheckedChange = { viewModel.updateSearchGroundingDefault(it) },
                                testTag = "setting_search_grounding_switch"
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SmartToy,
                                        contentDescription = null,
                                        tint = sudxNavyFg(),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "الشخصية الافتراضية للمعلم الذكي",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TutorRole.entries.forEach { role ->
                                        val isSelected = settings.defaultTutorRole == role
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(14.dp))
                                                .clickable { viewModel.updateDefaultTutorRole(role) }
                                                .minimumInteractiveComponentSize()
                                                .testTag("default_role_${role.name.lowercase()}"),
                                            shape = RoundedCornerShape(14.dp),
                                            color = if (isSelected) sudxGoldTintBg() else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = if (isSelected) SudXChampagneGold else MaterialTheme.colorScheme.outlineVariant
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = role.titleAr,
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = role.descriptionAr,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "مختار",
                                                        tint = sudxGoldFg(),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Study Tools & Local Data
                item {
                    SettingsSectionTitle(
                        title = "المذاكرة والبيانات المحلية",
                        subtitle = "إدارة التذكيرات والملاحظات وسجل المحادثات"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PremiumSurfaceCard(cornerRadius = 20.dp) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            SettingsSwitchRow(
                                icon = Icons.Default.NotificationsActive,
                                iconTint = sudxGoldFg(),
                                iconBg = sudxGoldTintBg(),
                                title = "تذكيرات جدول المذاكرة",
                                subtitle = "تفعيل أو إيقاف إشعارات جلسات المذاكرة المجدولة",
                                checked = settings.studyRemindersEnabled,
                                onCheckedChange = { viewModel.updateStudyRemindersEnabled(it, context) },
                                testTag = "setting_reminders_switch"
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            SettingsActionRow(
                                icon = Icons.Default.CalendarMonth,
                                iconTint = sudxEmeraldFg(),
                                iconBg = sudxEmeraldSoftBg(),
                                title = "جدول المذاكرة الأسبوعي",
                                subtitle = "عرض وتعديل حصص المذاكرة اليومية",
                                onClick = onNavigateToSchedule,
                                testTag = "settings_open_schedule_row"
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            SettingsActionRow(
                                icon = Icons.Default.EditNote,
                                iconTint = sudxNavyFg(),
                                iconBg = sudxNavySoftBg(),
                                title = "ملاحظاتي التعليمية ($notesCount)",
                                subtitle = "مراجعة الملاحظات المحفوظة في قاعدة بيانات Room",
                                onClick = onNavigateToNotes,
                                testTag = "settings_open_notes_row"
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            SettingsActionRow(
                                icon = Icons.Default.DeleteSweep,
                                iconTint = sudxCrimsonFg(),
                                iconBg = sudxCrimsonSoftBg(),
                                title = "مسح سجل محادثات المعلم الذكي",
                                subtitle = "إزالة الرسائل السابقة والبدء بصفحة جديدة",
                                onClick = { showClearChatDialog = true },
                                testTag = "settings_clear_chat_row"
                            )
                        }
                    }
                }

                // 6. About SudX Card
                item {
                    PremiumSurfaceCard(
                        cornerRadius = 18.dp,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SudXMidnightNavy)
                                    .border(1.dp, SudXChampagneGold.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SudXChampagneGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "منصة المعلم الذكي للشهادة السودانية",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "الإصدار 1.0 • مصمم لطلاب وطالبات الشهادة الثانوية",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearChatDialog) {
        AlertDialog(
            onDismissRequest = { showClearChatDialog = false },
            title = {
                Text(
                    text = "مسح سجل المحادثات",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("هل تريد حذف سجل المحادثة مع المعلم الذكي والبدء بمحادثة جديدة؟ ملاحظاتك التعليمية المحفوظة لن تتأثر.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearConversation()
                        showClearChatDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SudXCrimson),
                    modifier = Modifier.testTag("confirm_clear_chat_button")
                ) {
                    Text("مسح السجل", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearChatDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun StudentGoogleAccountCard(
    student: StudentAccount?,
    academicStream: AcademicStream,
    isSigningIn: Boolean,
    authError: String?,
    authSuccessMessage: String?,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onDismissFeedback: () -> Unit
) {
    val cardShape = RoundedCornerShape(24.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("google_account_card"),
        shape = cardShape,
        color = SudXMidnightNavy,
        border = BorderStroke(
            1.2.dp,
            Brush.linearGradient(
                colors = listOf(
                    SudXChampagneGold.copy(alpha = 0.85f),
                    SudXSoftGold.copy(alpha = 0.35f),
                    SudXChampagneGold.copy(alpha = 0.70f)
                )
            )
        ),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(SudXMidnightNavy, SudXRoyalNavy)
                    )
                )
                .padding(18.dp)
        ) {
            if (student != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Student Avatar (Google Photo with crisp initials fallback)
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(SudXGoldTint)
                            .border(1.5.dp, SudXChampagneGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val initial = student.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "S"
                        Text(
                            text = initial,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = SudXMidnightNavy
                        )
                        if (!student.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = student.photoUrl,
                                contentDescription = "صورة الطالب ${student.displayName}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = student.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SudXGoldTint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (student.email.isNotBlank()) {
                            Text(
                                text = student.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SudXEmerald.copy(alpha = 0.22f))
                                    .border(1.dp, SudXEmerald.copy(alpha = 0.6f), RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = Color(0xFF4ADE80),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "متصل بحساب Google",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFD1FAE5),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SudXChampagneGold.copy(alpha = 0.16f))
                                    .border(1.dp, SudXChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = academicStream.titleAr,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SudXSoftGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onSignOutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("google_sign_out_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SudXChampagneGold.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.06f),
                        contentColor = SudXGoldTint
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = SudXChampagneGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تسجيل الخروج من حساب Google",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = SudXGoldTint
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(SudXChampagneGold.copy(alpha = 0.16f))
                            .border(1.2.dp, SudXChampagneGold.copy(alpha = 0.7f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "حساب الطالب",
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "حساب الطالب في المعلم الذكي",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SudXGoldTint
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "سجّل الدخول بحساب Google لتوثيق ملفك الشخصي كطالب شهادة سودانية.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 19.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSignInClick,
                    enabled = !isSigningIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("google_sign_in_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SudXGoldTint,
                        contentColor = SudXMidnightNavy,
                        disabledContainerColor = SudXGoldTint.copy(alpha = 0.65f),
                        disabledContentColor = SudXMidnightNavy
                    )
                ) {
                    if (isSigningIn) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = SudXMidnightNavy
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "جاري ربط حساب Google...",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(SudXRoyalNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = SudXChampagneGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "تسجيل الدخول بحساب Google",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = SudXMidnightNavy
                        )
                    }
                }
            }

            if (!authError.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SudXCrimsonSoft,
                    border = BorderStroke(1.dp, SudXCrimson.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = SudXCrimson,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = authError,
                            style = MaterialTheme.typography.bodySmall,
                            color = SudXCrimson,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onDismissFeedback) {
                            Text("حسناً", color = SudXCrimson, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (!authSuccessMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SudXEmeraldSoft,
                    border = BorderStroke(1.dp, SudXEmerald.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SudXEmeraldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = authSuccessMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = SudXEmeraldDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(
    title: String,
    subtitle: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .minimumInteractiveComponentSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = SudXGoldTint,
                checkedTrackColor = SudXRoyalNavy,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}
