package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumData
import com.example.model.ChatMessage
import com.example.model.GroundingSource
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
import com.example.ui.theme.SudXObsidianVariant
import com.example.ui.theme.SudXRoyalNavy
import com.example.ui.theme.SudXSapphireSlate
import com.example.ui.theme.SudXSoftGold
import com.example.ui.theme.sudxCrimsonFg
import com.example.ui.theme.sudxCrimsonSoftBg
import com.example.ui.theme.sudxEmeraldFg
import com.example.ui.theme.sudxEmeraldSoftBg
import com.example.ui.theme.sudxGoldBorder
import com.example.ui.theme.sudxGoldFg
import com.example.ui.theme.sudxGoldTintBg
import com.example.ui.theme.sudxNavyFg
import com.example.ui.theme.sudxNavySoftBg
import com.example.viewmodel.TutorUiState
import com.example.viewmodel.TutorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.ui.platform.LocalContext
import com.example.model.TutorRole

@Composable
fun TutorChatScreen(
    viewModel: TutorViewModel,
    isDarkTheme: Boolean = false,
    onToggleNightMode: () -> Unit = {},
    onOpenNotes: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val tutorState by viewModel.tutorState.collectAsState()
    val isSearchGroundingEnabled by viewModel.isSearchGroundingEnabled.collectAsState()
    val selectedTutorRole by viewModel.selectedTutorRole.collectAsState()
    val pendingAttachment by viewModel.pendingAttachment.collectAsState()
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var inputText by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.attachFileFromUri(context, uri)
        }
    }

    val voiceInputLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenList?.firstOrNull()?.trim().orEmpty()
            if (spokenText.isNotEmpty()) {
                inputText = if (inputText.isBlank()) spokenText else "$inputText $spokenText"
            }
        }
    }

    var selectedSubjectTag by remember { mutableStateOf<String?>(null) }
    var isSearchHistoryVisible by remember { mutableStateOf(false) }
    var isTopOptionsBarVisible by remember { mutableStateOf(false) }
    var historySearchQuery by remember { mutableStateOf("") }
    var noteDraftContent by remember { mutableStateOf<String?>(null) }

    val subjectPrompts = remember {
        listOf(
            "التاريخ" to "يا أستاذ، لخص لي أهم نقاط درس الثورة المهدية التي تتكرر في امتحان التاريخ",
            "الرياضيات" to "يا أستاذ، اشرح لي خطوات إيجاد القيم القصوى المحلية في التفاضل",
            "الفيزياء" to "يا أستاذ، كيف أفرق بين السرعة الخطية والسرعة الزاوية في الحركة الدائرية؟",
            "الكيمياء" to "يا أستاذ، اشرح لي مبدأ لوشاتيليه وأثر الحرارة على ثابت الاتزان",
            "اللغة العربية" to "يا أستاذ، اشرح لي حالات المستثنى بـ (إلا) مع أمثلة إعرابية",
            "اللغة الإنجليزية" to "يا أستاذ، اشرح لي قاعدة المبني للمجهول (Passive Voice) بطريقة مبسطة"
        )
    }

    val isBusy = tutorState is TutorUiState.Loading || tutorState is TutorUiState.Streaming

    val filteredMessages = remember(messages, historySearchQuery) {
        if (historySearchQuery.isBlank()) {
            messages
        } else {
            messages.filter {
                it.text.contains(historySearchQuery, ignoreCase = true) ||
                    (it.attachmentName?.contains(historySearchQuery, ignoreCase = true) == true)
            }
        }
    }

    // Scroll smoothly when filtered messages or active tutor state changes
    LaunchedEffect(filteredMessages.size, tutorState::class) {
        val hasStatusItem = tutorState is TutorUiState.Loading ||
            tutorState is TutorUiState.Streaming ||
            tutorState is TutorUiState.Error
        val itemCount = filteredMessages.size + if (hasStatusItem) 1 else 0
        if (itemCount > 0) {
            listState.animateScrollToItem(itemCount - 1)
        }
    }

    val submitCurrentInput = {
        val trimmed = inputText.trim()
        if ((trimmed.isNotEmpty() || pendingAttachment != null) && !isBusy) {
            val prefix = selectedSubjectTag?.let { "[مادة $it] " } ?: ""
            inputText = ""
            focusManager.clearFocus()
            viewModel.sendMessage("$prefix$trimmed")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .testTag("tutor_chat_screen")
    ) {
        // 1. Luxury SudX Tutor Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SudXMidnightNavy,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(SudXMidnightNavy, SudXRoyalNavy, SudXSapphireSlate)
                        )
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(SudXSoftGold, SudXChampagneGold, SudXBronzeGold)
                                    )
                                )
                                .border(1.2.dp, SudXGoldTint.copy(alpha = 0.75f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "المعلم الذكي",
                                tint = SudXMidnightNavy,
                                modifier = Modifier.size(25.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "المعلم الذكي",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = SudXGoldTint,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(SudXEmerald)
                                )
                            }
                            Text(
                                text = "معلمك الخبير للشهادة السودانية",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE2E8F0),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Toggle Top Options Strip (hidden by default for clean chat space)
                        IconButton(
                            onClick = { isTopOptionsBarVisible = !isTopOptionsBarVisible },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("chat_options_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = if (isTopOptionsBarVisible) "إخفاء شريط الخيارات" else "إظهار شريط الخيارات",
                                tint = if (isTopOptionsBarVisible) SudXChampagneGold else SudXGoldTint
                            )
                        }

                        // 1-tap Night Mode Toggle
                        IconButton(
                            onClick = onToggleNightMode,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("chat_night_mode_toggle")
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (isDarkTheme) "التبديل إلى الوضع النهاري" else "التبديل إلى الوضع الليلي",
                                tint = SudXChampagneGold
                            )
                        }

                        // Open App Settings & Google Account
                        if (onOpenSettings != null) {
                            IconButton(
                                onClick = onOpenSettings,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("chat_open_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "الإعدادات وحساب الطالب",
                                    tint = SudXChampagneGold
                                )
                            }
                        }
                    }

                    // Collapsible Room Database, Quick Actions & Google Search Options Strip (hidden by default)
                    AnimatedVisibility(visible = isTopOptionsBarVisible) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.25f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Storage,
                                            contentDescription = null,
                                            tint = SudXChampagneGold,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "محادثة محفوظة (Room • ${messages.size} رسالة)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SudXGoldTint,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "بحث Google",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SudXGoldTint,
                                            fontSize = 10.sp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Switch(
                                            checked = isSearchGroundingEnabled,
                                            onCheckedChange = { viewModel.toggleSearchGrounding(it) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = SudXMidnightNavy,
                                                checkedTrackColor = SudXChampagneGold,
                                                uncheckedThumbColor = Color(0xFFE2E8F0),
                                                uncheckedTrackColor = Color(0xFF334155)
                                            ),
                                            modifier = Modifier.testTag("google_search_grounding_switch")
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (onOpenNotes != null) {
                                        TextButton(
                                            onClick = onOpenNotes,
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .testTag("chat_open_notes_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EditNote,
                                                contentDescription = null,
                                                tint = SudXChampagneGold,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "ملاحظاتي",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SudXGoldTint
                                            )
                                        }
                                    }

                                    TextButton(
                                        onClick = {
                                            isSearchHistoryVisible = !isSearchHistoryVisible
                                            if (!isSearchHistoryVisible) {
                                                historySearchQuery = ""
                                            }
                                        },
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("search_history_toggle")
                                    ) {
                                        Icon(
                                            imageVector = if (isSearchHistoryVisible) Icons.Default.Close else Icons.Default.Search,
                                            contentDescription = null,
                                            tint = SudXChampagneGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isSearchHistoryVisible) "إغلاق البحث" else "بحث في السجل",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SudXGoldTint
                                        )
                                    }

                                    TextButton(
                                        onClick = { viewModel.clearConversation() },
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("clear_chat_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = SudXChampagneGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "محادثة جديدة",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SudXGoldTint
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Search within Room Database history input bar
                    AnimatedVisibility(visible = isSearchHistoryVisible) {
                        Surface(
                            color = SudXMidnightNavy,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = historySearchQuery,
                                    onValueChange = { historySearchQuery = it },
                                    placeholder = {
                                        Text(
                                            text = "ابحث في الشروحات والدروس المحفوظة...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = SudXChampagneGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        if (historySearchQuery.isNotEmpty()) {
                                            IconButton(onClick = { historySearchQuery = "" }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "مسح البحث",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("history_search_input"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SudXChampagneGold,
                                        unfocusedBorderColor = Color(0xFF2A395E),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = SudXObsidianVariant,
                                        unfocusedContainerColor = SudXObsidianVariant
                                    )
                                )
                            }
                        }
                    }

                    // Champagne gold bottom hairline
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        SudXChampagneGold.copy(alpha = 0.75f),
                                        SudXSoftGold.copy(alpha = 0.9f),
                                        SudXChampagneGold.copy(alpha = 0.75f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }

        // 2. Gemini Tutor Role Selector & Subject Quick Chips (hidden by default)
        AnimatedVisibility(visible = isTopOptionsBarVisible) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chat_top_options_bar")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                // Gemini Multi-Role Chatbot Selector
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(TutorRole.entries) { role ->
                        val isRoleSelected = selectedTutorRole == role
                        val roleIcon = when (role) {
                            TutorRole.GENERAL -> Icons.Default.School
                            TutorRole.DEEP_REASONING -> Icons.Default.Psychology
                            TutorRole.INTERACTIVE_COACH -> Icons.Default.RecordVoiceOver
                            TutorRole.FAST_REVISION -> Icons.Default.Bolt
                        }
                        FilterChip(
                            selected = isRoleSelected,
                            enabled = !isBusy,
                            onClick = { viewModel.selectTutorRole(role) },
                            leadingIcon = {
                                Icon(
                                    imageVector = roleIcon,
                                    contentDescription = role.titleAr,
                                    tint = if (isRoleSelected) SudXChampagneGold else sudxGoldFg(),
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "${role.titleAr} • ${role.shortBadge}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isRoleSelected) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SudXMidnightNavy,
                                selectedLabelColor = SudXGoldTint,
                                containerColor = sudxGoldTintBg(),
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isRoleSelected,
                                borderColor = if (isRoleSelected) SudXChampagneGold else sudxGoldBorder()
                            ),
                            modifier = Modifier.testTag("tutor_role_${role.name.lowercase()}")
                        )
                    }
                }

                // Subject quick pills
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(subjectPrompts) { (subjectName, _) ->
                        val isSelected = selectedSubjectTag == subjectName
                        FilterChip(
                            selected = isSelected,
                            enabled = !isBusy,
                            onClick = {
                                selectedSubjectTag = if (isSelected) null else subjectName
                            },
                            label = {
                                Text(
                                    text = subjectName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SudXRoyalNavy,
                                selectedLabelColor = SudXGoldTint,
                                containerColor = sudxNavySoftBg(),
                                labelColor = sudxNavyFg()
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) SudXChampagneGold else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.testTag("subject_chip_$subjectName")
                        )
                    }
                }

                // Quick question chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(CurriculumData.quickQuestions) { question ->
                        FilterChip(
                            selected = false,
                            enabled = !isBusy,
                            onClick = {
                                if (!isBusy) {
                                    inputText = ""
                                    viewModel.sendMessage(question)
                                }
                            },
                            label = {
                                Text(
                                    text = question,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = sudxGoldFg(),
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = false,
                                borderColor = sudxGoldBorder().copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.testTag("quick_chip_${question.take(10)}")
                        )
                    }
                }
            }
            }
        }

        // 3. Message List (Backed by Room Local Database)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .testTag("message_list"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            if (filteredMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.HistoryEdu,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "لا توجد رسائل مطابقة للبحث في السجل المحلي",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredMessages, key = { it.id }) { message ->
                    ChatMessageItem(
                        message = message,
                        onSaveAsNote = {
                            noteDraftContent = message.text
                        },
                        onDelete = { viewModel.deleteMessage(message.id) }
                    )
                }
            }

            // Real-time streaming or loading bubble
            if (tutorState is TutorUiState.Loading) {
                item(key = "loading_indicator") {
                    LoadingTeacherBubble(isSearchGrounded = isSearchGroundingEnabled)
                }
            } else if (tutorState is TutorUiState.Streaming) {
                val streamingState = tutorState as TutorUiState.Streaming
                item(key = "streaming_indicator") {
                    StreamingTeacherBubble(
                        partialText = streamingState.partialResponse,
                        groundingSources = streamingState.groundingSources,
                        searchQueries = streamingState.searchQueries
                    )
                }
            } else if (tutorState is TutorUiState.Error) {
                val err = (tutorState as TutorUiState.Error).errorMessage
                item(key = "error_indicator") {
                    ErrorTeacherBubble(
                        errorMessage = err,
                        onRetry = { viewModel.retryLastMessage() }
                    )
                }
            }
        }

        // 4. Clean Text Input Field & Send Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                AnimatedVisibility(
                    visible = selectedSubjectTag != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = sudxGoldTintBg(),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, SudXChampagneGold.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "المادة المحددة: ${selectedSubjectTag.orEmpty()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "إلغاء تحديد المادة",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { selectedSubjectTag = null }
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = pendingAttachment != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val att = pendingAttachment
                    if (att != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = sudxEmeraldSoftBg(),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, SudXEmerald.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = if (att.mimeType.startsWith("image/")) {
                                                Icons.Default.Image
                                            } else {
                                                Icons.Default.Description
                                            },
                                            contentDescription = null,
                                            tint = sudxEmeraldFg(),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "ملف مرفق جاهز للتحليل:",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = sudxEmeraldFg(),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = att.fileName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.clearPendingAttachment() },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .testTag("remove_attachment_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "إزالة الملف المرفق",
                                            tint = sudxEmeraldFg(),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attach File / PDF / Image Button
                    IconButton(
                        onClick = {
                            filePickerLauncher.launch(
                                arrayOf(
                                    "application/pdf",
                                    "image/*",
                                    "text/plain"
                                )
                            )
                        },
                        enabled = !isBusy,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(sudxNavySoftBg())
                            .border(1.dp, sudxGoldBorder().copy(alpha = 0.6f), CircleShape)
                            .testTag("attach_file_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "إرفاق ملف PDF أو صورة مسألة",
                            tint = sudxNavyFg()
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Voice Input (Speech-to-Text) Button
                    IconButton(
                        onClick = {
                            val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SD")
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث بسؤالك للمعلم الذكي...")
                            }
                            try {
                                voiceInputLauncher.launch(speechIntent)
                            } catch (_: ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    "ميزة الإدخال الصوتي غير مدعومة على هذا الجهاز حالياً.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        enabled = !isBusy,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(sudxGoldTintBg())
                            .border(1.dp, sudxGoldBorder(), CircleShape)
                            .testTag("voice_input_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "طرح سؤال بالصوت",
                            tint = sudxGoldFg()
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = if (pendingAttachment != null) {
                                    "أضف سؤالك حول الملف أو اضغط إرسال..."
                                } else if (selectedSubjectTag != null) {
                                    "اكتب سؤالك في مادة $selectedSubjectTag..."
                                } else {
                                    "اكتب سؤالك أو أرفق ملفاً للمعلم..."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (inputText.isNotEmpty()) {
                                IconButton(
                                    onClick = { inputText = "" },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "مسح النص",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { submitCurrentInput() }),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SudXChampagneGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val canSend = (inputText.isNotBlank() || pendingAttachment != null) && !isBusy

                    IconButton(
                        onClick = submitCurrentInput,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (canSend) SudXRoyalNavy else MaterialTheme.colorScheme.outline
                            )
                            .border(
                                width = 1.dp,
                                color = if (canSend) SudXChampagneGold.copy(alpha = 0.7f) else Color.Transparent,
                                shape = CircleShape
                            )
                            .testTag("send_question_button"),
                        enabled = canSend
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "إرسال السؤال للمعلم الذكي",
                            tint = if (canSend) SudXChampagneGold else Color.White
                        )
                    }
                }
            }
        }
    }

    val draftText = noteDraftContent
    if (draftText != null) {
        AddEditStudyNoteDialog(
            initialSubject = selectedSubjectTag,
            initialTitle = draftText.lineSequence().firstOrNull { it.isNotBlank() }?.take(48).orEmpty(),
            initialContent = draftText,
            onDismiss = { noteDraftContent = null },
            onSave = { title, content, subjectTitle, isPinned ->
                viewModel.saveStudyNote(
                    title = title,
                    content = content,
                    subjectTitle = subjectTitle,
                    isPinned = isPinned
                )
                noteDraftContent = null
                Toast.makeText(context, "تم حفظ الملاحظة في قاعدة البيانات المحلية (Room)", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onSaveAsNote: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val isUser = message.isUser
    val clipboardManager = LocalClipboardManager.current
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale("ar")) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SudXMidnightNavy)
                    .border(1.2.dp, SudXChampagneGold.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "المعلم الذكي",
                    tint = SudXChampagneGold,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = SudXMidnightNavy.copy(alpha = 0.1f),
                    spotColor = SudXMidnightNavy.copy(alpha = 0.16f)
                )
                .testTag(if (isUser) "user_message_bubble" else "tutor_message_bubble"),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) SudXRoyalNavy else MaterialTheme.colorScheme.surface
            ),
            border = if (isUser) {
                BorderStroke(1.dp, SudXChampagneGold.copy(alpha = 0.48f))
            } else {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            },
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = message.tutorRoleName ?: "المعلم الذكي",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SudXChampagneGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (message.isSearchGrounded || message.groundingSources.isNotEmpty() || message.searchQueries.isNotEmpty()) {
                                Surface(
                                    color = sudxEmeraldSoftBg(),
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, SudXEmerald.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = sudxEmeraldFg(),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "بحث Google",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = sudxEmeraldFg(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            if (onSaveAsNote != null) {
                                IconButton(
                                    onClick = onSaveAsNote,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .testTag("save_message_note_${message.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkAdd,
                                        contentDescription = "حفظ كملاحظة تعليمية",
                                        tint = sudxGoldFg(),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(message.text))
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "نسخ الشرح",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            if (onDelete != null) {
                                IconButton(
                                    onClick = onDelete,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "حذف من السجل",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (!message.attachmentName.isNullOrBlank()) {
                    Surface(
                        color = if (isUser) Color.Black.copy(alpha = 0.25f) else sudxNavySoftBg(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (message.attachmentMimeType?.startsWith("image/") == true) {
                                    Icons.Default.Image
                                } else {
                                    Icons.Default.Description
                                },
                                contentDescription = null,
                                tint = if (isUser) SudXChampagneGold else sudxNavyFg(),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ملف مرفق: ${message.attachmentName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isUser) SudXGoldTint else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isUser) SudXGoldTint else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 25.sp
                )

                if (!isUser && (message.groundingSources.isNotEmpty() || message.searchQueries.isNotEmpty())) {
                    GroundingMetadataSection(
                        sources = message.groundingSources,
                        searchQueries = message.searchQueries
                    )
                }

                // Timestamp and stored badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = if (isUser) Arrangement.Start else Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isUser) SudXSoftGold.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SudXGoldTint)
                    .border(1.dp, SudXGoldBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "الطالب",
                    tint = SudXRoyalNavy,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun GroundingMetadataSection(
    sources: List<GroundingSource>,
    searchQueries: List<String>
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        if (searchQueries.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "عمليات بحث Google:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(searchQueries) { query ->
                    Surface(
                        color = sudxNavySoftBg(),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, sudxGoldBorder().copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "🔍 $query",
                            style = MaterialTheme.typography.labelSmall,
                            color = sudxNavyFg(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (sources.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = sudxEmeraldFg(),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "المصادر الموثقة من بحث Google:",
                    style = MaterialTheme.typography.labelSmall,
                    color = sudxEmeraldFg(),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                sources.take(4).forEach { source ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                runCatching { uriHandler.openUri(source.uri) }
                            }
                            .testTag("grounding_source_item")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = source.title,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "فتح المصدر",
                                tint = sudxGoldFg(),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingTeacherBubble(isSearchGrounded: Boolean = true) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(SudXMidnightNavy)
                .border(1.dp, SudXChampagneGold.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "المعلم الذكي",
                tint = SudXChampagneGold,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = SudXChampagneGold,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isSearchGrounded) {
                        "المعلم الذكي يبحث في Google ويجهز الشرح الموثق..."
                    } else {
                        "المعلم الذكي يفكر ويجهز التوجيه التعليمي..."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StreamingTeacherBubble(
    partialText: String,
    groundingSources: List<GroundingSource> = emptyList(),
    searchQueries: List<String> = emptyList()
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(SudXMidnightNavy)
                .border(1.dp, SudXChampagneGold.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "المعلم الذكي",
                tint = SudXChampagneGold,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SudXChampagneGold.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "المعلم الذكي يكتب الآن...",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = partialText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                )
                if (groundingSources.isNotEmpty() || searchQueries.isNotEmpty()) {
                    GroundingMetadataSection(
                        sources = groundingSources,
                        searchQueries = searchQueries
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorTeacherBubble(
    errorMessage: String,
    onRetry: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = sudxCrimsonSoftBg()),
        border = BorderStroke(1.dp, sudxCrimsonFg().copy(alpha = 0.35f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "تنبيه المعلم:",
                style = MaterialTheme.typography.titleSmall,
                color = sudxCrimsonFg(),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onRetry,
                    modifier = Modifier.testTag("retry_tutor_button")
                ) {
                    Text(
                        text = "إعادة المحاولة",
                        style = MaterialTheme.typography.labelLarge,
                        color = sudxCrimsonFg(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
