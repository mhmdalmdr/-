package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CurriculumData
import com.example.model.PomodoroUiState
import com.example.model.Subject
import com.example.ui.components.GlassmorphicSmartTeacherCard
import com.example.ui.components.PremiumInteractiveCard
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
import com.example.ui.theme.sudxEmeraldFg
import com.example.ui.theme.sudxEmeraldSoftBg
import com.example.ui.theme.sudxGoldBorder
import com.example.ui.theme.sudxGoldFg
import com.example.ui.theme.sudxGoldTintBg
import com.example.ui.theme.sudxIsDarkTheme
import com.example.ui.theme.sudxNavyFg
import com.example.ui.theme.sudxNavySoftBg
import com.example.util.StudentAccount

@Composable
fun HomeScreen(
    onSelectSubject: (Subject) -> Unit,
    onNavigateToTextbooks: () -> Unit,
    onNavigateToPastExams: () -> Unit,
    onNavigateToSmartTeacher: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToNotes: () -> Unit = {},
    notesCount: Int = 0,
    pomodoroState: PomodoroUiState = PomodoroUiState(),
    onNavigateToPomodoro: () -> Unit = {},
    loggedStudyMinutes: Int = 0,
    onNavigateToStats: () -> Unit = {},
    onNavigateToAiQuiz: () -> Unit = {},
    quizResultsCount: Int = 0,
    quizMasteryPercent: Int = 0,
    weakTopicsCount: Int = 0,
    onNavigateToProgressAssessment: () -> Unit = {},
    onNavigateToForum: () -> Unit = {},
    onNavigateToTeacherTips: () -> Unit = {},
    studentAccount: StudentAccount? = null,
    academicStreamTitle: String = "القسم العلمي",
    isDarkTheme: Boolean = false,
    onToggleNightMode: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {},
    onOpenMahdiyaLesson: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Flagship SudX Luxury Hero Header
        item {
            val headerShape = RoundedCornerShape(26.dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = headerShape,
                        ambientColor = SudXMidnightNavy.copy(alpha = 0.28f),
                        spotColor = SudXChampagneGold.copy(alpha = 0.22f)
                    ),
                colors = CardDefaults.cardColors(containerColor = SudXMidnightNavy),
                shape = headerShape,
                border = BorderStroke(
                    1.2.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            SudXChampagneGold.copy(alpha = 0.85f),
                            SudXSoftGold.copy(alpha = 0.30f),
                            SudXChampagneGold.copy(alpha = 0.60f)
                        )
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SudXMidnightNavy)
                ) {
                    // Silk & gold arabesque hero art blended subtly underneath
                    Image(
                        painter = painterResource(id = R.drawable.img_sudx_hero_banner),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(0.38f)
                    )

                    // Rich directional obsidian gradient overlay for crisp text legibility
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        SudXMidnightNavy.copy(alpha = 0.92f),
                                        SudXRoyalNavy.copy(alpha = 0.80f),
                                        SudXSapphireSlate.copy(alpha = 0.55f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SudXGoldTint.copy(alpha = 0.12f))
                                    .border(
                                        1.dp,
                                        SudXChampagneGold.copy(alpha = 0.65f),
                                        RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SudXChampagneGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "المعلم الذكي • الشهادة السودانية",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SudXSoftGold,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1-tap Night Mode Toggle Pill
                                Surface(
                                    onClick = onToggleNightMode,
                                    shape = RoundedCornerShape(50),
                                    color = if (isDarkTheme) {
                                        SudXChampagneGold.copy(alpha = 0.22f)
                                    } else {
                                        SudXGoldTint.copy(alpha = 0.14f)
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        SudXChampagneGold.copy(alpha = if (isDarkTheme) 0.85f else 0.55f)
                                    ),
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("home_night_mode_toggle")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                            contentDescription = if (isDarkTheme) "التبديل إلى الوضع النهاري" else "التبديل إلى الوضع الليلي",
                                            tint = SudXChampagneGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = if (isDarkTheme) "نهاري" else "الوضع الليلي",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SudXGoldTint,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = onNavigateToSettings,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(SudXGoldTint.copy(alpha = 0.14f))
                                        .border(1.dp, SudXChampagneGold.copy(alpha = 0.55f), CircleShape)
                                        .minimumInteractiveComponentSize()
                                        .testTag("home_settings_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "الإعدادات وحساب الطالب",
                                        tint = SudXGoldTint,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                // Embossed SudX Emblem Badge
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(SudXMidnightNavy.copy(alpha = 0.85f))
                                        .border(1.2.dp, SudXChampagneGold.copy(alpha = 0.75f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_sudx_icon_fg),
                                        contentDescription = "شعار المعلم الذكي",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (studentAccount != null) "أهلاً بك، ${studentAccount.displayName}" else "مرحباً بك في المعلم الذكي",
                            style = MaterialTheme.typography.headlineMedium,
                            color = SudXGoldTint,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 23.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "منصة النخبة الذكية لطلاب الشهادة الثانوية السودانية — مراجعة متقنة، كتب رسمية، وامتحانات 2026 المحدّثة بتوجيه تفاعلي.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 23.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Student Account / Google Sign-In Status Pill
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, SudXChampagneGold.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    if (studentAccount != null) onNavigateToSettings() else onGoogleSignIn()
                                }
                                .minimumInteractiveComponentSize()
                                .testTag(if (studentAccount != null) "home_account_banner" else "home_google_sign_in_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (studentAccount != null) Icons.Default.VerifiedUser else Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = if (studentAccount != null) Color(0xFF4ADE80) else SudXChampagneGold,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (studentAccount != null) {
                                            "متصل بحساب Google • $academicStreamTitle"
                                        } else {
                                            "تسجيل الدخول بحساب Google"
                                        },
                                        style = MaterialTheme.typography.labelLarge,
                                        color = SudXGoldTint,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (studentAccount != null) {
                                            studentAccount.email.ifBlank { "اضغط لإدارة الحساب والإعدادات" }
                                        } else {
                                            "اربط حسابك لحفظ ملفك كطالب شهادة سودانية"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = if (studentAccount != null) "الإعدادات" else "تسجيل الدخول",
                                    tint = SudXChampagneGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Resources Section (الكتب المدرسية & امتحانات سابقة)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "الموارد التعليمية الأساسية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Resource 1: الكتب المدرسية (Textbooks)
                    PremiumInteractiveCard(
                        onClick = onNavigateToTextbooks,
                        cornerRadius = 20.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("resource_textbooks_button")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(sudxNavySoftBg())
                                    .border(1.dp, sudxNavyFg().copy(alpha = 0.22f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = "الكتب المدرسية",
                                    tint = sudxNavyFg(),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "الكتب المدرسية",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "مقررات وزارة التربية المعتمدة",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Resource 2: امتحانات سابقة (Past Exams)
                    PremiumInteractiveCard(
                        onClick = onNavigateToPastExams,
                        cornerRadius = 20.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("resource_pastexams_button")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(sudxGoldTintBg())
                                    .border(1.dp, sudxGoldBorder(), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = "امتحانات 2026 الجديدة",
                                    tint = sudxGoldFg(),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "امتحانات 2026 الجديدة",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "نماذج نوفمبر 2026م المحدّثة بتوجيه ذكي",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 3. Customized Study Schedule & Reminders Section
        item {
            PremiumInteractiveCard(
                onClick = onNavigateToSchedule,
                cornerRadius = 22.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_study_schedule_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(sudxEmeraldSoftBg())
                            .border(1.dp, SudXEmerald.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "جدول المذاكرة",
                            tint = sudxEmeraldFg(),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "جدول المذاكرة والتذكيرات",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(sudxGoldTintBg())
                                    .border(0.8.dp, sudxGoldBorder(), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = null,
                                        tint = sudxGoldFg(),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "تنبيهات مخصصة",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = sudxGoldFg(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "نظم جدولك اليومي والأسبوعي لكل مادة مع إشعارات تنبيه ذكية",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 19.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "فتح الجدول",
                        tint = sudxEmeraldFg(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 3.5 Educational Notes (Room Local Database) Section
        item {
            val notesBadgeLabel = when (notesCount) {
                0 -> "ابدأ التدوين"
                1 -> "ملاحظة واحدة"
                2 -> "ملاحظتان"
                else -> "$notesCount ملاحظات"
            }
            PremiumInteractiveCard(
                onClick = onNavigateToNotes,
                cornerRadius = 22.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_study_notes_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(sudxGoldTintBg())
                            .border(1.dp, sudxGoldBorder(), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "ملاحظاتي التعليمية",
                            tint = sudxGoldFg(),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ملاحظاتي التعليمية",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(sudxNavySoftBg())
                                    .border(0.8.dp, sudxNavyFg().copy(alpha = 0.22f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = notesBadgeLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = sudxNavyFg(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "احفظ ملخصات الدروس والقوانين الهامة في قاعدة بيانات Room للعودة إليها لاحقاً",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 19.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "عرض الملاحظات",
                        tint = sudxGoldFg(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 3.7 Pomodoro Study Timer Card
        item {
            val pomodoroBadgeText = if (pomodoroState.isRunning) {
                "${pomodoroState.formattedTime} • قيد التشغيل"
            } else if (pomodoroState.completedSessionsTotal > 0) {
                "${pomodoroState.completedSessionsTotal} جلسات • ${pomodoroState.focusMinutes}/${pomodoroState.shortBreakMinutes} د"
            } else {
                "تقنية ${pomodoroState.focusMinutes}/${pomodoroState.shortBreakMinutes} دقيقة"
            }

            PremiumInteractiveCard(
                onClick = onNavigateToPomodoro,
                cornerRadius = 22.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_pomodoro_timer_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SudXRoyalNavy)
                            .border(1.2.dp, SudXChampagneGold.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "مؤقت المذاكرة بومودورو",
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "مؤقت المذاكرة (بومودورو)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (pomodoroState.isRunning) sudxEmeraldSoftBg() else sudxGoldTintBg())
                                    .border(
                                        0.8.dp,
                                        if (pomodoroState.isRunning) SudXEmerald.copy(alpha = 0.6f) else sudxGoldBorder(),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = pomodoroBadgeText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (pomodoroState.isRunning) sudxEmeraldFg() else sudxGoldFg(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (pomodoroState.isRunning) {
                                "المرحلة الحالية: ${pomodoroState.phase.shortTitleAr} (${pomodoroState.selectedSubjectTitle})"
                            } else {
                                "نظم وقتك بين جلسات التركيز والاستراحات لإنجاز أكبر في المذاكرة"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 19.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "فتح مؤقت بومودورو",
                        tint = sudxGoldFg(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 3.8 Study Statistics & Charts Card
        item {
            val hoursBadge = if (loggedStudyMinutes > 0) {
                com.example.util.StudyStatsAggregator.formatDurationAr(loggedStudyMinutes)
            } else {
                "رسوم بيانية تفاعلية"
            }

            PremiumInteractiveCard(
                onClick = onNavigateToStats,
                cornerRadius = 22.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_study_stats_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(sudxNavySoftBg())
                            .border(1.dp, sudxNavyFg().copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "إحصائيات المذاكرة",
                            tint = sudxNavyFg(),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "إحصائيات المذاكرة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(sudxGoldTintBg())
                                    .border(0.8.dp, sudxGoldBorder(), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = hoursBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = sudxGoldFg(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تابع إجمالي ساعات المذاكرة لكل مادة ومنحنى نشاطك الأسبوعي",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 19.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "عرض إحصائيات المذاكرة",
                        tint = sudxNavyFg(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 3.9 AI-Driven Guided Quiz Generator Card
        item {
            PremiumInteractiveCard(
                onClick = onNavigateToAiQuiz,
                cornerRadius = 22.dp,
                containerColor = sudxGoldTintBg(),
                borderColor = sudxGoldBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_ai_quiz_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SudXRoyalNavy)
                            .border(1.2.dp, SudXChampagneGold.copy(alpha = 0.75f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "مولّد الاختبارات الذكي",
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "مولّد الاختبارات الذكي (AI)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SudXRoyalNavy)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "توجيه ذكي",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SudXChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اختر المادة والموضوع، وتدرّب على أسئلة اختيار من متعدد تقودك للحل بتلميحات ذكية",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 19.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "فتح مولّد الاختبارات الذكي",
                        tint = sudxGoldFg(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 3.95 Collaborative Student Forum Card (AI-Supervised)
        item {
            PremiumInteractiveCard(
                onClick = onNavigateToForum,
                cornerRadius = 22.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                borderColor = SudXChampagneGold.copy(alpha = 0.65f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_student_forum_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SudXMidnightNavy)
                            .border(1.2.dp, SudXChampagneGold, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = "المنتدى الطلابي",
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "المنتدى الطلابي",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(sudxGoldTintBg())
                                    .border(0.8.dp, sudxGoldBorder(), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "إشراف ذكي",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = sudxGoldFg(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "شارك استفساراتك وساعد زملاءك في حل المسائل الدراسية تحت إشراف المعلم الذكي",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 19.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "فتح المنتدى الطلابي",
                        tint = sudxGoldFg(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 3.96 Renewable Teacher's Tips Card
        item {
            PremiumInteractiveCard(
                onClick = onNavigateToTeacherTips,
                cornerRadius = 22.dp,
                containerColor = sudxGoldTintBg(),
                borderColor = sudxGoldBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_teacher_tips_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SudXRoyalNavy)
                            .border(1.2.dp, SudXChampagneGold.copy(alpha = 0.78f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TipsAndUpdates,
                            contentDescription = "نصائح المعلم المتجددة",
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "نصائح المعلم المتجددة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SudXRoyalNavy)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "متجددة بالـ AI",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SudXChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "توجيهات عملية للتعامل مع قلق الاختبارات واستراتيجيات مذاكرة المواد العلمية والأدبية",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 19.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "فتح نصائح المعلم",
                        tint = sudxGoldFg(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 3.98 Progress Assessment Card
        if (quizResultsCount > 0 || weakTopicsCount > 0) {
            item {
                PremiumInteractiveCard(
                    onClick = onNavigateToProgressAssessment,
                    cornerRadius = 22.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("home_progress_assessment_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(sudxEmeraldSoftBg())
                                .border(1.dp, SudXEmerald.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "تقييم التقدم الأكاديمي",
                                tint = sudxEmeraldFg(),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تقييم التقدم الأكاديمي ($quizMasteryPercent%)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (weakTopicsCount > 0) {
                                    "لديك $weakTopicsCount موضوعات مقترحة للمراجعة المركزة"
                                } else {
                                    "أتممت $quizResultsCount اختبارات بنجاح • استمر في التفوق"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "عرض التقييم",
                            tint = sudxEmeraldFg(),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 4. Standout Glassmorphic & Glowing AI Teacher Section
        item {
            GlassmorphicSmartTeacherCard(
                onNavigateToSmartTeacher = onNavigateToSmartTeacher,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 5. Subjects Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "شبكة المواد الدراسية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "المقررات الأساسية للشهادة الثانوية",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 6. Grid of Subjects
        item {
            val displaySubjects = CurriculumData.subjects
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in displaySubjects.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val sub1 = displaySubjects[i]
                        SubjectGridItem(
                            subject = sub1,
                            onClick = { onSelectSubject(sub1) },
                            modifier = Modifier.weight(1f)
                        )

                        if (i + 1 < displaySubjects.size) {
                            val sub2 = displaySubjects[i + 1]
                            SubjectGridItem(
                                subject = sub2,
                                onClick = { onSelectSubject(sub2) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 7. Featured Focus: أسباب الثورة المهدية
        item {
            PremiumInteractiveCard(
                onClick = onOpenMahdiyaLesson,
                cornerRadius = 20.dp,
                containerColor = sudxGoldTintBg(),
                borderColor = sudxGoldBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_mahdiya_featured_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SudXRoyalNavy)
                            .border(1.dp, SudXChampagneGold.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "درس مراجعة مميز: أسباب الثورة المهدية",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ملخص الـ 4 أسباب والسبب المباشر مع أسئلة الامتحان",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = sudxGoldFg(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectGridItem(
    subject: Subject,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
        "math" -> SubjectMathBg
        "physics" -> SubjectPhysicsBg
        "chemistry" -> SubjectChemBg
        "arabic" -> SubjectArabicBg
        "english" -> SubjectEnglishBg
        else -> SubjectHistoryBg
    }

    val iconColor = when (subject.id) {
        "math" -> SubjectMathFg
        "physics" -> SubjectPhysicsFg
        "chemistry" -> SubjectChemFg
        "arabic" -> SubjectArabicFg
        "english" -> SubjectEnglishFg
        else -> SubjectHistoryFg
    }

    val isDark = sudxIsDarkTheme()
    val effectiveBgColor = if (isDark) iconColor.copy(alpha = 0.22f) else iconBgColor
    val effectiveIconTint = if (isDark) {
        if (subject.id == "history" || subject.id !in listOf("math", "physics", "chemistry", "arabic", "english")) {
            SudXSoftGold
        } else {
            iconBgColor
        }
    } else {
        iconColor
    }

    PremiumInteractiveCard(
        onClick = onClick,
        cornerRadius = 20.dp,
        modifier = modifier.testTag("subject_grid_card_${subject.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(effectiveBgColor)
                        .border(
                            width = 1.dp,
                            color = effectiveIconTint.copy(alpha = 0.28f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = subject.title,
                        tint = effectiveIconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(sudxNavySoftBg())
                        .border(0.8.dp, sudxNavyFg().copy(alpha = 0.18f), RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    val unitsLabel = when (subject.unitsCount) {
                        1 -> "وحدة واحدة"
                        2 -> "وحدتان"
                        else -> "${subject.unitsCount} وحدات"
                    }
                    Text(
                        text = unitsLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = sudxNavyFg(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = subject.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "اضغط لاستعراض الدروس والملخصات",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
