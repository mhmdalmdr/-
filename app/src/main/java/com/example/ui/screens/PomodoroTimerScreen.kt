package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CurriculumData
import com.example.model.PomodoroPhase
import com.example.model.PomodoroPreset
import com.example.ui.components.PremiumActionButton
import com.example.ui.components.PremiumOutlinedActionButton
import com.example.ui.components.PremiumSurfaceCard
import com.example.ui.components.SudXHeaderSurface
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
import com.example.viewmodel.TutorViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PomodoroTimerScreen(
    viewModel: TutorViewModel,
    onBack: () -> Unit,
    onAskTeacher: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val state by viewModel.pomodoroState.collectAsStateWithLifecycle()
    var showCustomDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )

    fun ensureNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val phaseAccentColor by animateColorAsState(
        targetValue = when (state.phase) {
            PomodoroPhase.FOCUS -> SudXChampagneGold
            PomodoroPhase.SHORT_BREAK -> SudXEmerald
            PomodoroPhase.LONG_BREAK -> Color(0xFF60A5FA)
        },
        animationSpec = tween(350),
        label = "phase_accent"
    )

    val phaseSecondaryColor by animateColorAsState(
        targetValue = when (state.phase) {
            PomodoroPhase.FOCUS -> SudXSoftGold
            PomodoroPhase.SHORT_BREAK -> Color(0xFF6EE7B7)
            PomodoroPhase.LONG_BREAK -> Color(0xFF93C5FD)
        },
        animationSpec = tween(350),
        label = "phase_secondary"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = state.progressFraction,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "pomodoro_ring_progress"
    )

    val pulseTransition = rememberInfiniteTransition(label = "pomodoro_pulse")
    val runningGlow by pulseTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "running_glow"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // SudX Luxury Top Bar
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
                        .testTag("pomodoro_back_button")
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
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = SudXChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "مؤقت المذاكرة • بومودورو",
                        style = MaterialTheme.typography.titleMedium,
                        color = SudXGoldTint,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "تقنية بومودورو لتنظيم التركيز والاستراحات",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE2E8F0)
                    )
                }

                IconButton(
                    onClick = { viewModel.togglePomodoroSound(!state.soundEnabled) },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("pomodoro_sound_header_toggle")
                ) {
                    Icon(
                        imageVector = if (state.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = if (state.soundEnabled) "إيقاف التنبيه الصوتي" else "تفعيل التنبيه الصوتي",
                        tint = if (state.soundEnabled) SudXChampagneGold else Color(0xFF94A3B8)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .align(Alignment.CenterHorizontally),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Phase Completion Celebration Banner
            item {
                AnimatedVisibility(visible = !state.lastCompletedMessage.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SudXEmeraldSoft,
                        border = BorderStroke(1.dp, SudXEmerald.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pomodoro_completion_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SudXEmeraldDark,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = state.lastCompletedMessage.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = SudXMidnightNavy,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearPomodoroCompletionBanner() },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "إغلاق التنبيه",
                                    tint = SudXEmeraldDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 1. Phase Switcher Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PomodoroPhase.entries.forEach { phase ->
                        val isSelected = state.phase == phase
                        val chipAccent = when (phase) {
                            PomodoroPhase.FOCUS -> SudXChampagneGold
                            PomodoroPhase.SHORT_BREAK -> SudXEmerald
                            PomodoroPhase.LONG_BREAK -> Color(0xFF60A5FA)
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) SudXRoyalNavy else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                width = if (isSelected) 1.4.dp else 1.dp,
                                color = if (isSelected) chipAccent else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.selectPomodoroPhase(phase) }
                                .minimumInteractiveComponentSize()
                                .testTag("pomodoro_phase_${phase.name}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (phase) {
                                        PomodoroPhase.FOCUS -> Icons.Default.Timer
                                        PomodoroPhase.SHORT_BREAK -> Icons.Default.Coffee
                                        PomodoroPhase.LONG_BREAK -> Icons.Default.SelfImprovement
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) chipAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = phase.shortTitleAr,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    color = if (isSelected) SudXGoldTint else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // 2. Hero Luxury Circular Chronometer Card
            item {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = SudXMidnightNavy,
                    border = BorderStroke(
                        1.3.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                phaseAccentColor.copy(alpha = 0.85f),
                                SudXSoftGold.copy(alpha = 0.28f),
                                phaseAccentColor.copy(alpha = 0.65f)
                            )
                        )
                    ),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pomodoro_chronometer_card")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        SudXMidnightNavy,
                                        SudXRoyalNavy,
                                        SudXSapphireSlate.copy(alpha = 0.88f)
                                    )
                                )
                            )
                            .padding(horizontal = 18.dp, vertical = 22.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Phase Title & Subtitle
                            Text(
                                text = state.phase.titleAr,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = phaseSecondaryColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.phase.subtitleAr,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Responsive Circular Clock Dial
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                val dialSize = minOf(maxWidth * 0.72f, 250.dp).coerceAtLeast(196.dp)
                                val activeGlowAlpha = if (state.isRunning) runningGlow else 0.45f

                                Box(
                                    modifier = Modifier.size(dialSize),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val strokeWidth = 14.dp.toPx()
                                        val inset = strokeWidth + 10.dp.toPx()
                                        val arcDiameter = size.minDimension - inset * 2
                                        val topLeft = Offset(
                                            x = (size.width - arcDiameter) / 2f,
                                            y = (size.height - arcDiameter) / 2f
                                        )
                                        val arcSize = Size(arcDiameter, arcDiameter)
                                        val center = Offset(size.width / 2f, size.height / 2f)
                                        val outerRadius = (size.minDimension / 2f) - 2.dp.toPx()

                                        // Subtle outer chronometer bezel ticks (24 ticks)
                                        for (tick in 0 until 24) {
                                            val angleRad = Math.toRadians((tick * 15).toDouble())
                                            val tickLength = if (tick % 6 == 0) 7.dp.toPx() else 3.5.dp.toPx()
                                            val startR = outerRadius - tickLength
                                            val p1 = Offset(
                                                center.x + (startR * cos(angleRad)).toFloat(),
                                                center.y + (startR * sin(angleRad)).toFloat()
                                            )
                                            val p2 = Offset(
                                                center.x + (outerRadius * cos(angleRad)).toFloat(),
                                                center.y + (outerRadius * sin(angleRad)).toFloat()
                                            )
                                            drawLine(
                                                color = if (tick % 6 == 0) phaseAccentColor.copy(alpha = 0.55f)
                                                else Color.White.copy(alpha = 0.16f),
                                                start = p1,
                                                end = p2,
                                                strokeWidth = if (tick % 6 == 0) 2.dp.toPx() else 1.2.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                        }

                                        // Track Ring
                                        drawArc(
                                            color = Color.White.copy(alpha = 0.12f),
                                            startAngle = -90f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = arcSize,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                        )

                                        // Active Sweeping Arc
                                        val sweep = 360f * (1f - animatedProgress).coerceIn(0.01f, 1f)
                                        drawArc(
                                            brush = Brush.sweepGradient(
                                                colors = listOf(
                                                    phaseAccentColor,
                                                    phaseSecondaryColor,
                                                    phaseAccentColor.copy(alpha = 0.85f)
                                                ),
                                                center = center
                                            ),
                                            startAngle = -90f,
                                            sweepAngle = sweep,
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = arcSize,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                        )

                                        // Glowing Indicator Dot at Arc Tip
                                        val tipAngleRad = Math.toRadians((-90f + sweep).toDouble())
                                        val arcRadius = arcDiameter / 2f
                                        val tipPos = Offset(
                                            center.x + (arcRadius * cos(tipAngleRad)).toFloat(),
                                            center.y + (arcRadius * sin(tipAngleRad)).toFloat()
                                        )
                                        drawCircle(
                                            color = phaseAccentColor.copy(alpha = 0.35f * activeGlowAlpha),
                                            radius = 12.dp.toPx(),
                                            center = tipPos
                                        )
                                        drawCircle(
                                            color = Color.White,
                                            radius = 5.dp.toPx(),
                                            center = tipPos
                                        )
                                    }

                                    // Center Digital Time Readout
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = Color.White.copy(alpha = 0.10f),
                                            border = BorderStroke(0.8.dp, phaseAccentColor.copy(alpha = 0.45f))
                                        ) {
                                            Text(
                                                text = if (state.isRunning) "قيد التركيز" else "جاهز للبدء",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = phaseSecondaryColor,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                            Text(
                                                text = state.formattedTime,
                                                style = TextStyle(
                                                    fontSize = 46.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = SudXGoldTint,
                                                    fontFeatureSettings = "tnum"
                                                ),
                                                modifier = Modifier.testTag("pomodoro_time_text")
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = state.selectedSubjectTitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFCBD5E1),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 4-Round Pomodoro Cycle Dots
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                repeat(4) { idx ->
                                    val completedInCycle = idx < state.cycleFocusCount
                                    val isCurrentRound = idx == state.cycleFocusCount && state.phase == PomodoroPhase.FOCUS
                                    Box(
                                        modifier = Modifier
                                            .size(if (isCurrentRound) 12.dp else 10.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    completedInCycle -> SudXEmerald
                                                    isCurrentRound -> SudXChampagneGold
                                                    else -> Color.White.copy(alpha = 0.22f)
                                                }
                                            )
                                            .then(
                                                if (isCurrentRound) {
                                                    Modifier.border(1.5.dp, Color.White, CircleShape)
                                                } else {
                                                    Modifier
                                                }
                                            )
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "الجولة ${state.cycleFocusCount + 1} من 4",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFFE2E8F0),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Transport Controls Row: Reset | Play/Pause | Skip
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.resetPomodoro() },
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = SudXGoldTint
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .height(50.dp)
                                        .testTag("pomodoro_reset_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RestartAlt,
                                        contentDescription = "إعادة ضبط المؤقت",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Button(
                                    onClick = {
                                        ensureNotificationPermissionIfNeeded()
                                        viewModel.startOrPausePomodoro()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = phaseAccentColor,
                                        contentColor = SudXMidnightNavy
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .testTag("pomodoro_play_pause_button")
                                ) {
                                    Icon(
                                        imageVector = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when {
                                            state.isRunning -> "إيقاف مؤقت"
                                            state.remainingSeconds < state.totalPhaseSeconds -> "استئناف"
                                            state.phase == PomodoroPhase.FOCUS -> "ابدأ التركيز"
                                            else -> "ابدأ الاستراحة"
                                        },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                OutlinedButton(
                                    onClick = { viewModel.skipPomodoroPhase() },
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = SudXGoldTint
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .height(50.dp)
                                        .testTag("pomodoro_skip_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = "تخطي للمرحلة التالية",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick +5 Minutes Extension Chip
                            TextButton(
                                onClick = { viewModel.addExtraPomodoroMinutes(5) },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("pomodoro_plus_5_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = phaseSecondaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "إضافة 5 دقائق للجلسة الحالية",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = phaseSecondaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 3. Presets & Custom Duration Selector
            item {
                PremiumSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "أنماط المذاكرة (بومودورو)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(
                                onClick = { showCustomDialog = true },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("pomodoro_custom_durations_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = SudXBronzeGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تخصيص الدقائق",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SudXBronzeGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PomodoroPreset.entries.forEach { preset ->
                                val isActivePreset = state.focusMinutes == preset.focusMinutes &&
                                    state.shortBreakMinutes == preset.shortBreakMinutes &&
                                    state.longBreakMinutes == preset.longBreakMinutes

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isActivePreset) SudXGoldTint else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                    border = BorderStroke(
                                        width = if (isActivePreset) 1.3.dp else 1.dp,
                                        color = if (isActivePreset) SudXChampagneGold else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { viewModel.applyPomodoroPreset(preset) }
                                        .minimumInteractiveComponentSize()
                                        .testTag("pomodoro_preset_${preset.name}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = preset.badgeText,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isActivePreset) SudXMidnightNavy else MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = preset.titleAr,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isActivePreset) SudXBronzeGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Subject Link & Study Goal Card
            item {
                val subjectChoices = remember {
                    listOf("عام — الشهادة السودانية") + CurriculumData.subjects.map { it.title }
                }

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
                                text = "المادة وهدف جلسة التركيز",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(subjectChoices) { subjectTitle ->
                                val isSelected = state.selectedSubjectTitle == subjectTitle
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setPomodoroSubject(subjectTitle) },
                                    label = {
                                        Text(
                                            text = subjectTitle,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SudXRoyalNavy,
                                        selectedLabelColor = SudXGoldTint,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                        labelColor = MaterialTheme.colorScheme.onSurface
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

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.sessionGoal,
                            onValueChange = { viewModel.setPomodoroGoal(it) },
                            label = { Text("هدف هذه الجلسة (مثال: حل تمرين النهايات والاتصال)") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SudXChampagneGold,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pomodoro_goal_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PremiumOutlinedActionButton(
                                onClick = {
                                    val noteTitle = if (state.sessionGoal.isNotBlank()) {
                                        state.sessionGoal.trim()
                                    } else {
                                        "جلسة بومودورو: ${state.selectedSubjectTitle}"
                                    }
                                    val noteContent = buildString {
                                        append("تم التخطيط لجلسة تركيز لمدة ${state.focusMinutes} دقيقة في مادة ${state.selectedSubjectTitle}.")
                                        if (state.sessionGoal.isNotBlank()) {
                                            append("\nالهدف: ${state.sessionGoal.trim()}")
                                        }
                                    }
                                    viewModel.saveStudyNote(
                                        title = noteTitle,
                                        content = noteContent,
                                        subjectTitle = state.selectedSubjectTitle,
                                        isPinned = false
                                    )
                                    Toast.makeText(context, "تم حفظ هدف الجلسة في ملاحظاتك التعليمية", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pomodoro_save_note_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkAdd,
                                    contentDescription = null,
                                    tint = SudXBronzeGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "حفظ كملاحظة",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            PremiumActionButton(
                                onClick = {
                                    val goalPart = if (state.sessionGoal.isNotBlank()) {
                                        "حول «${state.sessionGoal.trim()}»"
                                    } else {
                                        ""
                                    }
                                    onAskTeacher(
                                        "يا أستاذ، أبدأ الآن جلسة مذاكرة بومودورو في مادة ${state.selectedSubjectTitle} $goalPart. اعطني خطة مراجعة سريعة و3 نقاط أساسية أركز عليها."
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pomodoro_ask_teacher_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = SudXChampagneGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "استشارة المعلم",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 5. Focus Statistics & Timer Preferences Card
            item {
                PremiumSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "إحصائيات التركيز والإعدادات",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (state.completedSessionsTotal > 0) {
                                TextButton(
                                    onClick = { viewModel.resetPomodoroStatistics() },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "تصفير العداد",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PomodoroStatBox(
                                value = state.completedSessionsTotal.toString(),
                                label = "جلسة منجزة",
                                accentColor = SudXBronzeGold,
                                modifier = Modifier.weight(1f)
                            )
                            PomodoroStatBox(
                                value = "${state.totalFocusMinutes} د",
                                label = "إجمالي التركيز",
                                accentColor = SudXEmeraldDark,
                                modifier = Modifier.weight(1f)
                            )
                            PomodoroStatBox(
                                value = "${state.cycleFocusCount}/4",
                                label = "دورة البومودورو",
                                accentColor = SudXSapphireSlate,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sound & Auto-start toggles
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = SudXBronzeGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تنبيه صوتي واهتزاز عند انتهاء المؤقت",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "إصدار نغمة تنبيه هادئة عند إكمال التركيز أو الاستراحة",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = state.soundEnabled,
                                onCheckedChange = { viewModel.togglePomodoroSound(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SudXGoldTint,
                                    checkedTrackColor = SudXRoyalNavy
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SudXEmeraldDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "بدء المرحلة التالية تلقائياً",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "تشغيل مؤقت الاستراحة أو التركيز فور انتهاء المرحلة السابقة",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = state.autoStartNext,
                                onCheckedChange = { viewModel.togglePomodoroAutoStart(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SudXGoldTint,
                                    checkedTrackColor = SudXEmeraldDark
                                )
                            )
                        }
                    }
                }
            }

            // 6. Pomodoro Quick Study Guide Card
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SudXGoldTint.copy(alpha = 0.72f),
                    border = BorderStroke(1.dp, SudXGoldBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "كيف تستفيد من تقنية بومودورو للشهادة السودانية؟",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = SudXMidnightNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val steps = listOf(
                            "1. حدد المادة وهدفاً واضحاً (مثل: تلخيص وحدة أو حل 10 مسائل).",
                            "2. اضغط «ابدأ التركيز» لمدة 25 دقيقة دون تصفح الهاتف أو أي مشتتات.",
                            "3. خذ استراحة قصيرة (5 دقائق) لإراحة بصرك وتجديد نشاطك.",
                            "4. بعد كل 4 جولات تركيز، استمتع باستراحة طويلة (15 دقيقة) ثم واصل بقوة."
                        )
                        steps.forEach { step ->
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodySmall,
                                color = SudXRoyalNavy,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(18.dp)) }
        }
    }

    if (showCustomDialog) {
        PomodoroCustomDurationDialog(
            initialFocus = state.focusMinutes,
            initialShortBreak = state.shortBreakMinutes,
            initialLongBreak = state.longBreakMinutes,
            onDismiss = { showCustomDialog = false },
            onSave = { focusMin, shortMin, longMin ->
                viewModel.updateCustomPomodoroDurations(focusMin, shortMin, longMin)
                showCustomDialog = false
            }
        )
    }
}

@Composable
private fun PomodoroStatBox(
    value: String,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SudXNavySoft.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, SudXGoldBorder.copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = SudXMidnightNavy,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PomodoroCustomDurationDialog(
    initialFocus: Int,
    initialShortBreak: Int,
    initialLongBreak: Int,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int) -> Unit
) {
    var focusMin by remember { mutableIntStateOf(initialFocus) }
    var shortMin by remember { mutableIntStateOf(initialShortBreak) }
    var longMin by remember { mutableIntStateOf(initialLongBreak) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "تخصيص مدد مؤقت بومودورو",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DurationStepperRow(
                    title = "مدة التركيز (بالدقائق)",
                    value = focusMin,
                    min = 5,
                    max = 90,
                    step = 5,
                    onChange = { focusMin = it }
                )
                DurationStepperRow(
                    title = "الاستراحة القصيرة",
                    value = shortMin,
                    min = 1,
                    max = 25,
                    step = 1,
                    onChange = { shortMin = it }
                )
                DurationStepperRow(
                    title = "الاستراحة الطويلة",
                    value = longMin,
                    min = 5,
                    max = 45,
                    step = 5,
                    onChange = { longMin = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(focusMin, shortMin, longMin) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SudXRoyalNavy,
                    contentColor = SudXGoldTint
                )
            ) {
                Text("حفظ الإعدادات", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
private fun DurationStepperRow(
    title: String,
    value: Int,
    min: Int,
    max: Int,
    step: Int,
    onChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(
                onClick = { onChange((value - step).coerceAtLeast(min)) },
                enabled = value > min,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "إنقاص $title"
                )
            }
            Text(
                text = "$value د",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = { onChange((value + step).coerceAtMost(max)) },
                enabled = value < max,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "زيادة $title"
                )
            }
        }
    }
}
