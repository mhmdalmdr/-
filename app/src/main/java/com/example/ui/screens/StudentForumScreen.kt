package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.CurriculumData
import com.example.data.remote.ForumRepository
import com.example.model.ForumMutationState
import com.example.model.ForumPost
import com.example.model.ForumPostStatus
import com.example.model.ForumReply
import com.example.model.ForumUiState
import com.example.model.TeacherPostReviewStatus
import com.example.model.TeacherReplyBadge
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
import com.example.ui.theme.sudxGoldBorder
import com.example.ui.theme.sudxGoldFg
import com.example.ui.theme.sudxGoldTintBg
import com.example.ui.theme.sudxNavyFg
import com.example.ui.theme.sudxNavySoftBg
import com.example.util.GoogleAuthHelper
import com.example.util.StudentAccount
import com.example.util.toStudentAccount
import com.example.viewmodel.ForumViewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun StudentForumRoute(
    academicStreamTitle: String,
    isSigningIn: Boolean,
    authError: String?,
    onGoogleSignIn: () -> Unit,
    onAskSmartTeacher: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val initialAccount = remember(context) {
        GoogleAuthHelper.safeAuthOrNull(context)?.currentUser?.toStudentAccount()
    }
    val authAccount by GoogleAuthHelper.observeAuthState(context)
        .collectAsStateWithLifecycle(initialValue = initialAccount)

    if (authAccount == null) {
        BackHandler { onBack() }
        ForumAuthGateScreen(
            isSigningIn = isSigningIn,
            authError = authError,
            onGoogleSignIn = onGoogleSignIn,
            onBack = onBack,
            modifier = modifier
        )
    } else {
        val account = authAccount!!
        key(account.uid) {
            val databaseId = remember(context) {
                context.getString(R.string.firestore_database_id)
            }
            val forumViewModel: ForumViewModel = viewModel(
                key = "forum_vm_${account.uid}",
                factory = viewModelFactory {
                    initializer {
                        val db = FirebaseFirestore.getInstance(databaseId)
                        val auth = GoogleAuthHelper.safeAuthOrNull(context) ?: Firebase.auth
                        ForumViewModel(
                            repository = ForumRepository(db = db, auth = auth),
                            currentUserId = account.uid,
                            currentUserName = account.displayName,
                            currentUserTrack = academicStreamTitle
                        )
                    }
                }
            )
            StudentForumScreen(
                viewModel = forumViewModel,
                onAskSmartTeacher = onAskSmartTeacher,
                onBack = onBack,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ForumAuthGateScreen(
    isSigningIn: Boolean,
    authError: String?,
    onGoogleSignIn: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("forum_auth_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "العودة للرئيسية"
                )
            }
            Text(
                text = "المنتدى الطلابي",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 540.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.2.dp, SudXChampagneGold.copy(alpha = 0.7f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(SudXMidnightNavy, SudXRoyalNavy))
                        )
                        .border(1.5.dp, SudXChampagneGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        tint = SudXChampagneGold,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "انضم إلى المنتدى الطلابي",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "اطرح استفساراتك في مواد الشهادة السودانية، وساعد زملاءك في الحل تحت الإشراف المباشر من المعلم الذكي لضمان دقة المعلومات.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SudXGoldTint,
                    border = BorderStroke(1.dp, SudXGoldBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SudXBronzeGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مزايا المنتدى بإشراف المعلم الذكي:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = SudXMidnightNavy
                            )
                        }
                        Text(
                            text = "• توجيه سقراطي فوري عند طرح كل سؤال جديد",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "• تدقيق ردود الطلاب ومنح شارة «إجابة موثّقة»",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "• مزامنة فورية وآمنة مرتبطة بحسابك الجامعي/الطلابي",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF334155)
                        )
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
                        Text(
                            text = authError,
                            style = MaterialTheme.typography.bodySmall,
                            color = SudXCrimson,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onGoogleSignIn,
                    enabled = !isSigningIn,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SudXRoyalNavy,
                        contentColor = SudXGoldTint
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("forum_google_sign_in_button")
                ) {
                    if (isSigningIn) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = SudXChampagneGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("جاري تسجيل الدخول...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = SudXChampagneGold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تسجيل الدخول بحساب Google للمشاركة", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StudentForumScreen(
    viewModel: ForumViewModel,
    onAskSmartTeacher: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val postsState by viewModel.filteredPostsState.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubjectFilter.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedPost by viewModel.selectedPost.collectAsStateWithLifecycle()
    val repliesState by viewModel.repliesState.collectAsStateWithLifecycle()
    val postMutationState by viewModel.postMutationState.collectAsStateWithLifecycle()
    val replyMutationState by viewModel.replyMutationState.collectAsStateWithLifecycle()
    val aiSupervisingPostIds by viewModel.aiSupervisingPostIds.collectAsStateWithLifecycle()
    val aiSupervisingReplyIds by viewModel.aiSupervisingReplyIds.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()
    val errorBannerMessage by viewModel.errorBannerMessage.collectAsStateWithLifecycle()

    var showNewPostDialog by rememberSaveable { mutableStateOf(false) }

    BackHandler {
        when {
            showNewPostDialog -> showNewPostDialog = false
            selectedPost != null -> viewModel.closePostThread()
            else -> onBack()
        }
    }

    if (showNewPostDialog) {
        NewForumPostDialog(
            mutationState = postMutationState,
            onDismiss = {
                viewModel.resetPostMutationError()
                showNewPostDialog = false
            },
            onPublish = { subject, title, body ->
                viewModel.submitNewPost(
                    subjectTitle = subject,
                    title = title,
                    body = body,
                    onCreated = { newId ->
                        showNewPostDialog = false
                        viewModel.openPostThread(newId)
                    }
                )
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (selectedPost != null) {
            ForumThreadDetailView(
                post = selectedPost!!,
                currentUserId = viewModel.currentUserId,
                repliesState = repliesState,
                replyMutationState = replyMutationState,
                isAiSupervisingPost = aiSupervisingPostIds.contains(selectedPost!!.id),
                aiSupervisingReplyIds = aiSupervisingReplyIds,
                bannerMessage = bannerMessage,
                errorBannerMessage = errorBannerMessage,
                onDismissBanners = { viewModel.clearBanners() },
                onBack = { viewModel.closePostThread() },
                onRefreshPostAi = { viewModel.requestPostAiReview(selectedPost!!) },
                onMarkSolved = { viewModel.markPostAsSolved(selectedPost!!) },
                onUpvotePost = { viewModel.upvotePostHelpful(selectedPost!!) },
                onDeletePost = { viewModel.deleteOwnPost(selectedPost!!) },
                onSubmitReply = { body, clearInput ->
                    viewModel.submitReply(selectedPost!!, body, clearInput)
                },
                onToggleReplyHelpful = { reply ->
                    viewModel.toggleReplyHelpfulByAuthor(selectedPost!!, reply)
                },
                onRetryReplyAi = { reply ->
                    viewModel.requestReplyAiCheck(selectedPost!!, reply)
                },
                onDeleteReply = { reply ->
                    viewModel.deleteOwnReply(selectedPost!!, reply)
                },
                onAskSmartTeacher = {
                    val prompt = buildString {
                        append("يا أستاذ، أحتاج شرحاً إضافياً حول هذا الاستفسار في المنتدى (${selectedPost!!.subjectTitle}):\n")
                        append("السؤال: ${selectedPost!!.title}\n")
                        append("التفاصيل: ${selectedPost!!.body}")
                    }
                    onAskSmartTeacher(prompt)
                }
            )
        } else {
            ForumFeedView(
                postsState = postsState,
                selectedSubject = selectedSubject,
                selectedStatus = selectedStatus,
                searchQuery = searchQuery,
                aiSupervisingPostIds = aiSupervisingPostIds,
                bannerMessage = bannerMessage,
                errorBannerMessage = errorBannerMessage,
                onDismissBanners = { viewModel.clearBanners() },
                onSelectSubject = viewModel::selectSubjectFilter,
                onSelectStatus = viewModel::selectStatusFilter,
                onSearchChange = viewModel::updateSearchQuery,
                onOpenNewPost = {
                    viewModel.resetPostMutationError()
                    showNewPostDialog = true
                },
                onOpenPost = { post -> viewModel.openPostThread(post.id) },
                onBack = onBack
            )
        }
    }
}

@Composable
private fun ForumFeedView(
    postsState: ForumUiState<List<ForumPost>>,
    selectedSubject: String,
    selectedStatus: ForumPostStatus?,
    searchQuery: String,
    aiSupervisingPostIds: Set<String>,
    bannerMessage: String?,
    errorBannerMessage: String?,
    onDismissBanners: () -> Unit,
    onSelectSubject: (String) -> Unit,
    onSelectStatus: (ForumPostStatus?) -> Unit,
    onSearchChange: (String) -> Unit,
    onOpenNewPost: () -> Unit,
    onOpenPost: (ForumPost) -> Unit,
    onBack: () -> Unit
) {
    val subjectOptions = remember {
        listOf("الكل") + CurriculumData.subjects.map { it.title } + listOf("عام")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 84.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Luxury Header Banner
        item {
            Surface(
                color = SudXMidnightNavy,
                shape = RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp),
                border = BorderStroke(1.dp, SudXChampagneGold.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .minimumInteractiveComponentSize()
                                    .testTag("forum_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "رجوع",
                                    tint = SudXGoldTint
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "المنتدى الطلابي",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SudXGoldTint
                                )
                                Text(
                                    text = "تعاون الطلاب بإشراف المعلم الذكي",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SudXSoftGold
                                )
                            }
                        }

                        Button(
                            onClick = onOpenNewPost,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SudXChampagneGold,
                                contentColor = SudXMidnightNavy
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("forum_new_post_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "استفسار جديد",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                "ابحث عن سؤال أو قانون أو مادة...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = SudXChampagneGold
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "مسح البحث",
                                        tint = SudXGoldTint
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SudXRoyalNavy,
                            unfocusedContainerColor = SudXRoyalNavy.copy(alpha = 0.85f),
                            focusedTextColor = SudXGoldTint,
                            unfocusedTextColor = SudXGoldTint,
                            focusedBorderColor = SudXChampagneGold,
                            unfocusedBorderColor = SudXChampagneGold.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("forum_search_input")
                    )
                }
            }
        }

        // Info / Error Banner
        if (!bannerMessage.isNullOrBlank() || !errorBannerMessage.isNullOrBlank()) {
            item {
                ForumNotificationBanner(
                    infoMessage = bannerMessage,
                    errorMessage = errorBannerMessage,
                    onDismiss = onDismissBanners,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Status & Subject Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { onSelectStatus(null) },
                        label = { Text("جميع الحالات") },
                        modifier = Modifier.testTag("forum_status_all")
                    )
                    FilterChip(
                        selected = selectedStatus == ForumPostStatus.OPEN,
                        onClick = { onSelectStatus(ForumPostStatus.OPEN) },
                        label = { Text("مفتوح للنقاش") },
                        leadingIcon = {
                            Icon(Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier.testTag("forum_status_open")
                    )
                    FilterChip(
                        selected = selectedStatus == ForumPostStatus.SOLVED,
                        onClick = { onSelectStatus(ForumPostStatus.SOLVED) },
                        label = { Text("تم الحل") },
                        leadingIcon = {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SudXEmeraldSoft,
                            selectedLabelColor = SudXEmeraldDark
                        ),
                        modifier = Modifier.testTag("forum_status_solved")
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    subjectOptions.forEach { subject ->
                        FilterChip(
                            selected = selectedSubject == subject,
                            onClick = { onSelectSubject(subject) },
                            label = { Text(subject) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SudXRoyalNavy,
                                selectedLabelColor = SudXGoldTint
                            ),
                            modifier = Modifier.testTag("forum_subject_chip_$subject")
                        )
                    }
                }
            }
        }

        // Feed Body States
        when (postsState) {
            is ForumUiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = SudXChampagneGold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "جاري تحميل نقاشات الطلاب...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            is ForumUiState.Error -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = sudxCrimsonSoftBg()),
                        border = BorderStroke(1.dp, sudxCrimsonFg().copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = sudxCrimsonFg()
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = postsState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = sudxCrimsonFg()
                            )
                        }
                    }
                }
            }

            is ForumUiState.Success -> {
                val posts = postsState.data
                if (posts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, SudXGoldBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = SudXBronzeGold,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "لا توجد استفسارات مطابقة حالياً",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ابدأ بطرح أول مسألة أو سؤال دراسي ليشاركك زملاؤك الحل تحت إشراف المعلم الذكي.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onOpenNewPost,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SudXRoyalNavy,
                                        contentColor = SudXGoldTint
                                    ),
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("forum_empty_new_post_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("اطرح استفسارك الآن", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(posts, key = { it.id }) { post ->
                        ForumPostCard(
                            post = post,
                            isAiSupervising = aiSupervisingPostIds.contains(post.id),
                            onClick = { onOpenPost(post) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ForumPostCard(
    post: ForumPost,
    isAiSupervising: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSolved = post.parsedStatus == ForumPostStatus.SOLVED
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("forum_post_card_${post.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (isSolved) SudXEmerald.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = sudxNavySoftBg(),
                        border = BorderStroke(0.8.dp, sudxNavyFg().copy(alpha = 0.22f))
                    ) {
                        Text(
                            text = post.subjectTitle,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = sudxNavyFg(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSolved) sudxEmeraldSoftBg() else sudxGoldTintBg(),
                        border = BorderStroke(
                            0.8.dp,
                            if (isSolved) SudXEmerald.copy(alpha = 0.45f) else sudxGoldBorder()
                        )
                    ) {
                        Text(
                            text = post.parsedStatus.labelAr,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSolved) sudxEmeraldFg() else sudxGoldFg(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = formatTimestampAr(post.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = post.body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 19.sp
            )

            // AI Teacher Supervision Summary Strip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = sudxGoldTintBg(),
                border = BorderStroke(0.8.dp, sudxGoldBorder()),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isAiSupervising) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = sudxGoldFg(),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = sudxGoldFg(),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            isAiSupervising -> "المعلم الذكي يراجع السؤال الآن..."
                            post.teacherReviewNote.isNotBlank() ->
                                "${post.parsedTeacherReviewStatus.labelAr}: ${post.teacherReviewNote}"
                            else -> post.parsedTeacherReviewStatus.labelAr
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${post.authorName} • ${post.authorTrack}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = "عدد الردود",
                            tint = sudxNavyFg(),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.repliesCount} ردود",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "مفيد",
                            tint = SudXBronzeGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.helpfulCount}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ForumThreadDetailView(
    post: ForumPost,
    currentUserId: String,
    repliesState: ForumUiState<List<ForumReply>>,
    replyMutationState: ForumMutationState,
    isAiSupervisingPost: Boolean,
    aiSupervisingReplyIds: Set<String>,
    bannerMessage: String?,
    errorBannerMessage: String?,
    onDismissBanners: () -> Unit,
    onBack: () -> Unit,
    onRefreshPostAi: () -> Unit,
    onMarkSolved: () -> Unit,
    onUpvotePost: () -> Unit,
    onDeletePost: () -> Unit,
    onSubmitReply: (String, () -> Unit) -> Unit,
    onToggleReplyHelpful: (ForumReply) -> Unit,
    onRetryReplyAi: (ForumReply) -> Unit,
    onDeleteReply: (ForumReply) -> Unit,
    onAskSmartTeacher: () -> Unit
) {
    val isOwner = post.userId == currentUserId
    val isSolved = post.parsedStatus == ForumPostStatus.SOLVED
    var replyDraft by rememberSaveable(post.id) { mutableStateOf("") }
    var showDeletePostDialog by remember { mutableStateOf(false) }

    if (showDeletePostDialog) {
        AlertDialog(
            onDismissRequest = { showDeletePostDialog = false },
            title = { Text("حذف الاستفسار", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من حذف هذا الاستفسار من المنتدى الطلابي؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeletePostDialog = false
                        onDeletePost()
                    }
                ) {
                    Text("حذف", color = SudXCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePostDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Thread Top Bar
        Surface(
            color = SudXMidnightNavy,
            border = BorderStroke(1.dp, SudXChampagneGold.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("forum_thread_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "العودة لقائمة الاستفسارات",
                            tint = SudXGoldTint
                        )
                    }
                    Column {
                        Text(
                            text = post.subjectTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SudXGoldTint
                        )
                        Text(
                            text = post.parsedStatus.labelAr,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSolved) Color(0xFF4ADE80) else SudXSoftGold
                        )
                    }
                }

                if (isOwner) {
                    IconButton(
                        onClick = { showDeletePostDialog = true },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("forum_delete_post_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف الاستفسار",
                            tint = Color(0xFFFCA5A5)
                        )
                    }
                }
            }
        }

        // Scrollable Thread Content + Replies
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!bannerMessage.isNullOrBlank() || !errorBannerMessage.isNullOrBlank()) {
                item {
                    ForumNotificationBanner(
                        infoMessage = bannerMessage,
                        errorMessage = errorBannerMessage,
                        onDismiss = onDismissBanners
                    )
                }
            }

            // Original Question Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.2.dp, SudXChampagneGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${post.authorName} • ${post.authorTrack}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = SudXBronzeGold
                            )
                            Text(
                                text = formatTimestampAr(post.createdAt),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = post.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 23.sp
                        )

                        // Action Buttons Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onUpvotePost,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("forum_upvote_post_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = null,
                                    tint = SudXBronzeGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("سؤال مفيد (${post.helpfulCount})")
                            }

                            OutlinedButton(
                                onClick = onAskSmartTeacher,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("forum_ask_smart_teacher_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = SudXRoyalNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ناقش مع المعلم الذكي")
                            }

                            if (isOwner && !isSolved) {
                                Button(
                                    onClick = onMarkSolved,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SudXEmeraldDark,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("forum_mark_solved_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تم حل الاستفسار")
                                }
                            }
                        }
                    }
                }
            }

            // AI Teacher Supervision Card for the Post
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SudXRoyalNavy),
                    border = BorderStroke(1.2.dp, SudXChampagneGold.copy(alpha = 0.75f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forum_ai_supervision_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SudXChampagneGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "إشراف المعلم الذكي",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SudXGoldTint
                                )
                            }

                            if (isOwner && !isSolved && !isAiSupervisingPost) {
                                TextButton(
                                    onClick = onRefreshPostAi,
                                    modifier = Modifier.testTag("forum_refresh_post_ai_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = SudXChampagneGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "تحديث التوجيه",
                                        color = SudXChampagneGold,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }

                        if (isAiSupervisingPost) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = SudXChampagneGold,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "يحلل المعلم الذكي السؤال لصياغة توجيه سقراطي مساعد...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SudXSoftGold
                                )
                            }
                        } else if (post.teacherReviewNote.isNotBlank()) {
                            Text(
                                text = post.teacherReviewNote,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFF8FAFC),
                                lineHeight = 22.sp
                            )
                        } else {
                            Text(
                                text = "في انتظار إكمال المراجعة الإشرافية من المعلم الذكي.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SudXSoftGold
                            )
                        }
                    }
                }
            }

            // Section Header: Peer Replies
            item {
                Text(
                    text = "مساعدات الزملاء وتدقيق المعلم (${post.repliesCount})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            when (repliesState) {
                is ForumUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SudXChampagneGold)
                        }
                    }
                }

                is ForumUiState.Error -> {
                    item {
                        Text(
                            text = repliesState.message,
                            color = SudXCrimson,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                is ForumUiState.Success -> {
                    val replies = repliesState.data
                    if (replies.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "لا توجد ردود من الزملاء بعد. شارك بفهمك أو تلميحك لمساعدة زميلك في الأسفل!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(replies, key = { it.id }) { reply ->
                            ForumReplyCard(
                                reply = reply,
                                isPostOwner = isOwner,
                                isReplyOwner = reply.userId == currentUserId,
                                isAiChecking = aiSupervisingReplyIds.contains(reply.id),
                                onToggleHelpful = { onToggleReplyHelpful(reply) },
                                onRetryAiCheck = { onRetryReplyAi(reply) },
                                onDelete = { onDeleteReply(reply) }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Peer Reply Composer
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                if (replyMutationState is ForumMutationState.Error) {
                    Text(
                        text = replyMutationState.message,
                        color = SudXCrimson,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = replyDraft,
                        onValueChange = { replyDraft = it },
                        placeholder = { Text("اكتب شرحك أو مساعدتك لزميلك...") },
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("forum_reply_input")
                    )

                    val canSubmit = replyDraft.trim().length >= ForumRepository.MIN_REPLY_BODY_LEN &&
                        replyMutationState !is ForumMutationState.Submitting

                    Button(
                        onClick = {
                            onSubmitReply(replyDraft) {
                                replyDraft = ""
                            }
                        },
                        enabled = canSubmit,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SudXRoyalNavy,
                            contentColor = SudXChampagneGold
                        ),
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("forum_submit_reply_button")
                    ) {
                        if (replyMutationState is ForumMutationState.Submitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = SudXChampagneGold,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "إرسال الرد"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ForumReplyCard(
    reply: ForumReply,
    isPostOwner: Boolean,
    isReplyOwner: Boolean,
    isAiChecking: Boolean,
    onToggleHelpful: () -> Unit,
    onRetryAiCheck: () -> Unit,
    onDelete: () -> Unit
) {
    val badge = reply.parsedTeacherBadge
    val badgeBg = when (badge) {
        TeacherReplyBadge.VERIFIED -> sudxEmeraldSoftBg()
        TeacherReplyBadge.CONTAINS_ERROR -> sudxCrimsonSoftBg()
        TeacherReplyBadge.NEEDS_HINT -> sudxGoldTintBg()
        TeacherReplyBadge.NONE -> MaterialTheme.colorScheme.surfaceVariant
    }
    val badgeFg = when (badge) {
        TeacherReplyBadge.VERIFIED -> sudxEmeraldFg()
        TeacherReplyBadge.CONTAINS_ERROR -> sudxCrimsonFg()
        TeacherReplyBadge.NEEDS_HINT -> sudxGoldFg()
        TeacherReplyBadge.NONE -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val badgeIcon = when (badge) {
        TeacherReplyBadge.VERIFIED -> Icons.Default.Verified
        TeacherReplyBadge.CONTAINS_ERROR -> Icons.Default.WarningAmber
        TeacherReplyBadge.NEEDS_HINT -> Icons.Default.Psychology
        TeacherReplyBadge.NONE -> Icons.Default.SmartToy
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (reply.markedHelpfulByAuthor) SudXChampagneGold else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forum_reply_card_${reply.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = reply.authorName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (reply.markedHelpfulByAuthor) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = sudxGoldTintBg(),
                            border = BorderStroke(0.8.dp, SudXChampagneGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = sudxGoldFg(),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "رد مفيد",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = sudxGoldFg(),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Text(
                    text = formatTimestampAr(reply.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = reply.body,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 21.sp
            )

            // AI Teacher Reply Verification Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = badgeBg,
                border = BorderStroke(0.8.dp, badgeFg.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isAiChecking) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(15.dp),
                                    color = badgeFg,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = badgeIcon,
                                    contentDescription = null,
                                    tint = badgeFg,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAiChecking) "جاري تدقيق الرد بالمعلم الذكي..." else badge.labelAr,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = badgeFg
                            )
                        }

                        if (isReplyOwner && badge == TeacherReplyBadge.NONE && !isAiChecking) {
                            TextButton(
                                onClick = onRetryAiCheck,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("تدقيق الآن", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    if (reply.teacherFeedback.isNotBlank()) {
                        Text(
                            text = reply.teacherFeedback,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (isPostOwner || isReplyOwner) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isPostOwner) {
                        TextButton(
                            onClick = onToggleHelpful,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("forum_mark_reply_helpful_${reply.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (reply.markedHelpfulByAuthor) SudXBronzeGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (reply.markedHelpfulByAuthor) "إلغاء علامة الإفادة" else "تمييز كرد مفيد",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    if (isReplyOwner) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("forum_delete_reply_${reply.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف الرد",
                                tint = SudXCrimson,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewForumPostDialog(
    mutationState: ForumMutationState,
    onDismiss: () -> Unit,
    onPublish: (subjectTitle: String, title: String, body: String) -> Unit
) {
    val subjects = remember { CurriculumData.subjects.map { it.title } + "عام" }
    var selectedSubject by rememberSaveable { mutableStateOf(subjects.first()) }
    var title by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }

    val cleanTitle = title.trim()
    val cleanBody = body.trim()
    val isValid = cleanTitle.length in ForumRepository.MIN_TITLE_LEN..ForumRepository.MAX_TITLE_LEN &&
        cleanBody.length in ForumRepository.MIN_POST_BODY_LEN..ForumRepository.MAX_POST_BODY_LEN &&
        mutationState !is ForumMutationState.Submitting

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = SudXBronzeGold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("طرح استفسار في المنتدى", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "اختر المادة:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    subjects.forEach { subj ->
                        FilterChip(
                            selected = selectedSubject == subj,
                            onClick = { selectedSubject = subj },
                            label = { Text(subj) }
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= ForumRepository.MAX_TITLE_LEN) title = it },
                    label = { Text("عنوان السؤال أو المسألة") },
                    placeholder = { Text("مثال: كيف نطبق قانون فاراداي في هذا المثال؟") },
                    singleLine = true,
                    supportingText = {
                        Text("${cleanTitle.length}/${ForumRepository.MAX_TITLE_LEN} (الحد الأدنى ${ForumRepository.MIN_TITLE_LEN} أحرف)")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forum_post_title_input")
                )

                OutlinedTextField(
                    value = body,
                    onValueChange = { if (it.length <= ForumRepository.MAX_POST_BODY_LEN) body = it },
                    label = { Text("تفاصيل الاستفسار وما توصلت إليه") },
                    placeholder = { Text("اشرح النقطة غير الواضحة ليتمكن المعلم الذكي وزملاؤك من توجيهك بدقة...") },
                    minLines = 3,
                    maxLines = 6,
                    supportingText = {
                        Text("${cleanBody.length}/${ForumRepository.MAX_POST_BODY_LEN} (الحد الأدنى ${ForumRepository.MIN_POST_BODY_LEN} أحرف)")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forum_post_body_input")
                )

                if (mutationState is ForumMutationState.Error) {
                    Text(
                        text = mutationState.message,
                        color = SudXCrimson,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPublish(selectedSubject, title, body) },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SudXRoyalNavy,
                    contentColor = SudXGoldTint
                ),
                modifier = Modifier.testTag("forum_publish_post_button")
            ) {
                if (mutationState is ForumMutationState.Submitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = SudXChampagneGold,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("جاري النشر...")
                } else {
                    Text("نشر الاستفسار", fontWeight = FontWeight.Bold)
                }
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
private fun ForumNotificationBanner(
    infoMessage: String?,
    errorMessage: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isError = !errorMessage.isNullOrBlank()
    val text = errorMessage ?: infoMessage ?: return
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isError) sudxCrimsonSoftBg() else sudxEmeraldSoftBg(),
        border = BorderStroke(
            1.dp,
            if (isError) sudxCrimsonFg().copy(alpha = 0.45f) else SudXEmerald.copy(alpha = 0.45f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isError) Icons.Default.ErrorOutline else Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = if (isError) sudxCrimsonFg() else sudxEmeraldFg(),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) sudxCrimsonFg() else sudxEmeraldFg(),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق التنبيه",
                    tint = if (isError) sudxCrimsonFg() else sudxEmeraldFg(),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun formatTimestampAr(timestamp: Timestamp?): String {
    if (timestamp == null) return "الآن"
    val diffMs = System.currentTimeMillis() - timestamp.toDate().time
    val diffMins = diffMs / 60_000L
    return when {
        diffMins < 1L -> "الآن"
        diffMins < 60L -> "منذ $diffMins دقيقة"
        diffMins < 24 * 60L -> "منذ ${diffMins / 60L} ساعة"
        else -> SimpleDateFormat("d MMM", Locale("ar")).format(timestamp.toDate())
    }
}
