package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.LruCache
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.CurriculumData
import com.example.model.Textbook
import com.example.ui.components.PremiumActionButton
import com.example.ui.components.PremiumOutlinedActionButton
import com.example.ui.components.SudXHeaderSurface
import com.example.ui.theme.SudXChampagneGold
import com.example.ui.theme.SudXCrimson
import com.example.ui.theme.SudXGoldTint
import com.example.ui.theme.SudXMidnightNavy
import com.example.ui.theme.SudXRoyalNavy
import com.example.ui.theme.sudxCrimsonFg
import com.example.ui.theme.sudxGoldBorder
import com.example.ui.theme.sudxGoldFg
import com.example.ui.theme.sudxGoldTintBg
import com.example.ui.theme.sudxNavyFg
import com.example.util.PdfTextbookGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

private class PdfRenderSession(
    private val pfd: ParcelFileDescriptor,
    private val renderer: PdfRenderer,
    val pageSizes: List<Pair<Int, Int>>
) {
    val pageCount: Int get() = pageSizes.size
    private val mutex = Mutex()
    @Volatile
    private var isClosed = false

    private val bitmapCache = object : LruCache<String, Bitmap>(36 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return (value.byteCount / 1024).coerceAtLeast(1)
        }
    }

    suspend fun renderPage(pageIndex: Int, scale: Float): Bitmap? {
        if (pageIndex !in pageSizes.indices || isClosed) return null
        val scaleKey = (scale * 10).roundToInt()
        val cacheKey = "${pageIndex}_$scaleKey"
        bitmapCache.get(cacheKey)?.let { cached ->
            if (!cached.isRecycled) return cached
        }

        return withContext(Dispatchers.IO) {
            mutex.withLock {
                if (isClosed) return@withLock null
                bitmapCache.get(cacheKey)?.let { cached ->
                    if (!cached.isRecycled) return@withLock cached
                }
                try {
                    renderer.openPage(pageIndex).use { page ->
                        val widthPx = (page.width * scale).roundToInt().coerceIn(180, 1600)
                        val heightPx = (page.height * scale).roundToInt().coerceIn(240, 2260)
                        val bmp = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
                        bmp.eraseColor(android.graphics.Color.WHITE)
                        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmapCache.put(cacheKey, bmp)
                        bmp
                    }
                } catch (_: Exception) {
                    null
                }
            }
        }
    }

    fun close() {
        isClosed = true
        try {
            bitmapCache.evictAll()
        } catch (_: Throwable) {
        }
        try {
            renderer.close()
        } catch (_: Throwable) {
        }
        try {
            pfd.close()
        } catch (_: Throwable) {
        }
    }
}

