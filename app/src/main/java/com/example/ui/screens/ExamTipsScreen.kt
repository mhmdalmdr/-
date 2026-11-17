package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.model.ExamTip
import com.example.model.TeacherTipCategory
import com.example.model.TeacherTipsRefreshState
import com.example.ui.components.PremiumActionButton
import com.example.ui.components.PremiumOutlinedActionButton
import com.example.ui.components.PremiumSurfaceCard
import com.example.ui.components.SudXHeaderSurface
import com.example.ui.theme.SudXChampagneGold
import com.example.ui.theme.SudXEmerald
import com.example.ui.theme.SudXGoldTint
import com.example.ui.theme.SudXMidnightNavy
import com.example.ui.theme.SudXRoyalNavy
import com.example.ui.theme.sudxCrimsonFg
import com.example.ui.theme.sudxCrimsonSoftBg
import com.example.ui.theme.sudxEmeraldFg
import com.example.ui.theme.sudxEmeraldSoftBg
import com.example.ui.theme.sudxGoldBorder
import com.example.ui.theme.sudxGoldFg
import com.example.ui.theme.sudxGoldTintBg
import com.example.ui.theme.sudxNavyFg
import com.example.ui.theme.sudxNavySoftBg

@Composable
fun ExamTipsScreen(
    onAskTeacherForAdvice: (String) -> Unit,
    modifier: Modifier = Modifier,
    tips: List<ExamTip> = CurriculumData.examTips,
    featuredTipIndex: Int = 0,
    refreshState: TeacherTipsRefreshState = TeacherTipsRefreshState.Idle,
    academicStreamTitle: String = "القسم العلمي",
    onRefreshTips: (TeacherTipCategory) -> Unit = {},
    onCycleFeaturedTip: () -> Unit = {},
    onDismissRefreshState: () -> Unit = {},
    onSaveTipToNotes: ((ExamTip) -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    showHeader: Boolean = true
) {
    if (onBack != null) {
        BackHandler(onBack = onBack)
    }

    var selectedCategory by remember { mutableStateOf(TeacherTipCategory.ALL) }
    val context = LocalContext.current

    val filteredTips = remember(tips, selectedCategory) {
        if (selectedCategory == TeacherTipCategory.ALL) {
            tips
        } else {
            tips.filter { it.category == selectedCategory.titleAr }
        }
    }

    val featuredTip = remember(tips, featuredTipIndex) {
        if (tips.isNotEmpty()) {
            tips[featuredTipIndex.mod(tips.size)]
        } else {
            null
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("teacher_tips_screen"),
        contentPadding = PaddingValues(bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Luxury SudX Header (Standalone)
        if (showHeader) {
            item {
                SudXHeaderSurface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
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
                                    .clip(CircleShape)
                                    .background(SudXGoldTint.copy(alpha = 0.14f))
                                    .border(1.dp, SudXChampagneGold.copy(alpha = 0.55f), CircleShape)
                                    .testTag("teacher_tips_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "العودة",
                                    tint = SudXGoldTint
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SudXChampagneGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = SudXMidnightNavy,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "نصائح المعلم المتجددة",
                                style = MaterialTheme.typography.titleMedium,
                                color = SudXGoldTint,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "توجيهات عملية للتعامل مع قلق الاختبارات وإتقان المواد ($academicStreamTitle)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE2E8F0)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (refreshState !is TeacherTipsRefreshState.Loading) {
                                    onRefreshTips(selectedCategory)
                                }
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(CircleShape)
                                .background(SudXChampagneGold.copy(alpha = 0.22f))
                                .border(1.dp, SudXChampagneGold.copy(alpha = 0.7f), CircleShape)
                                .testTag("refresh_teacher_tips_button")
                        ) {
                            if (refreshState is TeacherTipsRefreshState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = SudXChampagneGold,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "توليد نصائح متجددة",
                                    tint = SudXChampagneGold
                                )
                            }
                        }
                    }
                }
            }
        }
        }

        // 2. Teacher's Motivational Greeting Card
        item {
            PremiumSurfaceCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                containerColor = sudxGoldTintBg(),
                borderColor = sudxGoldBorder(),
                cornerRadius = 18.dp,
                elevation = 2.dp
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
                            .background(SudXRoyalNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "«يا بني: النجاح في الشهادة السودانية يبدأ بهدوء النفس، والمذاكرة بذكاء لا بكثرة الساعات دون تركيز. خذ هذه التوجيهات خطوة بخطوة!»",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // 3. Featured Daily / Rotating Tip Card
        if (featuredTip != null) {
            item {
                FeaturedDailyTipCard(
                    tip = featuredTip,
                    totalTipsCount = tips.size,
                    onCycleNext = onCycleFeaturedTip,
                    onAskTeacher = {
                        onAskTeacherForAdvice("يا أستاذ، أريد خطة تطبيقية مفصلة لنصيحة: (${featuredTip.title})")
                    }
                )
            }
        }

        // 4. AI Renewable Tips Trigger & Live Status Banner
        item {
            RenewableTipsControlCard(
                selectedCategory = selectedCategory,
                refreshState = refreshState,
                onGenerateFreshTips = { onRefreshTips(selectedCategory) },
                onDismissState = onDismissRefreshState
            )
        }

        // 5. Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TeacherTipCategory.entries) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category.titleAr,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
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
                            borderColor = sudxGoldBorder(),
                            selectedBorderColor = SudXChampagneGold
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("teacher_tip_category_${category.tag}")
                    )
                }
            }
        }

        // 6. Empty State if Filter Has Zero Items
        if (filteredTips.isEmpty()) {
            item {
                PremiumSurfaceCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    cornerRadius = 18.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.TipsAndUpdates,
                            contentDescription = null,
                            tint = sudxGoldFg(),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا توجد نصائح محفوظة حالياً في محور «${selectedCategory.titleAr}»",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PremiumActionButton(
                            onClick = { onRefreshTips(selectedCategory) },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SudXChampagneGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("توليد نصائح لهذا المحور الآن")
                        }
                    }
                }
            }
        } else {
            // 7. Tips Feed
            items(filteredTips, key = { it.id }) { tip ->
                ExamTipItemCard(
                    tip = tip,
                    onSaveToNotes = onSaveTipToNotes?.let { saver ->
                        {
                            saver(tip)
                            Toast.makeText(
                                context,
                                "تم حفظ النصيحة في ملاحظاتي التعليمية",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onAskTeacher = {
                        onAskTeacherForAdvice("يا أستاذ، كيف أطبق نصيحة: (${tip.title}) بشكل عملي في مذاكرتي؟")
                    }
                )
            }
        }
    }
}

@Composable
private fun FeaturedDailyTipCard(
    tip: ExamTip,
    totalTipsCount: Int,
    onCycleNext: () -> Unit,
    onAskTeacher: () -> Unit
) {
    PremiumSurfaceCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("featured_daily_tip_card"),
        containerColor = sudxNavySoftBg(),
        borderColor = SudXChampagneGold.copy(alpha = 0.72f),
        cornerRadius = 22.dp,
        elevation = 4.dp
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
                            .background(SudXMidnightNavy)
                            .border(1.dp, SudXChampagneGold, RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SudXChampagneGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (tip.isAiGenerated) "نصيحة اليوم • متجددة AI" else "نصيحة اليوم المختارة",
                                style = MaterialTheme.typography.labelSmall,
                                color = SudXGoldTint,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(sudxGoldTintBg())
                            .border(0.8.dp, sudxGoldBorder(), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tip.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = sudxGoldFg(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (totalTipsCount > 1) {
                    Surface(
                        onClick = onCycleNext,
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, sudxGoldBorder()),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("cycle_featured_tip_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "عرض نصيحة أخرى",
                                tint = sudxGoldFg(),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "نصيحة أخرى",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = tip.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 23.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            PremiumOutlinedActionButton(
                onClick = onAskTeacher,
                borderColor = SudXChampagneGold,
                contentColor = sudxNavyFg(),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("ask_about_featured_tip_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = sudxGoldFg(),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ناقش تطبيق هذه النصيحة مع المعلم الذكي",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RenewableTipsControlCard(
    selectedCategory: TeacherTipCategory,
    refreshState: TeacherTipsRefreshState,
    onGenerateFreshTips: () -> Unit,
    onDismissState: () -> Unit
) {
    val isLoading = refreshState is TeacherTipsRefreshState.Loading
    val targetLabel = if (selectedCategory == TeacherTipCategory.ALL) {
        "نصائح متجددة شاملة"
    } else {
        "نصائح جديدة في «${selectedCategory.titleAr}»"
    }

    PremiumSurfaceCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = sudxGoldBorder(),
        cornerRadius = 18.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(sudxGoldTintBg())
                        .border(1.dp, sudxGoldBorder(), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = sudxGoldFg(),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "تجديد النصائح بالذكاء الاصطناعي",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "اطلب 3 نصائح عملية جديدة مخصصة لمحورك الدراسي الحالي",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            PremiumActionButton(
                onClick = {
                    if (!isLoading) onGenerateFreshTips()
                },
                containerColor = SudXRoyalNavy,
                contentColor = SudXGoldTint,
                cornerRadius = 14.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("generate_fresh_tips_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = SudXChampagneGold,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "المعلم الذكي يكتب نصائح جديدة...",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = SudXChampagneGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "توليد $targetLabel",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = refreshState is TeacherTipsRefreshState.Success || refreshState is TeacherTipsRefreshState.Error) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    when (refreshState) {
                        is TeacherTipsRefreshState.Success -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(sudxEmeraldSoftBg())
                                    .border(1.dp, SudXEmerald.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = sudxEmeraldFg(),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تمت إضافة ${refreshState.addedCount} نصائح متجددة في محور «${refreshState.categoryTitle}» بالأعلى!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = sudxEmeraldFg(),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = onDismissState,
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "إغلاق التنبيه",
                                        tint = sudxEmeraldFg(),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        is TeacherTipsRefreshState.Error -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(sudxCrimsonSoftBg())
                                    .border(1.dp, sudxCrimsonFg().copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = sudxCrimsonFg(),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = refreshState.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = sudxCrimsonFg(),
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                PremiumOutlinedActionButton(
                                    onClick = onGenerateFreshTips,
                                    borderColor = sudxCrimsonFg().copy(alpha = 0.6f),
                                    contentColor = sudxCrimsonFg(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                        .testTag("retry_refresh_tips_button")
                                ) {
                                    Text(
                                        text = "إعادة المحاولة",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
fun ExamTipItemCard(
    tip: ExamTip,
    onAskTeacher: () -> Unit,
    onSaveToNotes: (() -> Unit)? = null
) {
    PremiumSurfaceCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("exam_tip_card_${tip.id}"),
        borderColor = if (tip.isAiGenerated) SudXChampagneGold.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outlineVariant,
        cornerRadius = 20.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(sudxNavySoftBg())
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tip.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = sudxNavyFg(),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (tip.isAiGenerated) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(sudxEmeraldSoftBg())
                                .border(0.8.dp, SudXEmerald.copy(alpha = 0.45f), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "متجددة AI",
                                style = MaterialTheme.typography.labelSmall,
                                color = sudxEmeraldFg(),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    } else if (tip.streamTag.isNotBlank() && tip.streamTag != "عام") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(sudxGoldTintBg())
                                .border(0.8.dp, sudxGoldBorder(), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tip.streamTag,
                                style = MaterialTheme.typography.labelSmall,
                                color = sudxGoldFg(),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.TipsAndUpdates,
                    contentDescription = null,
                    tint = sudxGoldFg(),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = tip.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )

            if (tip.actionSteps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(sudxNavySoftBg())
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "خطوات التنفيذ العملية:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = sudxNavyFg()
                    )
                    tip.actionSteps.forEachIndexed { idx, step ->
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(SudXRoyalNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SudXChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 19.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(sudxGoldTintBg())
                    .border(0.8.dp, sudxGoldBorder(), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = sudxGoldFg(),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "القاعدة الذهبية: ${tip.importantRule}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PremiumActionButton(
                    onClick = onAskTeacher,
                    containerColor = SudXRoyalNavy,
                    contentColor = SudXGoldTint,
                    cornerRadius = 14.dp,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("ask_about_tip_${tip.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SudXChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "استشر الأستاذ",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (onSaveToNotes != null) {
                    PremiumOutlinedActionButton(
                        onClick = onSaveToNotes,
                        borderColor = sudxGoldBorder(),
                        contentColor = sudxNavyFg(),
                        cornerRadius = 14.dp,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("save_tip_note_${tip.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = "حفظ النصيحة في ملاحظاتي",
                            tint = sudxGoldFg(),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "حفظ",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
