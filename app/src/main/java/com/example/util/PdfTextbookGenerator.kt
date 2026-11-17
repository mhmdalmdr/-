package com.example.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.data.CurriculumData
import com.example.model.CurriculumUnit
import com.example.model.PastExam
import com.example.model.Textbook
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private const val TAG = "PdfTextbookGenerator"
private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842
private const val MARGIN_H = 42f
private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN_H * 2)

object PdfTextbookGenerator {

    private fun resolveArabicTypeface(context: Context, bold: Boolean): Typeface {
        return try {
            val resId = if (bold) R.font.cairo else R.font.tajawal
            ResourcesCompat.getFont(context, resId) ?: Typeface.create(
                Typeface.SANS_SERIF,
                if (bold) Typeface.BOLD else Typeface.NORMAL
            )
        } catch (_: Exception) {
            Typeface.create(Typeface.SANS_SERIF, if (bold) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    fun getPdfCacheDir(context: Context): File {
        val dir = File(context.filesDir, "textbooks_v2")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun generateOrGetPdfFile(
        context: Context,
        textbook: Textbook,
        relatedUnits: List<CurriculumUnit>,
        relatedExams: List<PastExam> = CurriculumData.pastExams.filter {
            it.subjectTitle.contains(textbook.subjectTitle, ignoreCase = true) ||
                textbook.subjectTitle.contains(it.subjectTitle, ignoreCase = true)
        }
    ): File {
        val safeFileName = textbook.pdfFileName
            .ifBlank { "${textbook.id}.pdf" }
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
        val outFile = File(getPdfCacheDir(context), safeFileName)
        if (outFile.exists() && outFile.length() > 1024L) {
            return outFile
        }

        if (tryRenderWithAndroidPdfDocument(context, outFile, textbook, relatedUnits, relatedExams)) {
            return outFile
        }

        writeMinimalPdfFallback(outFile, textbook, relatedUnits)
        return outFile
    }

    private fun tryRenderWithAndroidPdfDocument(
        context: Context,
        outFile: File,
        textbook: Textbook,
        relatedUnits: List<CurriculumUnit>,
        relatedExams: List<PastExam>
    ): Boolean {
        var document: PdfDocument? = null
        return try {
            val doc = PdfDocument()
            document = doc

            val regularTypeface = resolveArabicTypeface(context, bold = false)
            val boldTypeface = resolveArabicTypeface(context, bold = true)

            val navyColor = Color.rgb(11, 19, 43)
            val royalNavyColor = Color.rgb(22, 33, 62)
            val goldColor = Color.rgb(212, 175, 55)
            val softGoldBg = Color.rgb(250, 244, 228)
            val softNavyBg = Color.rgb(241, 245, 249)
            val bodyDark = Color.rgb(30, 41, 59)
            val mutedGray = Color.rgb(100, 116, 139)

            val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = boldTypeface
                textSize = 20f
                color = goldColor
            }
            val headingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = boldTypeface
                textSize = 15f
                color = royalNavyColor
            }
            val subHeadingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = boldTypeface
                textSize = 13f
                color = Color.rgb(154, 114, 20)
            }
            val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = regularTypeface
                textSize = 11.5f
                color = bodyDark
            }
            val coverBodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = regularTypeface
                textSize = 12f
                color = Color.rgb(226, 232, 240)
            }
            val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = regularTypeface
                textSize = 9.5f
                color = mutedGray
            }
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
            }

            var pageNumber = 1
            var currentPage = doc.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            )
            var canvas = currentPage.canvas

            fun drawFooter(pageNum: Int) {
                strokePaint.color = goldColor
                canvas.drawLine(MARGIN_H, PAGE_HEIGHT - 44f, PAGE_WIDTH - MARGIN_H, PAGE_HEIGHT - 44f, strokePaint)
                val footerText = "منصة المعلم الذكي للشهادة الثانوية السودانية  |  ${textbook.title}  |  صفحة $pageNum"
                val layout = buildStaticLayout(footerText, footerPaint, CONTENT_WIDTH.toInt())
                canvas.save()
                canvas.translate(MARGIN_H, PAGE_HEIGHT - 36f)
                layout.draw(canvas)
                canvas.restore()
            }

            fun startNewPage(sectionLabel: String = textbook.title): Float {
                drawFooter(pageNumber)
                doc.finishPage(currentPage)
                pageNumber += 1
                currentPage = doc.startPage(
                    PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                )
                canvas = currentPage.canvas
                fillPaint.color = royalNavyColor
                canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 38f, fillPaint)
                fillPaint.color = goldColor
                canvas.drawRect(0f, 38f, PAGE_WIDTH.toFloat(), 40f, fillPaint)

                val headerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = boldTypeface
                    textSize = 10f
                    color = Color.rgb(250, 243, 224)
                }
                val headerLayout = buildStaticLayout(sectionLabel, headerPaint, CONTENT_WIDTH.toInt())
                canvas.save()
                canvas.translate(MARGIN_H, 11f)
                headerLayout.draw(canvas)
                canvas.restore()
                return 58f
            }

            fun drawBlock(
                text: String,
                paint: TextPaint,
                cursorY: Float,
                extraSpace: Float = 8f,
                pageSectionHeader: String = textbook.title
            ): Float {
                if (text.isBlank()) return cursorY
                val layout = buildStaticLayout(text, paint, CONTENT_WIDTH.toInt())
                var y = cursorY
                if (y + layout.height > PAGE_HEIGHT - 58f) {
                    y = startNewPage(pageSectionHeader)
                }
                canvas.save()
                canvas.translate(MARGIN_H, y)
                layout.draw(canvas)
                canvas.restore()
                return y + layout.height + extraSpace
            }

            // --- Page 1: Cover & Full Table of Contents ---
            fillPaint.color = navyColor
            canvas.drawRoundRect(
                RectF(MARGIN_H - 6f, 36f, PAGE_WIDTH - MARGIN_H + 6f, 246f),
                16f,
                16f,
                fillPaint
            )
            strokePaint.color = goldColor
            canvas.drawRoundRect(
                RectF(MARGIN_H - 6f, 36f, PAGE_WIDTH - MARGIN_H + 6f, 246f),
                16f,
                16f,
                strokePaint
            )

            var y = 56f
            y = drawBlock(
                "المعلم الذكي • مكتبة الشهادة الثانوية السودانية (PDF)",
                subHeadingPaint.apply { color = goldColor },
                y,
                6f
            )
            subHeadingPaint.color = Color.rgb(154, 114, 20)
            y = drawBlock(textbook.title, titlePaint, y, 8f)
            y = drawBlock(
                "المادة: ${textbook.subjectTitle}  •  المسار: ${textbook.track.titleAr}  •  ${textbook.grade}",
                coverBodyPaint,
                y,
                6f
            )
            y = drawBlock(
                "عدد الفصول: ${textbook.chaptersCount}  •  المرجع الوزاري (${textbook.pagesCount} صفحة)",
                coverBodyPaint,
                y,
                10f
            )
            y = drawBlock(textbook.description, coverBodyPaint, y, 22f)

            y = maxOf(y, 268f)

            fillPaint.color = softGoldBg
            canvas.drawRoundRect(
                RectF(MARGIN_H - 4f, y - 6f, PAGE_WIDTH - MARGIN_H + 4f, y + 26f),
                8f,
                8f,
                fillPaint
            )
            y = drawBlock("فهرس أبواب ووحدات الكتاب الكامل:", headingPaint, y, 10f)
            textbook.chapters.forEachIndexed { index, chapter ->
                y = drawBlock("الفصل ${index + 1}: $chapter", bodyPaint, y, 6f)
            }
            if (relatedExams.isNotEmpty() || relatedUnits.any { it.sampleQuestions.isNotEmpty() }) {
                y = drawBlock("الملحق الختامي: بنك الأسئلة والتدريبات والامتحانات النموذجية", subHeadingPaint, y, 6f)
            }

            // --- Page 2: Executive Summaries & Study Strategy ---
            y = startNewPage("${textbook.title} • الملخص التنفيذي")
            fillPaint.color = softGoldBg
            canvas.drawRoundRect(
                RectF(MARGIN_H - 4f, y - 6f, PAGE_WIDTH - MARGIN_H + 4f, y + 26f),
                8f,
                8f,
                fillPaint
            )
            y = drawBlock("أهم المحاور والمفاهيم المطلوبة لامتحان الشهادة:", headingPaint, y, 10f)
            if (textbook.keySummaries.isNotEmpty()) {
                textbook.keySummaries.forEachIndexed { idx, summary ->
                    y = drawBlock("${idx + 1}. $summary", bodyPaint, y, 8f, "${textbook.title} • الملخص التنفيذي")
                }
            } else {
                y = drawBlock(
                    "يغطي هذا المرجع كامل أبواب ${textbook.subjectTitle} المقررة وفق مواصفات وزارة التربية والتعليم.",
                    bodyPaint,
                    y,
                    8f,
                    "${textbook.title} • الملخص التنفيذي"
                )
            }

            y += 8f
            y = drawBlock("خطة المذاكرة الفعّالة لهذا الكتاب:", headingPaint, y, 8f, "${textbook.title} • الملخص التنفيذي")
            val studyGuidelines = listOf(
                "ابدأ بقراءة فهرس كل فصل لفهم الترابط بين الموضوعات والقوانين الأساسية.",
                "دوّن التعاريف والمعادلات والأفكار المتكررة في دفتر الملاحظات ثم راجعها بمؤقت بومودورو.",
                "طبّق فوراً بحل الأسئلة التدريبية ونماذج 2026م في الصفحات الختامية من الكتاب."
            )
            studyGuidelines.forEachIndexed { i, guide ->
                y = drawBlock("• الخطوة ${i + 1}: $guide", bodyPaint, y, 6f, "${textbook.title} • الملخص التنفيذي")
            }

            // --- Dedicated Pages for EVERY Chapter in Textbook ---
            val usedUnitIds = mutableSetOf<String>()
            val chaptersList = textbook.chapters.ifEmpty { listOf(textbook.title) }
            chaptersList.forEachIndexed { chapIdx, chapterTitle ->
                val sectionHeader = "${textbook.title} • الفصل ${chapIdx + 1}"
                y = startNewPage(sectionHeader)

                fillPaint.color = softNavyBg
                canvas.drawRoundRect(
                    RectF(MARGIN_H - 4f, y - 6f, PAGE_WIDTH - MARGIN_H + 4f, y + 48f),
                    10f,
                    10f,
                    fillPaint
                )
                strokePaint.color = goldColor
                canvas.drawRoundRect(
                    RectF(MARGIN_H - 4f, y - 6f, PAGE_WIDTH - MARGIN_H + 4f, y + 48f),
                    10f,
                    10f,
                    strokePaint
                )

                y = drawBlock("الفصل (${chapIdx + 1} من ${chaptersList.size})", subHeadingPaint, y, 4f, sectionHeader)
                y = drawBlock(chapterTitle, headingPaint, y, 12f, sectionHeader)

                // Match curriculum unit by keyword overlap or chapter index
                val matchedUnit = relatedUnits.firstOrNull { unit ->
                    !usedUnitIds.contains(unit.id) && (
                        chapterTitle.contains(unit.title.take(10), ignoreCase = true) ||
                            unit.title.contains(chapterTitle.substringAfter(":").trim().take(10), ignoreCase = true)
                        )
                } ?: relatedUnits.getOrNull(chapIdx)?.takeIf { !usedUnitIds.contains(it.id) }

                if (matchedUnit != null) {
                    usedUnitIds.add(matchedUnit.id)
                    y = drawBlock("الشرح التفصيلي — ${matchedUnit.title}", subHeadingPaint, y, 6f, sectionHeader)
                    if (matchedUnit.keyPoints.isNotEmpty()) {
                        y = drawBlock("النقاط المحورية للفصل:", headingPaint, y, 4f, sectionHeader)
                        matchedUnit.keyPoints.forEachIndexed { ptIdx, pt ->
                            y = drawBlock("${ptIdx + 1}) $pt", bodyPaint, y, 5f, sectionHeader)
                        }
                    }
                    if (matchedUnit.fullContent.isNotBlank()) {
                        y += 4f
                        y = drawBlock("المحتوى العلمي والشرح المنهجي:", headingPaint, y, 6f, sectionHeader)
                        matchedUnit.fullContent.split("\n\n").forEach { paragraph ->
                            y = drawBlock(paragraph.trim(), bodyPaint, y, 7f, sectionHeader)
                        }
                    }
                    if (matchedUnit.examFocus.isNotBlank()) {
                        y += 4f
                        y = drawBlock("ملاحظة واضع الامتحان: ${matchedUnit.examFocus}", subHeadingPaint, y, 8f, sectionHeader)
                    }
                } else {
                    // Rich structured coverage when unit has condensed data
                    val cleanChapterTopic = chapterTitle.substringAfter(":").trim().ifBlank { chapterTitle }
                    y = drawBlock("1. أهداف التعلم في هذا الفصل:", headingPaint, y, 6f, sectionHeader)
                    y = drawBlock(
                        "يتناول هذا الفصل محاور «$cleanChapterTopic» ضمن مقرر ${textbook.subjectTitle} (${textbook.grade}) بهدف إكساب الطالب فهماً متدرجاً للمفاهيم والقدرة على حل مسائل الشهادة الثانوية بدقة.",
                        bodyPaint,
                        y,
                        10f,
                        sectionHeader
                    )

                    y = drawBlock("2. المفاهيم والمرتكزات الأساسية:", headingPaint, y, 6f, sectionHeader)
                    val matchedSummary = textbook.keySummaries.getOrNull(chapIdx % textbook.keySummaries.size.coerceAtLeast(1))
                    if (!matchedSummary.isNullOrBlank()) {
                        y = drawBlock("• المحور المركزي: $matchedSummary", bodyPaint, y, 6f, sectionHeader)
                    }
                    y = drawBlock(
                        "• الترابط المنهجي: احرص عند دراسة «$cleanChapterTopic» على ربط التعاريف النظرية بالتطبيقات العملية والمعادلات أو الشواهد النصية المطلوبة في ورقة الامتحان.",
                        bodyPaint,
                        y,
                        8f,
                        sectionHeader
                    )

                    val examKeyTopics = relatedExams.flatMap { it.keyTopics }
                    if (examKeyTopics.isNotEmpty()) {
                        y = drawBlock("3. تركيز امتحانات الشهادة المرتبط بهذا الجزء:", headingPaint, y, 6f, sectionHeader)
                        examKeyTopics.forEach { topic ->
                            y = drawBlock("   - $topic", bodyPaint, y, 4f, sectionHeader)
                        }
                    }

                    y += 6f
                    y = drawBlock("4. قائمة التحقق قبل الانتقال للفصل التالي:", headingPaint, y, 6f, sectionHeader)
                    val checklist = listOf(
                        "استيعاب جميع المصطلحات والتعاريف الأساسية في $cleanChapterTopic.",
                        "القدرة على التفسير والتعليل وذكر الأمثلة المنهجية بدقة.",
                        "تطبيق المهارات في اختبارات التدريب الحر باستخدام تطبيق المعلم الذكي."
                    )
                    checklist.forEach { check ->
                        y = drawBlock("☑ $check", bodyPaint, y, 5f, sectionHeader)
                    }
                }
            }

            // Remaining unmatched CurriculumUnits if any
            relatedUnits.filter { !usedUnitIds.contains(it.id) }.forEach { unit ->
                val sectionHeader = "${textbook.title} • ${unit.title}"
                y = startNewPage(sectionHeader)
                y = drawBlock("وحدة إضافية: ${unit.title}", headingPaint, y, 4f, sectionHeader)
                y = drawBlock(unit.subtitle, subHeadingPaint, y, 8f, sectionHeader)
                if (unit.keyPoints.isNotEmpty()) {
                    y = drawBlock("النقاط المحورية:", subHeadingPaint, y, 4f, sectionHeader)
                    unit.keyPoints.forEachIndexed { idx, pt ->
                        y = drawBlock("${idx + 1}) $pt", bodyPaint, y, 4f, sectionHeader)
                    }
                }
                if (unit.fullContent.isNotBlank()) {
                    y += 4f
                    unit.fullContent.split("\n\n").forEach { paragraph ->
                        y = drawBlock(paragraph.trim(), bodyPaint, y, 6f, sectionHeader)
                    }
                }
                if (unit.examFocus.isNotBlank()) {
                    y += 4f
                    y = drawBlock("توجيهات واضع الامتحان: ${unit.examFocus}", subHeadingPaint, y, 8f, sectionHeader)
                }
            }

            // --- Final Pages: Model Questions & Past Exam Practice ---
            val allQuestions = relatedUnits.flatMap { it.sampleQuestions } +
                relatedExams.flatMap { it.sampleQuestions }
            if (allQuestions.isNotEmpty()) {
                val qaSection = "${textbook.title} • بنك الأسئلة والإجابات"
                y = startNewPage(qaSection)
                fillPaint.color = softGoldBg
                canvas.drawRoundRect(
                    RectF(MARGIN_H - 4f, y - 6f, PAGE_WIDTH - MARGIN_H + 4f, y + 26f),
                    8f,
                    8f,
                    fillPaint
                )
                y = drawBlock("بنك الأسئلة النموذجية والحلول المنهجية:", headingPaint, y, 10f, qaSection)
                allQuestions.distinctBy { it.question }.forEachIndexed { qIdx, q ->
                    y = drawBlock("س${qIdx + 1}: ${q.question}", subHeadingPaint, y, 4f, qaSection)
                    q.options.forEachIndexed { optIdx, opt ->
                        val marker = if (optIdx == q.correctIndex) "✓" else "•"
                        y = drawBlock("   $marker $opt", bodyPaint, y, 2f, qaSection)
                    }
                    if (q.explanation.isNotBlank()) {
                        y = drawBlock("   التوضيح: ${q.explanation}", bodyPaint, y, 8f, qaSection)
                    }
                }
            }

            drawFooter(pageNumber)
            doc.finishPage(currentPage)

            FileOutputStream(outFile).use { fos ->
                doc.writeTo(fos)
            }
            true
        } catch (t: Throwable) {
            Log.w(TAG, "PdfDocument rendering unavailable in runtime; falling back to multi-page PDF-1.4 writer", t)
            false
        } finally {
            try {
                document?.close()
            } catch (_: Throwable) {
            }
        }
    }

    private fun writeMinimalPdfFallback(
        outFile: File,
        textbook: Textbook,
        relatedUnits: List<CurriculumUnit>
    ) {
        val chapters = textbook.chapters.ifEmpty { listOf("Chapter 1") }
        val totalPages = 1 + chapters.size
        // Object numbers:
        // 1: Catalog
        // 2: Pages
        // 3: Font F1
        // For each page i in 0 until totalPages:
        //   pageObj = 4 + i * 2
        //   contentObj = 5 + i * 2
        val kidsRefs = (0 until totalPages).joinToString(" ") { i -> "${4 + i * 2} 0 R" }
        val objects = mutableListOf(
            "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n",
            "2 0 obj\n<< /Type /Pages /Kids [$kidsRefs] /Count $totalPages >>\nendobj\n",
            "3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n"
        )

        for (pageIdx in 0 until totalPages) {
            val pageObjNum = 4 + pageIdx * 2
            val contentObjNum = 5 + pageIdx * 2
            val streamBody = if (pageIdx == 0) {
                buildString {
                    append("BT\n/F1 16 Tf\n50 780 Td\n")
                    append("(Al-Muallim Al-Thaki Sudan Secondary Certificate PDF Library) Tj\n")
                    append("0 -24 Td\n/F1 12 Tf\n")
                    append("(Textbook ID: ${escapePdfAscii(textbook.id)} | Track: ${escapePdfAscii(textbook.track.name)}) Tj\n")
                    append("0 -18 Td\n")
                    append("(File: ${escapePdfAscii(textbook.pdfFileName)} | Chapters: ${chapters.size} | Total Pages: $totalPages) Tj\n")
                    append("0 -18 Td\n")
                    append("(Units: ${relatedUnits.size} | Summaries: ${textbook.keySummaries.size}) Tj\n")
                    append("ET\n")
                }
            } else {
                val chapterIndex = pageIdx - 1
                buildString {
                    append("BT\n/F1 15 Tf\n50 780 Td\n")
                    append("(Page ${pageIdx + 1} of $totalPages - Chapter ${chapterIndex + 1}) Tj\n")
                    append("0 -22 Td\n/F1 11 Tf\n")
                    append("(Book: ${escapePdfAscii(textbook.id)} | Track: ${escapePdfAscii(textbook.track.name)}) Tj\n")
                    append("0 -18 Td\n")
                    append("(Chapter Section ${chapterIndex + 1} Study Notes & Review Checklist) Tj\n")
                    append("ET\n")
                }
            }
            val streamBytes = streamBody.toByteArray(Charsets.US_ASCII)
            objects.add(
                "$pageObjNum 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 3 0 R >> >> /Contents $contentObjNum 0 R >>\nendobj\n"
            )
            objects.add(
                "$contentObjNum 0 obj\n<< /Length ${streamBytes.size} >>\nstream\n$streamBody\nendstream\nendobj\n"
            )
        }

        val pdfBytes = ByteArrayOutputStream()
        pdfBytes.write("%PDF-1.4\n".toByteArray(Charsets.US_ASCII))
        val offsets = mutableListOf<Int>()
        for (obj in objects) {
            offsets.add(pdfBytes.size())
            pdfBytes.write(obj.toByteArray(Charsets.US_ASCII))
        }
        val xrefStart = pdfBytes.size()
        val xref = buildString {
            append("xref\n")
            append("0 ${objects.size + 1}\n")
            append("0000000000 65535 f \n")
            for (offset in offsets) {
                append(String.format(java.util.Locale.US, "%010d 00000 n \n", offset))
            }
            append("trailer\n")
            append("<< /Size ${objects.size + 1} /Root 1 0 R >>\n")
            append("startxref\n")
            append("$xrefStart\n")
            append("%%EOF\n")
        }
        pdfBytes.write(xref.toByteArray(Charsets.US_ASCII))

        FileOutputStream(outFile).use { fos ->
            fos.write(pdfBytes.toByteArray())
        }
    }

    private fun escapePdfAscii(input: String): String {
        return input
            .filter { it.code in 32..126 }
            .replace("\\", "\\\\")
            .replace("(", "\\(")
            .replace(")", "\\)")
    }

    private fun buildStaticLayout(text: String, paint: TextPaint, widthPx: Int): StaticLayout {
        return StaticLayout.Builder
            .obtain(text, 0, text.length, paint, widthPx.coerceAtLeast(1))
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(2f, 1.15f)
            .setIncludePad(false)
            .build()
    }

    fun copyPdfToUri(context: Context, pdfFile: File, targetUri: Uri): Boolean {
        return try {
            context.contentResolver.openOutputStream(targetUri)?.use { out ->
                pdfFile.inputStream().use { input ->
                    input.copyTo(out)
                }
            } != null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export PDF to URI", e)
            false
        }
    }

    fun getShareableUri(context: Context, pdfFile: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
    }

    fun sharePdf(context: Context, pdfFile: File, bookTitle: String): Boolean {
        return try {
            val uri = getShareableUri(context, pdfFile)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, bookTitle)
                putExtra(Intent.EXTRA_TEXT, "كتاب $bookTitle بصيغة PDF من منصة المعلم الذكي للشهادة الثانوية السودانية")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "مشاركة كتاب PDF"))
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share PDF", e)
            false
        }
    }

    fun openPdfExternally(context: Context, pdfFile: File): Boolean {
        return try {
            val uri = getShareableUri(context, pdfFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "فتح ملف PDF"))
            true
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "No external PDF viewer found", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open external PDF viewer", e)
            false
        }
    }
}