@Composable
fun PdfReaderScreen(
    textbook: Textbook,
    onBack: () -> Unit,
    onAskTeacherAboutBook: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var pdfFile by remember(textbook.id) { mutableStateOf<File?>(null) }
    var renderSession by remember(textbook.id) { mutableStateOf<PdfRenderSession?>(null) }
    var isLoading by remember(textbook.id) { mutableStateOf(true) }
    var errorMessage by remember(textbook.id) { mutableStateOf<String?>(null) }

    var isGridOverview by remember(textbook.id) { mutableStateOf(false) }
    var zoomFactor by remember(textbook.id) { mutableFloatStateOf(1.0f) }

    val activePageIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { targetUri ->
        val file = pdfFile
        if (targetUri != null && file != null) {
            val saved = PdfTextbookGenerator.copyPdfToUri(context, file, targetUri)
            val msg = if (saved) {
                "تم حفظ ملف PDF بنجاح في جهازك"
            } else {
                "تعذر حفظ ملف PDF، يرجى المحاولة مرة أخرى"
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(textbook.id) {
        isLoading = true
        errorMessage = null
        try {
            val loaded = withContext(Dispatchers.IO) {
                val relatedUnits = CurriculumData.subjects
                    .filter { it.id == textbook.subjectId || it.title == textbook.subjectTitle }
                    .flatMap { it.units }
                val file = PdfTextbookGenerator.generateOrGetPdfFile(context, textbook, relatedUnits)
                val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(pfd)
                val sizes = (0 until renderer.pageCount).map { idx ->
                    renderer.openPage(idx).use { page ->
                        page.width.coerceAtLeast(1) to page.height.coerceAtLeast(1)
                    }
                }
                file to PdfRenderSession(pfd, renderer, sizes)
            }
            pdfFile = loaded.first
            renderSession = loaded.second
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "تعذر توليد أو فتح ملف PDF"
        } finally {
            isLoading = false
        }
    }

    DisposableEffect(textbook.id) {
        onDispose {
            renderSession?.close()
            renderSession = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("pdf_reader_screen")
    ) {
        SudXHeaderSurface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("pdf_reader_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = SudXGoldTint
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SudXCrimson.copy(alpha = 0.22f))
                            .border(1.dp, SudXChampagneGold.copy(alpha = 0.7f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = textbook.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = SudXGoldTint,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val countText = renderSession?.let { "${it.pageCount} صفحة كاملة" }
                            ?: textbook.pdfFileName.ifBlank { "${textbook.id}.pdf" }
                        Text(
                            text = "${textbook.track.titleAr} • $countText",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE2E8F0),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = {
                            val file = pdfFile ?: return@IconButton
                            if (!PdfTextbookGenerator.sharePdf(context, file, textbook.title)) {
                                Toast.makeText(context, "تعذر مشاركة ملف PDF", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("pdf_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة PDF",
                            tint = SudXGoldTint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PremiumActionButton(
                        onClick = {
                            val defaultName = textbook.pdfFileName.ifBlank { "${textbook.id}.pdf" }
                            createDocumentLauncher.launch(defaultName)
                        },
                        containerColor = SudXChampagneGold,
                        contentColor = SudXMidnightNavy,
                        cornerRadius = 12.dp,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("pdf_download_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "حفظ PDF في الجهاز",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    PremiumOutlinedActionButton(
                        onClick = {
                            val file = pdfFile ?: return@PremiumOutlinedActionButton
                            if (!PdfTextbookGenerator.openPdfExternally(context, file)) {
                                Toast.makeText(
                                    context,
                                    "لا يوجد قارئ PDF خارجي مثبت، يمكنك قراءة الكتاب هنا مباشرة.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        borderColor = SudXChampagneGold.copy(alpha = 0.7f),
                        contentColor = SudXGoldTint,
                        cornerRadius = 12.dp,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("pdf_external_open_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = SudXChampagneGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تطبيق خارجي",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SudXGoldTint
                        )
                    }
                }
            }
        }

        // All-Pages Toolbar & Page Jump Strip when PDF is ready
        val session = renderSession
        if (!isLoading && errorMessage == null && session != null && session.pageCount > 0) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "صفحة ${(activePageIndex.coerceIn(0, session.pageCount - 1)) + 1} من ${session.pageCount}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = sudxNavyFg(),
                            modifier = Modifier.testTag("pdf_page_counter_label")
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (!isGridOverview) {
                                IconButton(
                                    onClick = { zoomFactor = (zoomFactor - 0.25f).coerceAtLeast(1.0f) },
                                    enabled = zoomFactor > 1.0f,
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("pdf_zoom_out_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ZoomOut,
                                        contentDescription = "تصغير الصفحة",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "${(zoomFactor * 100).roundToInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                IconButton(
                                    onClick = { zoomFactor = (zoomFactor + 0.25f).coerceAtMost(2.0f) },
                                    enabled = zoomFactor < 2.0f,
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("pdf_zoom_in_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ZoomIn,
                                        contentDescription = "تكبير الصفحة",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            FilterChip(
                                selected = isGridOverview,
                                onClick = { isGridOverview = !isGridOverview },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isGridOverview) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = if (isGridOverview) "قراءة متواصلة" else "كل الصفحات",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SudXRoyalNavy,
                                    selectedLabelColor = SudXGoldTint
                                ),
                                modifier = Modifier.testTag("pdf_view_mode_toggle")
                            )
                        }
                    }

                    if (!isGridOverview && session.pageCount > 1) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(session.pageCount) { pageIdx ->
                                val isCurrent = activePageIndex == pageIdx
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isCurrent) SudXRoyalNavy else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    border = BorderStroke(
                                        width = if (isCurrent) 1.4.dp else 1.dp,
                                        color = if (isCurrent) SudXChampagneGold else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .clickable {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(pageIdx)
                                            }
                                        }
                                        .testTag("pdf_jump_page_$pageIdx")
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "ص ${pageIdx + 1}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            color = if (isCurrent) SudXGoldTint else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = SudXRoyalNavy)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "جاري تجهيز جميع صفحات الكتاب بصيغة PDF...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "تعذر عرض ملف PDF",
                            style = MaterialTheme.typography.bodyMedium,
                            color = sudxCrimsonFg()
                        )
                    }
                }
                session != null && isGridOverview -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(148.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("pdf_all_pages_grid"),
                        contentPadding = PaddingValues(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(session.pageCount) { pageIdx ->
                            val isCurrent = activePageIndex == pageIdx
                            val (pw, ph) = session.pageSizes[pageIdx]
                            val aspectRatio = (pw.toFloat() / ph.toFloat()).coerceIn(0.55f, 1.5f)
                            var thumbBitmap by remember(textbook.id, pageIdx) { mutableStateOf<Bitmap?>(null) }

                            LaunchedEffect(textbook.id, pageIdx) {
                                thumbBitmap = session.renderPage(pageIdx, 0.7f)
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) SudXChampagneGold else MaterialTheme.colorScheme.outlineVariant
                                ),
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        isGridOverview = false
                                        coroutineScope.launch {
                                            listState.scrollToItem(pageIdx)
                                        }
                                    }
                                    .testTag("pdf_grid_page_$pageIdx")
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(aspectRatio)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val bmp = thumbBitmap
                                        if (bmp != null && !bmp.isRecycled) {
                                            Image(
                                                bitmap = bmp.asImageBitmap(),
                                                contentDescription = "معاينة صفحة ${pageIdx + 1}",
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(22.dp),
                                                strokeWidth = 2.dp,
                                                color = SudXRoyalNavy
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (isCurrent) sudxGoldTintBg() else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        val label = when (pageIdx) {
                                            0 -> "ص 1 • الغلاف والفهرس"
                                            1 -> "ص 2 • الملخص التنفيذي"
                                            else -> "صفحة ${pageIdx + 1} من ${session.pageCount}"
                                        }
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) sudxGoldFg() else MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                session != null -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val basePageWidth = (maxWidth - 28.dp).coerceAtLeast(240.dp)
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("pdf_all_pages_list"),
                            contentPadding = PaddingValues(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(session.pageCount) { pageIdx ->
                                val (pw, ph) = session.pageSizes[pageIdx]
                                val aspectRatio = (pw.toFloat() / ph.toFloat()).coerceIn(0.55f, 1.5f)
                                val renderScale = (1.6f * zoomFactor).coerceIn(1.5f, 2.6f)
                                var pageBitmap by remember(textbook.id, pageIdx, (renderScale * 10).roundToInt()) {
                                    mutableStateOf<Bitmap?>(null)
                                }

                                LaunchedEffect(textbook.id, pageIdx, (renderScale * 10).roundToInt()) {
                                    pageBitmap = session.renderPage(pageIdx, renderScale)
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    shadowElevation = 6.dp,
                                    border = BorderStroke(1.dp, sudxGoldBorder()),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        val horizScroll = rememberScrollState()
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .then(
                                                    if (zoomFactor > 1.0f) Modifier.horizontalScroll(horizScroll)
                                                    else Modifier
                                                )
                                                .background(Color.White),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val targetWidth = basePageWidth * zoomFactor
                                            val bmp = pageBitmap
                                            if (bmp != null && !bmp.isRecycled) {
                                                Image(
                                                    bitmap = bmp.asImageBitmap(),
                                                    contentDescription = "صفحة ${pageIdx + 1} من ${textbook.title}",
                                                    contentScale = ContentScale.FillWidth,
                                                    modifier = if (zoomFactor > 1.0f) {
                                                        Modifier.width(targetWidth)
                                                    } else {
                                                        Modifier.fillMaxWidth()
                                                    }
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(aspectRatio),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(28.dp),
                                                        color = SudXRoyalNavy
                                                    )
                                                }
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(SudXMidnightNavy.copy(alpha = 0.08f))
                                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                        ) {
                                            Text(
                                                text = "صفحة ${pageIdx + 1} من ${session.pageCount}  •  ${textbook.title}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SudXMidnightNavy,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                PremiumActionButton(
                                    onClick = {
                                        onAskTeacherAboutBook("يا أستاذ، أريد تلخيصاً وشرحاً لأهم فصول ${textbook.title}.")
                                    },
                                    containerColor = SudXRoyalNavy,
                                    contentColor = SudXGoldTint,
                                    cornerRadius = 14.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SudXChampagneGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "اسأل المعلم الذكي عن هذا الكتاب",
                                        fontWeight = FontWeight.Bold
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
