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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.DailyStudyStat
import com.example.model.PomodoroSessionLog
import com.example.model.StatsTimeFilter
import com.example.model.SubjectStudyStat
import com.example.ui.components.PremiumActionButton
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
import com.example.util.StudyStatsAggregator
import com.example.viewmodel.TutorViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudyStatsScreen(
    viewModel: TutorViewModel,
    onBack: () -> Unit,
    onOpenPomodoroForSubject: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val allSessions by viewModel.pomodoroSessions.collectAsStateWithLifecycle()
    val pomodoroState by viewModel.pomodoroState.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf(StatsTimeFilter.ALL) }

    val filteredSessions = remember(allSessions, selectedFilter) {
        StudyStatsAggregator.filterByTime(allSessions, selectedFilter)
    }
    val subjectStats = remember(filteredSessions) {
        StudyStatsAggregator.aggregateBySubject(filteredSessions)
    }
    val dailyStats = remember(allSessions) {
        StudyStatsAggregator.aggregateLast7Days(allSessions)
    }

    val totalMinutes = remember(filteredSessions) {
        filteredSessions.sumOf { it.durationMinutes }
    }
    val totalSessionsCount = filteredSessions.size
    val topSubject = subjectStats.firstOrNull()

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
                        .testTag("stats_back_button")
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
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = SudXChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "إحصائيات المذاكرة • المعلم الذكي",
                        style = MaterialTheme.typography.titleMedium,
                        color = SudXGoldTint,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "تحليل ساعات التركيز لكل مادة من جلسات بومودورو",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE2E8F0)
                    )
                }

                IconButton(
                    onClick = { onOpenPomodoroForSubject(null) },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("stats_open_pomodoro_header_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "فتح مؤقت بومودورو",
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
            // 1. Time Period Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatsTimeFilter.entries.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    text = filter.titleAr,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SudXRoyalNavy,
                                selectedLabelColor = SudXGoldTint,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outlineVariant,
                                selectedBorderColor = SudXChampagneGold
                            ),
                            modifier = Modifier.testTag("stats_filter_${filter.name}")
                        )
                    }
                }
            }

            // 2. Hero Summary KPI Card
            item {
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
                        .testTag("stats_hero_summary_card")
                ) {
                    Column(
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
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ملخص إنجازك (${selectedFilter.titleAr})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SudXSoftGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (totalMinutes > 0) {
                                        StudyStatsAggregator.formatDurationAr(totalMinutes)
                                    } else {
                                        "0 دقيقة"
                                    },
                                    style = MaterialTheme.typography.headlineMedium,
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
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${StudyStatsAggregator.formatHoursDecimal(totalMinutes)} س",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SudXChampagneGold
                                    )
                                    Text(
                                        text = "إجمالي الساعات",
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
                            HeroMiniStatPill(
                                icon = Icons.Default.CheckCircle,
                                label = "الجلسات المكتملة",
                                value = "$totalSessionsCount جلسة",
                                tint = SudXEmerald,
                                modifier = Modifier.weight(1f)
                            )
                            HeroMiniStatPill(
                                icon = Icons.Default.EmojiEvents,
                                label = "أكثر مادة تركيزاً",
                                value = topSubject?.let {
                                    StudyStatsAggregator.shortSubjectLabel(it.subjectTitle)
                                } ?: "لم تُسجل بعد",
                                tint = SudXChampagneGold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 3. Empty State OR Charts & Breakdown
            if (filteredSessions.isEmpty()) {
                item {
                    PremiumSurfaceCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stats_empty_state")
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
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = null,
                                    tint = SudXBronzeGold,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (allSessions.isEmpty()) {
                                    "لا توجد جلسات بومودورو مكتملة بعد"
                                } else {
                                    "لا توجد جلسات مسجلة في فترة «${selectedFilter.titleAr}»"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (pomodoroState.completedSessionsTotal > 0 && allSessions.isEmpty()) {
                                    "أكمل جلسة بومودورو جديدة واختر المادة الدراسية ليتم رسم ساعات المذاكرة تلقائياً بالرسوم البيانية."
                                } else {
                                    "اختر المادة الدراسية داخل مؤقت بومودورو وأتمم جلسة التركيز، وستظهر هنا ساعات مذاكرتك لكل مادة وتقدمك اليومي تلقائياً."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PremiumActionButton(
                                onClick = { onOpenPomodoroForSubject(null) },
                                modifier = Modifier.testTag("stats_start_first_pomodoro_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = SudXChampagneGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ابدأ جلسة بومودورو الآن",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // 3A. Vico Bar/Column Chart: Study Hours per Subject
                item {
                    PremiumSurfaceCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stats_subject_bar_chart_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.BarChart,
                                        contentDescription = null,
                                        tint = SudXBronzeGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "ساعات المذاكرة لكل مادة",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "مقارنة إجمالي الساعات المنجزة حسب المادة (بالساعات)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            SubjectHoursColumnChart(
                                subjectStats = subjectStats,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(230.dp)
                            )
                        }
                    }
                }

                // 3B. Vico Line Chart: Last 7 Days Study Activity Trend
                item {
                    PremiumSurfaceCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stats_weekly_line_chart_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                                    contentDescription = null,
                                    tint = SudXEmeraldDark,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "منحنى التركيز خلال آخر 7 أيام",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "توزيع دقائق المذاكرة اليومية على مدار الأسبوع",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            WeeklyMinutesLineChart(
                                dailyStats = dailyStats,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(210.dp)
                            )
                        }
                    }
                }

                // 4. Subject Breakdown Header
                item {
                    Text(
                        text = "تفصيل الساعات حسب المادة (${subjectStats.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                itemsIndexed(subjectStats, key = { _, item -> item.subjectTitle }) { index, stat ->
                    SubjectStatBreakdownCard(
                        rank = index + 1,
                        stat = stat,
                        onStudySubject = { onOpenPomodoroForSubject(stat.subjectTitle) }
                    )
                }

                // 5. Recent Completed Sessions Log Header
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
                                text = "سجل جلسات بومودورو المكتملة",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "${filteredSessions.size} جلسة",
                            style = MaterialTheme.typography.labelMedium,
                            color = SudXBronzeGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(filteredSessions.take(25), key = { it.id }) { session ->
                    CompletedSessionHistoryCard(
                        session = session,
                        onDelete = { viewModel.deletePomodoroSessionLog(session.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun HeroMiniStatPill(
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
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFCBD5E1),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = SudXGoldTint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SubjectHoursColumnChart(
    subjectStats: List<SubjectStudyStat>,
    modifier: Modifier = Modifier
) {
    val displayed = remember(subjectStats) { subjectStats.take(7) }
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(displayed) {
        if (displayed.isNotEmpty()) {
            val hoursList = displayed.map { it.totalHours.toDouble().coerceAtLeast(0.05) }
            modelProducer.runTransaction {
                columnSeries {
                    series(hoursList)
                }
            }
        }
    }

    val bottomLabels = remember(displayed) {
        displayed.map { StudyStatsAggregator.shortSubjectLabel(it.subjectTitle) }
    }

    val bottomValueFormatter = remember(bottomLabels) {
        CartesianValueFormatter { _, value, _ ->
            bottomLabels.getOrElse(value.toInt()) { "" }
        }
    }

    val startValueFormatter = remember {
        CartesianValueFormatter { _, value, _ ->
            String.format(Locale.US, "%.1f س", value)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        ProvideVicoTheme(
            rememberM3VicoTheme(
                columnCartesianLayerColors = listOf(SudXChampagneGold, SudXRoyalNavy)
            )
        ) {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                fill = fill(SudXChampagneGold),
                                thickness = 18.dp
                            )
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        valueFormatter = startValueFormatter
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = bottomValueFormatter
                    )
                ),
                modelProducer = modelProducer,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun WeeklyMinutesLineChart(
    dailyStats: List<DailyStudyStat>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(dailyStats) {
        if (dailyStats.isNotEmpty()) {
            val minutesList = dailyStats.map { it.totalMinutes.toDouble() }
            modelProducer.runTransaction {
                lineSeries {
                    series(minutesList)
                }
            }
        }
    }

    val dayLabels = remember(dailyStats) { dailyStats.map { it.dayLabel } }

    val bottomValueFormatter = remember(dayLabels) {
        CartesianValueFormatter { _, value, _ ->
            dayLabels.getOrElse(value.toInt()) { "" }
        }
    }

    val startValueFormatter = remember {
        CartesianValueFormatter { _, value, _ ->
            "${value.toInt()} د"
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        ProvideVicoTheme(
            rememberM3VicoTheme(
                lineCartesianLayerColors = listOf(SudXEmeraldDark, SudXChampagneGold)
            )
        ) {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(
                        valueFormatter = startValueFormatter
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = bottomValueFormatter
                    )
                ),
                modelProducer = modelProducer,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun SubjectStatBreakdownCard(
    rank: Int,
    stat: SubjectStudyStat,
    onStudySubject: () -> Unit
) {
    val percent = (stat.shareFraction * 100f).toInt().coerceIn(1, 100)

    PremiumSurfaceCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stats_subject_card_${stat.subjectTitle}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (rank == 1) SudXRoyalNavy else SudXNavySoft)
                        .border(
                            1.dp,
                            if (rank == 1) SudXChampagneGold else SudXGoldBorder,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (rank == 1) SudXChampagneGold else SudXRoyalNavy
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stat.subjectTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${StudyStatsAggregator.formatDurationAr(stat.totalMinutes)} (${StudyStatsAggregator.formatHoursDecimal(stat.totalMinutes)} ساعة) • ${stat.sessionsCount} جلسات",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = onStudySubject,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = SudXBronzeGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "مذاكرة",
                        style = MaterialTheme.typography.labelMedium,
                        color = SudXBronzeGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { stat.shareFraction.coerceIn(0.02f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (rank == 1) SudXChampagneGold else SudXRoyalNavy,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = SudXBronzeGold
                )
            }
        }
    }
}

@Composable
private fun CompletedSessionHistoryCard(
    session: PomodoroSessionLog,
    onDelete: () -> Unit
) {
    val formattedDate = remember(session.completedAt) {
        SimpleDateFormat("dd MMM • hh:mm a", Locale("ar")).format(Date(session.completedAt))
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stats_session_log_${session.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SudXEmeraldSoft)
                    .border(1.dp, SudXEmerald.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = SudXEmeraldDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.subjectTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (session.goal.isNotBlank()) {
                    Text(
                        text = session.goal,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = SudXBronzeGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${session.durationMinutes} دقيقة  •  $formattedDate",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("delete_stats_log_${session.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف السجل",
                    tint = SudXCrimson.copy(alpha = 0.78f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
